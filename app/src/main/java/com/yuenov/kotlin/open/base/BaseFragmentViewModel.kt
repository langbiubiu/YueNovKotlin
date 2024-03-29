package com.yuenov.kotlin.open.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yuenov.kotlin.open.application.singleThreadPoolExecutor
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbCache
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.model.request.DownloadChapterRequest
import com.yuenov.kotlin.open.model.response.*
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.network.AppException
import me.hgj.jetpackmvvm.network.BaseResponse
import me.hgj.jetpackmvvm.state.ResultState

/**
 * ViewModel的基类，用来实现公用的数据操作
 */
open class BaseFragmentViewModel : BaseViewModel() {

    var updateChapterListState: MutableLiveData<UpdateUiState<ChapterListResponse>> =
        MutableLiveData()
    var downloadChapterContentState: MutableLiveData<ListDataUiState<ChapterInfoItem>> =
        MutableLiveData()
    var getBookListByCategoryIdState: MutableLiveData<UpdateUiState<BookListResponse>> =
        MutableLiveData()

    private val defaultApiDelayTime = 12 * 1000L

    fun <T> requestDelay(
        block: suspend () -> BaseResponse<T>,
        success: (T) -> Unit,
        error: (AppException) -> Unit = {},
        isShowDialog: Boolean = false,
        loadingMessage: String = "请求网络中..."
    ) {
        // 因为接口限制，每次网络请求后都需要至少等待12秒，保证接口能请求成功
        // 利用单线程池异步执行请求，保证请求的时间间隔
        // singleThreadPoolExecutor是一个全局的单线程池
        viewModelScope.launch(singleThreadPoolExecutor.asCoroutineDispatcher()) {
            runBlocking { request(block, success, error, isShowDialog, loadingMessage).join() }
            Thread.sleep(defaultApiDelayTime)
        }
    }

    fun <T> requestDelay(
        block: suspend () -> BaseResponse<T>,
        resultState: MutableLiveData<ResultState<T>>,
        isShowDialog: Boolean = false,
        loadingMessage: String = "请求网络中..."
    ) {
        // 因为接口限制，每次网络请求后都需要等待12秒，保证接口能请求成功
        viewModelScope.launch(singleThreadPoolExecutor.asCoroutineDispatcher()) {
            runBlocking { request(block, resultState, isShowDialog, loadingMessage).join() }
            Thread.sleep(defaultApiDelayTime)
        }
    }

    /**
     * 更新章节列表
     */
    fun updateChapterList(bookId: Int, isShowLoading: Boolean) {
        var chapterId = 0L
        requestDelay(
            {
                logD(CLASS_TAG, "updateChapterList")
                val bookChapter = appDb.chapterDao.getLastChapter(bookId)
                chapterId = bookChapter?.chapterId ?: 0L
                val v = bookChapter?.v ?: 0
                apiService.getChapterByBookId(bookId, chapterId, v)
            },
            { response ->
                logD(CLASS_TAG, "updateChapterList onSuccess")
                if (!response.chapters.isNullOrEmpty()) {
                    // 接口会返回请求参数中的章节 由于该章节本地已存在，所以从返回结果中删掉此章节
                    if (chapterId > 0) {
                        for ((index, chapter) in response.chapters!!.withIndex()) {
                            if (chapter.id == chapterId) {
                                response.chapters!!.removeAt(index)
                                break
                            }
                        }
                    }
                    appDb.chapterDao.addChapter(bookId, response.chapters!!)
                }
                updateChapterListState.value = UpdateUiState(true, response)
            },
            {
                updateChapterListState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg,
                )
            },
            isShowDialog = isShowLoading
        )
    }

    fun downloadChapterContent(bookId: Int, chapterId: Long, v: Int?, isShowLoading: Boolean) {
        requestDelay(
            {
                logD(CLASS_TAG, "downloadChapterContent")
                // 如果已下载过，就不需要重新下载
                val chapter = appDb.chapterDao.getEntity(bookId, chapterId)
                if (!chapter?.content.isNullOrEmpty()) {
                    return@requestDelay ApiResponse(ResponseResult(), DownloadChapterListResponse())
                }
                val request = DownloadChapterRequest(bookId, listOf(chapterId), v)
                apiService.downloadChapter(request)
            },
            { resp ->
                if (resp.list.isNullOrEmpty()) {
                    downloadChapterContentState.value = ListDataUiState(
                        isSuccess = true,
                        isEmpty = true,
                        errMessage = "list empty"
                    )
                } else {
                    appDb.chapterDao.addContent(bookId, resp.list!!)
                    downloadChapterContentState.value = ListDataUiState(
                        isSuccess = true,
                        isEmpty = false,
                        listData = resp.list!!
                    )
                }
            },
            {
                // 书源失效，需要更新，重新请求 errorMsg返回的就是新的v值
                if (it.errCode == ResponseResult.INVALID_SOURCE) {
                    downloadChapterContent(bookId, chapterId, it.errorMsg.toInt(), isShowLoading)
                }
                downloadChapterContentState.value = ListDataUiState(
                    isSuccess = false,
                    isEmpty = true,
                    errMessage = it.errorMsg
                )
            }, isShowLoading
        )
    }

    fun getBookListByCategoryId(
        pageNum: Int,
        pageSize: Int,
        categoryId: Int,
        channelId: Int?,
        orderBy: String?
    ) {
        requestDelay(
            { apiService.getCategoryId(pageNum, pageSize, categoryId, channelId, orderBy) },
            {
                getBookListByCategoryIdState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                getBookListByCategoryIdState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            })
    }

    fun getCacheContent(type: String): String {
        val cache = appDb.cacheDao.getEntity(type)
        return if (cache != null && !cache.cContent.isNullOrEmpty()) cache.cContent!! else ""
    }

    fun setCacheContent(type: String, content: String) {
        if (type.isEmpty() || content.isEmpty()) return
        appDb.cacheDao.addOrUpdate(TbCache(type, content))
    }
}