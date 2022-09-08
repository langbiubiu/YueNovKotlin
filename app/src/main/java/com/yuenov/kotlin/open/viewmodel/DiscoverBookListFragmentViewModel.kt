package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState

class DiscoverBookListFragmentViewModel: BaseFragmentViewModel() {
    var getDiscoverAllState: MutableLiveData<UpdateUiState<BookListResponse>> = MutableLiveData()

    fun getDiscoverAll(pageNum: Int, pageSize: Int, type: String, categoryId: Int?) {
        requestDelay(
            { apiService.categoryDiscoveryAll(pageNum, pageSize, type, categoryId) },
            {
                getDiscoverAllState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                getDiscoverAllState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            }
        )
    }
}