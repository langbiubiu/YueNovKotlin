package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.RankListInfoItem
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState

class RankFragmentViewModel : BaseFragmentViewModel() {

    var getRankListState: MutableLiveData<ListDataUiState<RankListInfoItem>> = MutableLiveData()

    fun getRankList() {
        requestDelay(
            { apiService.getRankList() },
            {
                getRankListState.value = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it.channels?.isEmpty() ?: true,
                    listData = it.channels ?: listOf()
                )
            },
            {
                getRankListState.value = ListDataUiState(
                    isSuccess = false,
                    isEmpty = true,
                    errMessage = it.errorMsg
                )
            }
        )
    }

}