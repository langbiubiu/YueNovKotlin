package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.database.tb.TbReadHistory
import com.yuenov.kotlin.open.model.response.BookDetailInfoResponse
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.network.apiService
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.demo.app.network.stateCallback.UpdateUiState
import me.hgj.jetpackmvvm.ext.launch
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

class DetailViewModel: BaseViewModel() {

    val bookDetailDataState: MutableLiveData<ResultState<BookDetailInfoResponse>> = MutableLiveData()

    val hasReadRecordState: BooleanLiveData = BooleanLiveData()

    val hasBookShelfState: BooleanLiveData = BooleanLiveData()

    val hasChapterState: BooleanLiveData = BooleanLiveData()

    val addOrRemoveBookShelfState: BooleanLiveData = BooleanLiveData()

    val getRecommendListState: MutableLiveData<UpdateUiState<BookListResponse>> = MutableLiveData()

    fun requestBookDetail(bookId: Int) {
        request(
            { apiService.getDetail(bookId) },
            bookDetailDataState,
            isShowDialog = true,
        )
    }

    //是否有阅读记录
    fun hasReadRecord(bookId: Int) {
        launch(
            { appDb.readHistoryDao.existsRealRead(bookId) },
            { hasReadRecordState.value = it }
        )
    }

    //是否在书架中
    fun hasBookShelf(bookId: Int) {
        launch(
            { appDb.bookShelfDao.exists(bookId) },
            { hasBookShelfState.value = it }
        )
    }

    //是否有章节信息
    fun hasChapter(bookId: Int) {
        launch(
            { appDb.chapterDao.getFirstChapter(bookId) },
            { hasChapterState.value = it != null }
        )
    }

    fun addReadHistory(response: BookDetailInfoResponse) {
        launch(
            {
                response.apply {
                    val readHistory = TbReadHistory(
                        bookId,
                        title,
                        0,
                        0,
                        coverImg,
                        author,
                        appDb.readHistoryDao.existsRealRead(bookId),
                        System.currentTimeMillis()
                    )
                    appDb.readHistoryDao.addOrUpdateByPreview(readHistory)
                }
            }, {})
    }

    fun addOrRemoveBookShelf(hasBookShelf: Boolean, bookInfo: BookBaseInfo) {
        launch(
            {
                if (hasBookShelf) {
                    //在书架上，则删除书架记录
                    appDb.bookShelfDao.deleteByBookId(bookInfo.bookId)
                    appDb.readHistoryDao.resetAddBookShelfStat(bookInfo.bookId, false)
                } else {
                    appDb.bookShelfDao.addOrUpdate(TbBookShelf(
                        bookInfo.bookId,
                        bookInfo.title,
                        bookInfo.coverImg,
                        bookInfo.author,
                        false,
                        System.currentTimeMillis()
                    ))
                    appDb.readHistoryDao.resetAddBookShelfStat(bookInfo.bookId, true)
                }
            },
            { addOrRemoveBookShelfState.value = true },
            { addOrRemoveBookShelfState.value = false}
        )
    }

    private var isReplacing:Boolean = false
    fun getRecommendList(bookId: Int, pageNum: Int, pageSize: Int) {
        if (isReplacing) return
        request(
            {
                isReplacing = true
                apiService.getRecommend(bookId, pageNum, pageSize)
            },
            {
                isReplacing = false
                getRecommendListState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                isReplacing = false
                getRecommendListState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            }
        )
    }

}