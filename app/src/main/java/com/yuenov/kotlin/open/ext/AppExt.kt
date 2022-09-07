package com.yuenov.kotlin.open.ext

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_BOOL_SHOW_FILTER
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_BOOK_ID
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_CATEGORY_ID
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_CHANNEL_ID
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_LONG_CHAPTER_ID
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_MODEL_BOOK_BASE_INFO
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_STRING_CATEGORY_NAME
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction

private var lastClickTime: Long = 0

fun isFastDoubleClick(value: Long = 150L): Boolean {
    val time = System.currentTimeMillis()
    val timeD = time - lastClickTime
    return if (timeD in 1 until value) {
        true
    } else {
        lastClickTime = time
        false
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
                    //这一句是为全局Style(夜间模式)准备的
//                    this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(it)
                }
            }
            loadingDialog?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
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

fun isLoadingShowing(): Boolean {
    return loadingDialog?.isShowing ?: false
}

fun Fragment.showToast(@StringRes resId: Int) {
    Toast.makeText(this.context, resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(str: String) {
    Toast.makeText(this.context, str, Toast.LENGTH_SHORT).show()
}

/**
 * 跳转至阅读界面
 * @param chapterId 为0时表示打开历史记录
 */
fun Fragment.toRead(bookInfo: BookBaseInfo, chapterId: Long) {
    nav().navigateAction(R.id.to_read_fragment, Bundle().apply {
        putParcelable(EXTRA_MODEL_BOOK_BASE_INFO, bookInfo)
        putLong(EXTRA_LONG_CHAPTER_ID, chapterId)
    })
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
 * TODO: 跳转至搜索界面
 */
fun Fragment.toSearch() {
    nav().navigateAction(R.id.action_main_to_detail)
}

/**
 * 跳转至下载界面
 */
@Deprecated("不做下载界面")
fun Fragment.toDownload() {}

/**
 * TODO: 跳转至分类界面
 */
fun Fragment.toCategoryChannel() {
    nav().navigateAction(R.id.to_category_channel_fragment)
}

/**
 * TODO: 跳转至榜单界面
 */
fun Fragment.toRank() {
    nav().navigateAction(R.id.to_rank_fragment)
}

/**
 * TODO: 跳转至完本界面
 */
fun Fragment.toCategoryEnd() {
    nav().navigateAction(R.id.action_main_to_detail)
}

/**
 * TODO: 跳转至专题界面
 */
fun Fragment.toSpecial() {
    nav().navigateAction(R.id.action_main_to_detail)
}

/**
 * TODO: 跳转至分类下所有书籍界面
 */
fun Fragment.toDiscoverAll(categoryName: String?, type: String?, categoryId: Int?) {

}

/**
 * 跳转至分类下书籍列表界面
 */
fun Fragment.toCategoryBookList(categoryName: String?, categoryId: Int?, channelId: Int, isShowFilter: Boolean) {
    nav().navigateAction(R.id.to_category_book_list_fragment, Bundle().apply {
        putString(EXTRA_STRING_CATEGORY_NAME, categoryName)
        putInt(EXTRA_INT_CATEGORY_ID, categoryId ?: 0)
        putInt(EXTRA_INT_CHANNEL_ID, channelId)
        putBoolean(EXTRA_BOOL_SHOW_FILTER, isShowFilter)
    })
}

/**
 * 跳转至榜单分类下书籍列表界面
 */
fun Fragment.toRankBookList(rankName: String, channelId: Int, rankId: Int) {
    nav().navigateAction(R.id.to_rank_book_list_fragment, Bundle().apply {
        putString(EXTRA_STRING_CATEGORY_NAME, rankName)
        putInt(EXTRA_INT_CATEGORY_ID, rankId ?: 0)
        putInt(EXTRA_INT_CHANNEL_ID, channelId)
    })
}