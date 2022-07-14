package com.yuenov.kotlin.open.ext

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    this.offscreenPageLimit = offscreenPageLimit
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

fun Fragment.setClickListeners(vararg views: View, listener: View.OnClickListener) {
    for (i in views.indices) {
        views[i].setOnClickListener(listener)
    }
}

//lambda需要写在可变参数前，否则编译器会把lambda表达式也看做一个可变参数
fun Fragment.setClickListener(listener: (v: View) -> Unit, vararg views: View) {
    for (view in views) {
        view.setOnClickListener { listener(it) }
    }
}

fun Dialog.setClickListeners(vararg views: View, listener: View.OnClickListener) {
    for (i in views.indices) {
        views[i].setOnClickListener(listener)
    }
}
