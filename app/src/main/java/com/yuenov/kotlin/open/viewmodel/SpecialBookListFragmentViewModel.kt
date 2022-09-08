package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState

class SpecialBookListFragmentViewModel: BaseFragmentViewModel() {
    var getSpecialPageState: MutableLiveData<UpdateUiState<BookListResponse>> = MutableLiveData()

    fun getSpecialPage(
        pageNum: Int,
        pageSize: Int,
        id: Int,
    ) {
        requestDelay(
            { apiService.getSpecialPage(id, pageNum, pageSize) },
            {
                getSpecialPageState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                getSpecialPageState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            }
        )
    }
}