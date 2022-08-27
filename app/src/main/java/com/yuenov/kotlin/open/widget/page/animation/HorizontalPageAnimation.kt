package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.widget.page.PageView
import kotlin.math.abs

/**
 * 横向动画的模板
 */
abstract class HorizontalPageAnimation : PageAnimation() {
    override var curBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
    override var nextBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)

    //水平翻页时，背景跟随书页翻动，因此不需要单独绘制
    override var bgBitmap: Bitmap
        get() = nextBitmap
        set(_) {}

    //是否取消翻页
    protected var isCancel = false

    //可以使用 mLast代替
    private var moveX = 0
    private var moveY = 0

    // 触点是否发生过移动了
    private var hasMoved = false

    //是否翻阅下一页。true表示翻到下一页，false表示上一页。
    private var isNext = false

    //是否没下一页或者上一页
    protected var hasNext = false

    // move时是否执行过一次hasPre或hasNext方法(下载时候按住不放 会执行两次，所以加此标志只执行一次)
    private var executePreOrNextPage = false

    /**
     * 转换页面，在显示下一章的时候，必须首先调用此方法
     * ### 应当在翻页后isRunning变为false时调用！
     */
    fun changePage() {
        curBitmap.apply {
            curBitmap = nextBitmap
            nextBitmap = this
        }
        pageView.swapPage()
    }

    /**
     * 绘制不滑动页面，原则上应当只绘制curBitmap
     */
    abstract fun drawStatic(canvas: Canvas)

    //绘制滑动页面
    abstract fun drawMove(canvas: Canvas)

    private fun reset(x: Int, y: Int) {
        //移动的点击位置
        moveX = 0
        moveY = 0
        hasMoved = false
        hasNext = true
        isNext = false
        isCancel = false
        executePreOrNextPage = true
        setStartPoint(x.toFloat(), y.toFloat())
        abortAnim()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //获取点击位置
        val x = event.x.toInt()
        val y = event.y.toInt()
        //设置触摸点
        setTouchPoint(x.toFloat(), y.toFloat())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> reset(x, y)
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(pageView.context).scaledTouchSlop
                //判断是否移动了
                if (!hasMoved) {
                    hasMoved = abs(startX - x) > slop || abs(startY - y) > slop
                }
                if (hasMoved) {
                    //判断是否是准备移动的状态(将要移动但是还没有移动)
                    if (moveX == 0 && moveY == 0) {

                        //判断翻得是上一页还是下一页
                        if (x - startX > 0) {
                            //上一页的参数配置
                            isNext = false
                            executePreOrNextPage = false
                            direction = Direction.PRE
                            //如果上一页不存在
                            if (!pageView.hasNext(isNext)) {
                                hasNext = false
                                return true
                            }
                        } else {
                            //进行下一页的配置
                            isNext = true
                            //判断是否下一页存在
                            executePreOrNextPage = false
                            //如果存在设置动画方向
                            direction = Direction.NEXT
                            //如果下一页不存在 表示没有下一页了
                            if (!pageView.hasNext(isNext)) {
                                hasNext = false
                                return true
                            }
                        }
                        pageView.turnPageStart()
                    } else {
                        //判断是否取消翻页
                        isCancel = if (isNext) {
                            x - moveX > 0
                        } else {
                            x - moveX < 0
                        }
                    }
                    moveX = x
                    moveY = y
                    isRunning = true

                    // 防止无动画抖动
                    if (this !is NonePageAnimation)
                        pageView.invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!hasMoved) {
                    isNext = x >= screenWidth / 2
                    if (isNext) {
                        //设置动画方向
                        direction = Direction.NEXT
                        //判断是否下一页存在
                        if (!pageView.hasNext(isNext)) {
                            return true
                        }
                    } else {
                        direction = Direction.PRE
                        if (!pageView.hasNext(isNext)) {
                            return true
                        }
                    }
                    pageView.turnPageStart()
                }

                // 是否取消翻页
                if (isCancel) {
                    pageView.turnPageCanceled()
                }

                // 开启翻页效果
                if (hasNext) {
                    startAnim()
                    pageView.invalidate()
                }
            }
        }
        return true
    }

    override fun draw(canvas: Canvas) {
        if (isRunning) {
            drawMove(canvas)
        } else {
            drawStatic(canvas)
        }
    }

    override fun setView(view: PageView) {
        super.setView(view)
        curBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
        nextBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
    }

    override fun scrollAnim() {
        scroller?.apply {
            if (computeScrollOffset()) {
                val x = currX
                val y = currY
                setTouchPoint(x.toFloat(), y.toFloat())
                if (finalX == x && finalY == y) {
                    isRunning = false
                    if (!isCancel) {
                        changePage()
                        pageView.turnPageCompleted()
                    }

                    // 自动翻页后，重新记录下down的位置，否则翻页前的动画不知道down的起始点
                    if (autoPageIsRunning) {
                        reset(0, 0)
                        setTouchPoint(finalX.toFloat(), finalY.toFloat())
                        autoPageIsRunning = false
                    }
                }
                pageView.postInvalidate()
            }
        }
    }

    override fun abortAnim() {
        scroller?.apply {
            if (!isFinished) {
                abortAnimation()
                isRunning = false
                if (!isCancel) {
                    changePage()
                    pageView.turnPageCompleted()
                }
                autoPageIsRunning = false
                setTouchPoint(finalX.toFloat(), finalY.toFloat())
                pageView.postInvalidate()
            }
        }
    }

}