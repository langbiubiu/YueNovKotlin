package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.BookListResponse
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState

class RankBookListFragmentViewModel : BaseFragmentViewModel() {
    var getRankPageState: MutableLiveData<UpdateUiState<BookListResponse>> = MutableLiveData()

    fun getRankPage(channelId: Int, rankId: Int, pageNum: Int, pageSize: Int) {
        requestDelay(
            { apiService.getRankPage(channelId, rankId, pageNum, pageSize) },
            {
                getRankPageState.value = UpdateUiState(
                    isSuccess = true,
                    data = it
                )
            },
            {
                getRankPageState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            }
        )
    }
}