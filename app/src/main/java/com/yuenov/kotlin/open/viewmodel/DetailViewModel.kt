package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbReadHistory
import com.yuenov.kotlin.open.model.response.BookDetailInfoResponse
import com.yuenov.kotlin.open.network.apiService
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.launch
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

class DetailViewModel: BaseViewModel() {

    val bookDetailDataState: MutableLiveData<ResultState<BookDetailInfoResponse>> = MutableLiveData()

    val hasReadRecordState: MutableLiveData<Boolean> = MutableLiveData()

    val hasBookShelfState: MutableLiveData<Boolean> = MutableLiveData()

    val hasChapterState: MutableLiveData<Boolean> = MutableLiveData()

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
            { appDb.readHistoryDao.existsRealRead(bookId) },
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
                        null,
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
            },
            {

            })
    }

}