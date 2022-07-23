package com.yuenov.kotlin.open.application

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.*
import com.google.gson.Gson
import com.yuenov.kotlin.open.constant.InterFaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.utils.DataStoreUtils
import com.yuenov.kotlin.open.viewmodel.AppViewModel
import com.yuenov.kotlin.open.viewmodel.EventViewModel
import me.hgj.jetpackmvvm.base.BaseApp
import me.hgj.jetpackmvvm.ext.util.jetpackMvvmLog

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewMode: AppViewModel by lazy { MyApplication.appViewModelInstance }

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { MyApplication.eventViewModelInstance }

//一个全局的Gson实例
val gson: Gson by lazy { Gson() }

class MyApplication : BaseApp() {

    companion object {
        lateinit var appContext: Context
        lateinit var eventViewModelInstance: EventViewModel
        lateinit var appViewModelInstance: AppViewModel
        var watchActivityLife = false
        var watchAppLife = false
    }

    override fun onCreate() {
        logd(CLASS_TAG, "onCreate")
        super.onCreate()
        appContext = this
        eventViewModelInstance = getAppViewModelProvider().get(EventViewModel::class.java)
        appViewModelInstance = getAppViewModelProvider().get(AppViewModel::class.java)
        initHttpInfo()
        //JetpackMVVM框架内部打印开关
        jetpackMvvmLog = false
    }

    //设置端口
    private fun initHttpInfo() {
        val port = DataStoreUtils.getData(PreferenceConstants.KEY_INTERFACE_PORT, InterFaceConstants.domainPort)
        InterFaceConstants.domainPort = port
    }

    /**
     * 监听Activity的生命周期
     */
    private fun registerLifecycleCallbacks() {
        if (watchActivityLife)
            registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                }

                override fun onActivityStarted(activity: Activity) {
                }

                override fun onActivityResumed(activity: Activity) {
                }

                override fun onActivityPaused(activity: Activity) {
                }

                override fun onActivityStopped(activity: Activity) {
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                }
            })
    }

    private fun registerAppLifecycleObserver() {
        if (watchAppLife)
            ProcessLifecycleOwner.get().lifecycle.addObserver(object: LifecycleEventObserver{
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> Unit
                        Lifecycle.Event.ON_START -> eventViewModel.isForeground.value = true
                        Lifecycle.Event.ON_RESUME -> Unit
                        Lifecycle.Event.ON_PAUSE -> Unit
                        Lifecycle.Event.ON_STOP -> eventViewModel.isForeground.value = false
                        Lifecycle.Event.ON_DESTROY -> Unit
                        Lifecycle.Event.ON_ANY -> throw IllegalArgumentException("ON_ANY must not been send by anybody")
                    }
                }
            })
    }
}