package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.model.response.ChapterListResponse
import com.yuenov.kotlin.open.network.apiService
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.demo.app.network.stateCallback.UpdateUiState
import me.hgj.jetpackmvvm.ext.request

/**
 * 用来实现一些公用的数据操作
 */
class CommonViewModel : BaseViewModel() {

    var updateChapterListState: MutableLiveData<UpdateUiState<ChapterListResponse>> = MutableLiveData()

    fun updateChapterList(bookId: Int, isShowLoading: Boolean) {
        var chapterId = 0L;
        request(
            {
                logd(CLASS_TAG, "updateChapterList")
                val bookChapter = appDb.chapterDao.getLastChapter(bookId)
                chapterId = bookChapter?.chapterId ?: 0L
                val v = bookChapter?.v ?: 0
                apiService.getChapterByBookId(bookId, chapterId, v)
            },
            { response ->
                logd(CLASS_TAG, "updateChapterList onSuccess")
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
                    isSuccess = true,
                    errorMsg = it.errorMsg,
                )
            }, isShowLoading
        )
    }
}