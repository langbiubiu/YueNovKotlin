package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.model.response.CategoryEndListResponse
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState

class CategoryEndFragmentViewModel: BaseFragmentViewModel() {

    var getCategoryEndState: MutableLiveData<UpdateUiState<CategoryEndListResponse>> = MutableLiveData()
    var getCategoryEndBookListState: MutableLiveData<Pair<String, BookListResponse>?> = MutableLiveData()

    fun getCategoryEnd(pageNum: Int, pageSize: Int) {
        requestDelay(
            { apiService.getCategoryEnd(pageNum, pageSize) },
            {
                getCategoryEndState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                getCategoryEndState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            })
    }

    fun getCategoryEndBookList(
        pageNum: Int,
        pageSize: Int,
        categoryName: String,
        categoryId: Int,
    ) {
        requestDelay(
            { apiService.getCategoryId(pageNum, pageSize, categoryId, null, "END") },
            { getCategoryEndBookListState.value = Pair(categoryName, it) },
            { getCategoryEndBookListState.value = null }
        )
    }
}