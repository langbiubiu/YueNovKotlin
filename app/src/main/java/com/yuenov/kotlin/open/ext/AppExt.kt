package com.yuenov.kotlin.open.ext

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_BOOK_ID
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction

private var lastClickTime: Long = 0

fun isFastDoubleClick(): Boolean {
    val time = System.currentTimeMillis()
    val timeD = time - lastClickTime
    if (0L < timeD && timeD < 150L) {
        return true
    } else {
        lastClickTime = time
        return false
    }
}

fun isFastDoubleClick(value: Long): Boolean {
    val time = System.currentTimeMillis()
    val timeD = time - lastClickTime
    if (0L < timeD && timeD < value) {
        return true
    } else {
        lastClickTime = time
        return false
    }
}

//loading框，
// 有内存泄露的风险，Kotlin函数参数默认为val，没有办法从外部传入dialog对象来置空
// 目前的办法是在destroy时，调用一次dismissLoadingExt
@SuppressLint("StaticFieldLeak")
private var loadingDialog: MaterialDialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    if (!this.isFinishing) {
        if (loadingDialog == null) {
            loadingDialog = MaterialDialog(this)
                .cancelable(true)
                .cancelOnTouchOutside(false)
                .cornerRadius(12f)
                .customView(R.layout.layout_custom_progress_dialog_view)
                .lifecycleOwner(this)
            loadingDialog?.getCustomView()?.run {
                this.findViewById<TextView>(R.id.loading_tips).text = message
//                this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(this@showLoadingExt)
            }
        }
        loadingDialog?.show()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let {
        if (!it.isFinishing) {
            if (loadingDialog == null) {
                loadingDialog = MaterialDialog(it)
                    .cancelable(true)
                    .cancelOnTouchOutside(false)
                    .cornerRadius(12f)
                    .customView(R.layout.layout_custom_progress_dialog_view)
                    .lifecycleOwner(this)
                loadingDialog?.getCustomView()?.run {
                    this.findViewById<TextView>(R.id.loading_tips).text = message
//                    this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(it)
                }
            }
            loadingDialog?.show()
        }
    }
}

/**
 * 关闭等待框
 */
fun AppCompatActivity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

fun AppCompatActivity.isLoadingShowing(): Boolean {
    return loadingDialog?.isShowing ?: false
}

fun Fragment.isLoadingShowing(): Boolean {
    return loadingDialog?.isShowing ?: false
}

fun AppCompatActivity.showToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(str: CharSequence) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(resId: Int) {
    Toast.makeText(this.context, resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(str: CharSequence) {
    Toast.makeText(this.context, str, Toast.LENGTH_SHORT).show()
}

/**
 * 跳转至阅读界面
 */
fun AppCompatActivity.toRead() {

}

/**
 * 跳转至阅读界面
 */
fun Fragment.toRead() {

}

/**
 * 跳转至详情界面
 */
fun Fragment.toDetail(bookId: Int) {
    nav().navigateAction(R.id.action_main_to_detail, Bundle().apply {
        putInt(EXTRA_INT_BOOK_ID, bookId)
    })
}

/**
 * 跳转至搜索界面
 */
fun AppCompatActivity.toSearch() {

}

/**
 * 跳转至搜索界面
 */
fun Fragment.toSearch() {

}