package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.model.response.CategoryInfoItem
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState

class DiscoverFragmentViewModel : BaseFragmentViewModel() {

    val getCategoryDiscoveryState: MutableLiveData<ListDataUiState<CategoryInfoItem>> =
        MutableLiveData()
    val getCategoryBookListState: MutableLiveData<Pair<String, BookListResponse>?> =
        MutableLiveData()

    fun getCategoryDiscovery() {
        requestDelay(
            {
                apiService.categoryDiscovery()
            },
            {
                getCategoryDiscoveryState.value = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it.list?.isEmpty() ?: true,
                    listData = it.list ?: listOf()
                )
            },
            {
                getCategoryDiscoveryState.value = ListDataUiState(
                    isSuccess = false,
                    isEmpty = true,
                    errMessage = it.errorMsg
                )
            })
    }

    fun getCategoryBookList(
        categoryName: String,
        categoryId: Int,
        pageNum: Int,
        pageSize: Int,
        type: String
    ) {
        requestDelay(
            { apiService.categoryDiscoveryAll(pageNum, pageSize, type, categoryId) },
            { getCategoryBookListState.value = Pair(categoryName, it) },
            { getCategoryBookListState.value = null }
        )
    }
}