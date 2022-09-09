package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState

class RecommendFragmentViewModel : BaseFragmentViewModel() {

    var getRecommendState: MutableLiveData<UpdateUiState<BookListResponse>> = MutableLiveData()

    fun getRecommend(bookId: Int, pageNum: Int, pageSize: Int) {
        requestDelay(
            { apiService.getRecommend(bookId, pageNum, pageSize) },
            {
                getRecommendState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                getRecommendState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            }
        )
    }
}