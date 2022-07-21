package com.yuenov.kotlin.open.ext

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.yuenov.kotlin.open.constant.InterFaceConstants
import java.io.File

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

//--------------------- ViewPager ------------------
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
//--------------------- ViewPager ------------------

//--------------------- set click listener ------------------
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
//--------------------- set click listener ------------------

//--------------------- ImageView ------------------
private fun getImageUrl(url: String?): String? {
    if (!url.isNullOrBlank() && url.startsWith("/"))
        return InterFaceConstants.getImageDomain() + url
    else
        return url
}

fun ImageView.loadLocalImage(filePath: String?) {
    if (filePath.isNullOrEmpty()) return
    Glide.with(this).load(File(filePath)).into(this)
}

fun ImageView.loadImage(iconUrl: String?, defaultResourcesID: Int) {
    Glide.with(this).load(getImageUrl(iconUrl)).apply(
        RequestOptions().centerCrop().skipMemoryCache(false).placeholder(defaultResourcesID)
            .error(defaultResourcesID).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    ).into(this)
}
//--------------------- ImageView ------------------