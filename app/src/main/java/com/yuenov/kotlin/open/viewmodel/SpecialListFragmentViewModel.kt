package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.model.response.SpecialInfoItem
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState

class SpecialListFragmentViewModel : BaseFragmentViewModel() {

    var getSpecialListState: MutableLiveData<ListDataUiState<SpecialInfoItem>> = MutableLiveData()
    var getSpecialPageState: MutableLiveData<Pair<String, BookListResponse>?> = MutableLiveData()

    fun getSpecialList(pageNum: Int, pageSize: Int) {
        requestDelay(
            { apiService.getSpecialList(pageNum, pageSize) },
            {
                getSpecialListState.value = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it.specialList?.isEmpty() ?: true,
                    listData = it.specialList ?: listOf()
                )
            },
            {
                getSpecialListState.value = ListDataUiState(
                    isSuccess = false,
                    isEmpty = true,
                    errMessage = it.errorMsg
                )
            })
    }

    fun getSpecialPage(
        pageNum: Int,
        pageSize: Int,
        categoryName: String,
        id: Int,
    ) {
        requestDelay(
            { apiService.getSpecialPage(id, pageNum, pageSize) },
            { getSpecialPageState.value = Pair(categoryName, it) },
            { getSpecialPageState.value = null }
        )
    }
}