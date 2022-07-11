package com.yuenov.kotlin.open.viewmodel

import com.yuenov.kotlin.open.model.AppConfigInfo
import com.yuenov.kotlin.open.utils.AppConfigUtils
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData

class AppViewModel : BaseViewModel() {

    var appConfigInfo = EventLiveData<AppConfigInfo>()

    init {
        appConfigInfo.value = AppConfigUtils.getAppConfigInfo()
    }
}