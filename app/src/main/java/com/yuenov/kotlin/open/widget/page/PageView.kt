package com.yuenov.kotlin.open.widget.page

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation

class PageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var viewWidth = 0
    private var viewHeight = 0
    private var startX = 0
    private var startY = 0
    // 是否滑动了
    private var isMove = false
    // View是否已完成布局
    private var isPrepare = false
    // 当前显示的Page
    private var curPage: Page = Page()
    // 当前显示的PageNumber
    var curPageNum = 0

    // 背景色
    private val bgColor: Int
    @ColorRes get() = pageLoader.getBgColor()

    // 字体颜色
    private val textColor: Int
    @ColorRes get() = pageLoader.getTextColor()

    // 字体大小
    private val textSize: Float
        get() = pageLoader.getTextSize()

    // 翻页动画
    private val pageAnimation: PageAnimation
        get() = pageLoader.getPageAnimation()

    // 右下角进度字体大小
    private var processTextSize: Float = 13f
    // 顶部标题字体大小
    private var titleTextSize: Float = 13f
    // 时间字体大小
    private var timeTextSize: Float = 13f
    //一个默认的PageLoader
    private var pageLoader: IPagerLoader = DefaultPageLoader(this)

    fun setPageLoader(loader: IPagerLoader) {
        pageLoader = loader
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        logd(CLASS_TAG, "onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        isPrepare = true
    }

    fun hasNext(isPrev: Boolean, execute: Boolean): Boolean {
        return if (pageLoader.allowTurnPage(isPrev)) {
            // TODO 还要加上判断章节内是否能翻页，如果不能，则说明本章内容显示到了开头或结尾
            // 再判断是否存在前一章或后一章
            pageLoader.hasNextContent(isPrev)
        } else {
            false
        }
    }

    fun turnPage() {}

    fun cancelTurn() {

    }

}