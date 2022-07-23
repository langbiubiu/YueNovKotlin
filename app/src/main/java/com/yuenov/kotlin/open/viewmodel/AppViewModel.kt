package com.yuenov.kotlin.open.viewmodel

import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.ext.readFromAssets
import com.yuenov.kotlin.open.model.response.AppConfigInfo
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.utils.DataStoreUtils
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData
import me.hgj.jetpackmvvm.ext.request

class AppViewModel : BaseViewModel() {

    var appConfigInfo = EventLiveData<AppConfigInfo>()

    init {
        if (appConfigInfo.value == null) {
            try {
                val info = gson.fromJson(
                    DataStoreUtils.getData(PreferenceConstants.KEY_CATEGORY_INFO, ""),
                    AppConfigInfo::class.java
                )
                if (info == null || info.categories.isNullOrEmpty()) {
                    val categoriesMenuJson =
                        readFromAssets(MyApplication.appContext, "categories.json")
                    appConfigInfo.value = gson.fromJson(categoriesMenuJson, AppConfigInfo::class.java)
                } else {
                    appConfigInfo.value = info
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * 更新配置信息
     * 通过网络接口，更新AppConfigInfo，并将数据存入DataStore中
     */
    fun updateAppConfigInfo() {
        request({ apiService.getAppConfig()},
            {
                appConfigInfo.value = it
                DataStoreUtils.putJsonData(PreferenceConstants.KEY_CATEGORY_INFO, it)
            }, {})
    }
}