package com.yuenov.kotlin.open.base

import androidx.viewbinding.ViewBinding
import me.hgj.jetpackmvvm.base.activity.BaseVmVbActivity
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbActivity<VM, VB>() {

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {}

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {}
}