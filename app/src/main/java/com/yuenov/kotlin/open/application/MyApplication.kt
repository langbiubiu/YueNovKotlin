package com.yuenov.kotlin.open.application

import android.content.Context
import com.yuenov.kotlin.open.viewmodel.AppViewModel
import com.yuenov.kotlin.open.viewmodel.EventViewModel
import me.hgj.jetpackmvvm.base.BaseApp

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewMode : AppViewModel by lazy { MyApplication.appViewModelInstance }

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { MyApplication.eventViewModelInstance }

class MyApplication: BaseApp() {

    companion object {
        lateinit var appContext: Context
        lateinit var eventViewModelInstance: EventViewModel
        lateinit var appViewModelInstance: AppViewModel
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        eventViewModelInstance = getAppViewModelProvider().get(EventViewModel::class.java)
        appViewModelInstance = getAppViewModelProvider().get(AppViewModel::class.java)
        initHttpInfo()
    }

    private fun initHttpInfo() {
        //设置端口和代理IP
    }
}