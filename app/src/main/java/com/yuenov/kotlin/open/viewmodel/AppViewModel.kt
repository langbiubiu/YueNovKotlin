package com.yuenov.kotlin.open.viewmodel

import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.ext.readFromAssets
import com.yuenov.kotlin.open.model.response.AppConfigInfo
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.utils.DataStoreUtils
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData

class AppViewModel : BaseFragmentViewModel() {

    var appConfigInfo = EventLiveData<AppConfigInfo>()

    init {
        if (appConfigInfo.value == null) {
            try {
                val info = DataStoreUtils.getJsonData(
                    PreferenceConstants.KEY_CATEGORY_INFO,
                    AppConfigInfo::class.java,
                    AppConfigInfo()
                )
                if (info.categories.isNullOrEmpty()) {
                    val categoriesMenuJson =
                        readFromAssets(MyApplication.appContext, "categories.json")
                    appConfigInfo.value =
                        gson.fromJson(categoriesMenuJson, AppConfigInfo::class.java)
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
        requestDelay({ apiService.getAppConfig() },
            {
                appConfigInfo.value = it
                DataStoreUtils.putJsonData(PreferenceConstants.KEY_CATEGORY_INFO, it)
            }, {})
    }
}