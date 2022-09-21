package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState
import me.hgj.jetpackmvvm.ext.launch

class SearchFragmentViewModel : BaseFragmentViewModel() {

    var searchBookState: MutableLiveData<UpdateUiState<BookListResponse>> = MutableLiveData()

    fun searchBook(keyWord: String, pageNum: Int, pageSize: Int) {
        requestDelay(
            { apiService.searchBook(keyWord, pageNum, pageSize) },
            {
                searchBookState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                searchBookState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            })
    }

    fun addBookShelf(bookInfo: BookBaseInfo) {
        launch(
            {
                appDb.bookShelfDao.addOrUpdate(
                    TbBookShelf(
                        bookInfo.bookId,
                        bookInfo.title,
                        bookInfo.coverImg,
                        bookInfo.author,
                        false,
                        System.currentTimeMillis()
                    )
                )
            }, {}, {}
        )
    }
}