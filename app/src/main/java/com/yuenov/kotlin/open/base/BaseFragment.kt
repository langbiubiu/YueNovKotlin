package com.yuenov.kotlin.open.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.yuenov.kotlin.open.ext.dismissLoadingExt
import com.yuenov.kotlin.open.ext.hideSoftKeyboard
import com.yuenov.kotlin.open.ext.showLoadingExt
import me.hgj.jetpackmvvm.base.fragment.BaseVmVbFragment
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbFragment<VM, VB>() {

    var loadingDialog: MaterialDialog? = null

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载 只有当前fragment视图显示时才会触发该方法，且之后触发一次
     * IO请求的数据在这里加载
     */
    override fun lazyLoadData() {}

    /**
     * 创建LiveData观察者 Fragment执行onViewCreated后触发
     */
    override fun createObserver() {}

    /**
     * Fragment执行onViewCreated后触发
     * 一些不耗时的数据可以在这里加载
     */
    override fun initData() {

    }

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

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard(activity)
    }

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    override fun lazyLoadTime(): Long {
        return 300
    }
}