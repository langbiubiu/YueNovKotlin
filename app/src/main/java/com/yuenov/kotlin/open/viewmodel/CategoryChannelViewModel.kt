package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.model.response.ChannelInfoItem
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState

class CategoryChannelViewModel : BaseFragmentViewModel() {

    var getCategoryChannelListState: MutableLiveData<ListDataUiState<ChannelInfoItem>> =
        MutableLiveData()

    fun getCategoryChannelList() {
        requestDelay(
            { apiService.getCategoryChannel() },
            {
                if (!it.channels.isNullOrEmpty()) {
                    getCategoryChannelListState.value = ListDataUiState(
                        isSuccess = true,
                        isEmpty = false,
                        listData = it.channels!!
                    )
                } else {
                    getCategoryChannelListState.value = ListDataUiState(
                        isSuccess = true,
                        isEmpty = true,
                        errMessage = "empty list"
                    )
                }
            },
            {
                getCategoryChannelListState.value = ListDataUiState(
                    isSuccess = false,
                    isEmpty = true,
                    errMessage = it.errorMsg
                )
            }, true
        )
    }
}