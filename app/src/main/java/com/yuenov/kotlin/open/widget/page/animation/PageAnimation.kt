package com.yuenov.kotlin.open.widget.page.animation

import android.widget.Scroller
import android.view.MotionEvent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.widget.page.PageView

/**
 * 翻页动画抽象类
 */
abstract class PageAnimation {

    //正在使用的View
    protected lateinit var pageView: PageView
    //屏幕的尺寸
    protected var screenWidth: Int = 0
    protected var screenHeight: Int = 0
    //屏幕的间距
    protected var marginWidth: Int = 0
    protected var marginHeight: Int = 0
    //视图的尺寸
    protected var viewWidth: Int = 0
    protected var viewHeight: Int = 0

    // 获取背景板
    abstract var bgBitmap: Bitmap
    // 获取当前页内容显示版面
    abstract var curBitmap: Bitmap
    // 获取下一页内容显示版面
    abstract var nextBitmap: Bitmap
    // 滑动装置
    protected var scroller: Scroller? = null

    //移动方向
    open var direction = Direction.NONE
    //是否正在执行动画
    var isRunning = false
    //是否正在执行自动阅读
    var autoPageIsRunning = false
    var isCancelTouch = false

    //起始点
    protected var startX = 0f
    protected var startY = 0f

    //触碰点
    protected var touchX = 0f
    protected var touchY = 0f

    //上一个触碰点
    protected var lastX = 0f
    protected var lastY = 0f

    // 设置起始位置的触摸点，并记录上次的触摸点
    open fun setStartPoint(x: Float, y: Float) {
        startX = x
        startY = y
        lastX = startX
        lastY = startY
    }

    open fun setTouchPoint(x: Float, y: Float) {
        lastX = touchX
        lastY = touchY
        touchX = x
        touchY = y
    }

    @CallSuper
    internal open fun setView(view: PageView) {
        pageView = view
        screenWidth = pageView.width
        screenHeight = pageView.height
        scroller = Scroller(pageView.context, LinearInterpolator())
        viewWidth = screenWidth - marginWidth * 2
        viewHeight = screenHeight - marginHeight * 2
    }

    /**
     * 开启翻页动画
     */
    open fun startAnim() {
        if (isRunning || autoPageIsRunning) {
            return
        }
        isRunning = true
    }

    /**
     * 点击事件的处理
     * @param event
     */
    abstract fun onTouchEvent(event: MotionEvent): Boolean

    /**
     * 绘制图形
     * @param canvas
     */
    abstract fun draw(canvas: Canvas)

    /**
     * 滚动动画
     * 必须放在computeScroll()方法中执行
     */
    abstract fun scrollAnim()

    /**
     * 取消动画
     */
    abstract fun abortAnim()

    enum class Direction(val isHorizontal: Boolean) {
        NONE(true), NEXT(true), PRE(true), UP(false), DOWN(false);
    }

    interface PageAnimationListener {
        fun onTurnPageStart()
        fun onTurnPageCompleted()
        fun onTurnPageCanceled()
    }

}