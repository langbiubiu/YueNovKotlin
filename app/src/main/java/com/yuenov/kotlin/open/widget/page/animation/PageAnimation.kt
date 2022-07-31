package com.yuenov.kotlin.open.widget.page.animation

import android.widget.Scroller
import android.view.MotionEvent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by newbiechen on 17-7-24.
 * 翻页动画抽象类
 */
abstract class PageAnimation(
    //屏幕的尺寸
    protected var screenWidth: Int,
    protected var screenHeight: Int,
    //屏幕的间距
    protected var marginWidth: Int,
    protected var marginHeight: Int,
    //正在使用的View
    protected var view: View,
    //监听器
    protected var listener: OnPageChangeListener?
) {
    //滑动装置
    protected var scroller: Scroller = Scroller(this.view.context, LinearInterpolator())

    //移动方向
    var direction = Direction.NONE
    var isRunning = false
        protected set
    var autoPageIsRunning = false
    var isCancelTouch = false

    //视图的尺寸
    protected var viewWidth: Int = screenWidth - marginWidth * 2
    protected var viewHeight: Int = screenHeight - marginHeight * 2

    //起始点
    protected var startX = 0f
    protected var startY = 0f

    //触碰点
    protected var touchX = 0f
    protected var touchY = 0f

    //上一个触碰点
    protected var lastX = 0f
    protected var lastY = 0f

    constructor(w: Int, h: Int, view: View, listener: OnPageChangeListener?) : this(
        w,
        h,
        0,
        0,
        view,
        listener
    ) {}

    fun setStartPoint(x: Float, y: Float) {
        startX = x
        startY = y
        lastX = startX
        lastY = startY
    }

    fun setTouchPoint(x: Float, y: Float) {
        lastX = touchX
        lastY = touchY
        touchX = x
        touchY = y
    }

    /**
     * 开启翻页动画
     */
    fun startAnim() {
        if (isRunning || autoPageIsRunning) {
            return
        }
        isRunning = true
    }

    fun clear() {
//        view = null
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
    abstract fun draw(canvas: Canvas?)

    /**
     * 滚动动画
     * 必须放在computeScroll()方法中执行
     */
    abstract fun scrollAnim()

    /**
     * 取消动画
     */
    abstract fun abortAnim()

    /**
     * 获取背景板
     * @return
     */
    abstract var bgBitmap: Bitmap?

    /**
     * 获取内容显示版面
     */
    abstract var nextBitmap: Bitmap?

    enum class Direction(val isHorizontal: Boolean) {
        NONE(true), NEXT(true), PRE(true), UP(false), DOWN(false);
    }

    interface OnPageChangeListener {
        fun hasPrev(execute: Boolean): Boolean
        fun hasNext(execute: Boolean): Boolean
        fun pageCancel()
        fun turnPage()
    }
}