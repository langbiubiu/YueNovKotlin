package com.yuenov.kotlin.open.utils

import com.google.gson.Gson
import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.ext.readFromAssets
import com.yuenov.kotlin.open.model.AppConfigInfo

/**
 * 用于初始化AppConfigInfo的信息
 */
object AppConfigUtils {

    private var appConfigInfo: AppConfigInfo? = null

    /**
     * 获取AppConfigInfo
     * 首先从DataStore中获取，若DataStore为空，则从asset目录下的json文件获取
     */
    fun getAppConfigInfo(): AppConfigInfo? {
        if (appConfigInfo == null) {
            try {
                val gson = Gson()
                val info = gson.fromJson(
                    DataStoreUtils.getData(PreferenceConstants.KEY_CATEGORY_INFO, ""),
                    AppConfigInfo::class.java
                )
                if (info == null || info.isEmpty()) {
                    val categoriesMenuJson =
                        readFromAssets(MyApplication.appContext, "categories.json")
                    appConfigInfo = Gson().fromJson(categoriesMenuJson, AppConfigInfo::class.java)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return appConfigInfo
    }

    /**
     * 更新配置信息
     * 通过网络接口，更新AppConfigInfo，并将数据存入DataStore中
     */
    fun updateAppConfigInfo() {
        // TODO:
    }
}