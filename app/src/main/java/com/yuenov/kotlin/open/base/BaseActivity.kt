package com.yuenov.kotlin.open.base

import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.yuenov.kotlin.open.ext.dismissLoadingExt
import com.yuenov.kotlin.open.ext.showLoadingExt
import me.hgj.jetpackmvvm.base.activity.BaseVmVbActivity

abstract class BaseActivity<VM : BaseFragmentViewModel, VB : ViewBinding> :
    BaseVmVbActivity<VM, VB>() {

    //loading框
    var loadingDialog: MaterialDialog? = null

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }
}