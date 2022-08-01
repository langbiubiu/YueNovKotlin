package com.yuenov.kotlin.open.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.yuenov.kotlin.open.ext.*
import me.hgj.jetpackmvvm.base.fragment.BaseVmVbFragment
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.nav

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbFragment<VM, VB>() {

    var nav: NavController? = null
    private var callback: BackPressCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nav = nav()
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                logd(this@BaseFragment.CLASS_TAG, "${this@BaseFragment} lifecycle event: ${event.name}")
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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

    override fun onResume() {
        super.onResume()
        // [OnBackPressedDispatcher]只会触发最新加入的enable为true的callback，
        // 因此在onResume注册callback，在onPause去除注册，保证不同Fragment之间不会互相影响
        if (needBackPressedCallback()) {
            callback = BackPressCallback(true)
            requireActivity().onBackPressedDispatcher.addCallback(callback!!)
        }
    }

    override fun onPause() {
        super.onPause()
        callback?.remove()
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

    /**
     * 是否注册返回键监听
     */
    open fun needBackPressedCallback(): Boolean = false

    /**
     * 只有在[needBackPressedCallback]为true才会回调
     * 注意：重写时要考虑好是否需要调用super.onBackPressed()，注册callback时会拦截默认的返回键处理流程，
     * 因此不会默认返回上一个Fragment。除非你不需要返回到上一个Fragment，或者想自己实现返回逻辑
     */
    open fun onBackPressed() {
        nav?.navigateUp()
    }

    inner class BackPressCallback(enabled: Boolean) : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }
}