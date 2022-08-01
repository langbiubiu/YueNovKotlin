package com.yuenov.kotlin.open.ext

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yuenov.kotlin.open.constant.InterFaceConstants
import jp.wasabeef.blurry.Blurry
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
fun Fragment.setClickListener(vararg views: View, listener: View.OnClickListener?) {
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

fun Dialog.setClickListener(vararg views: View, listener: View.OnClickListener?) {
    for (i in views.indices) {
        views[i].setOnClickListener(listener)
    }
}

fun ViewGroup.setClickListener(vararg views: View, listener: View.OnClickListener?) {
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
        RequestOptions().skipMemoryCache(false).placeholder(defaultResourcesID)
            .error(defaultResourcesID).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    ).into(this)
}

fun ImageView.blur(imageUrl: String?) {
    Glide.with(this).asBitmap().load(getImageUrl(imageUrl))
        .into(object : CustomViewTarget<ImageView, Bitmap>(this) {

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Blurry.with(this@blur.context)
                    .radius(10)
                    .sampling(9)
                    .async()
                    .from(resource)
                    .into(this@blur)
            }

            override fun onResourceCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
            }

        })
}
//--------------------- ImageView ------------------

//-------------------------------------------------------
/**
 * 拦截BottomNavigation长按事件 防止长按时出现Toast ---- 追求完美的大屌群友提的bug
 * @receiver BottomNavigationViewEx
 * @param ids IntArray
 */
fun BottomNavigationView.interceptLongClick(vararg ids:Int) {
    val bottomNavigationMenuView: ViewGroup = (this.getChildAt(0) as ViewGroup)
    for (index in ids.indices){
        bottomNavigationMenuView.getChildAt(index).findViewById<View>(ids[index]).setOnLongClickListener {
            true
        }
    }
}
//-------------------------------------------------------

//----------------- Visibility --------------------
fun resetVisibility(isShow: Boolean, vararg views: View) {
    val visibility = if (isShow) View.VISIBLE else View.GONE
    for (i in views.indices) {
        val v = views[i]
        if (v.visibility != visibility) v.visibility = visibility
    }
}

fun resetVisibility(visibility: Int, vararg views: View) {
    for (i in views.indices) {
        val v = views[i]
        if (v.visibility != visibility) v.visibility = visibility
    }
}