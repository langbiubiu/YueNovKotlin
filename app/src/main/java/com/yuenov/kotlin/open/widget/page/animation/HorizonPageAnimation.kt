package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * Created by newbiechen on 17-7-24.
 * 横向动画的模板
 */
abstract class HorizonPageAnimation(
    w: Int, h: Int, marginWidth: Int, marginHeight: Int,
    view: View, listener: OnPageChangeListener?
) : PageAnimation(w, h, marginWidth, marginHeight, view, listener) {
    protected var mCurBitmap: Bitmap? = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
    override var nextBitmap: Bitmap? = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565)
    override var bgBitmap: Bitmap?
        get() = nextBitmap
        set(value) {}

    //是否取消翻页
    protected var isCancel = false
    private var x = 0
    private var y = 0

    //可以使用 mLast代替
    private var moveX = 0
    private var moveY = 0

    //是否移动了
    private var isMove = false

    //是否翻阅下一页。true表示翻到下一页，false表示上一页。
    private var isNext = false

    //是否没下一页或者上一页
    private var noNext = false

    // move时是否执行过一次hasPre或hasNext方法(下载时候按住不放 会执行两次，所以加此标志只执行一次)
    var executePreOrNextPage = false

    constructor(w: Int, h: Int, view: View, listener: OnPageChangeListener?) : this(
        w,
        h,
        0,
        0,
        view,
        listener
    ) {
    }

    /**
     * 转换页面，在显示下一章的时候，必须首先调用此方法
     */
    fun changePage() {
        val bitmap = mCurBitmap
        mCurBitmap = nextBitmap
        nextBitmap = bitmap
    }

    abstract fun drawStatic(canvas: Canvas?)
    abstract fun drawMove(canvas: Canvas?)
    private fun simulationTouchDown(x: Int, y: Int) {
        //移动的点击位置
        moveX = 0
        moveY = 0
        //是否移动
        isMove = false
        //是否存在下一章
        noNext = false
        //是下一章还是前一章
        isNext = false
        //是否正在执行动画
        isRunning = false
        //取消
        isCancel = false
        executePreOrNextPage = true
        //设置起始位置的触摸点
        setStartPoint(x.toFloat(), y.toFloat())
        //如果存在动画则取消动画
        abortAnim()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        //获取点击位置
        x = event.x.toInt()
        y = event.y.toInt()
        //设置触摸点
        setTouchPoint(x.toFloat(), y.toFloat())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> simulationTouchDown(x, y)
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(view.context).scaledTouchSlop
                //判断是否移动了
                if (!isMove) {
                    isMove = Math.abs(startX - x) > slop || Math.abs(startY - y) > slop
                }
                if (isMove) {
                    //判断是否是准备移动的状态(将要移动但是还没有移动)
                    if (moveX == 0 && moveY == 0) {

                        //判断翻得是上一页还是下一页
                        if (x - startX > 0) {
                            //上一页的参数配置
                            isNext = false
                            executePreOrNextPage = false
                            direction = Direction.PRE
                            listener?.apply {
                                //如果上一页不存在
                                if (!hasPrev(executePreOrNextPage)) {
                                    noNext = true
                                    return true
                                }
                            }
                        } else {
                            //进行下一页的配置
                            isNext = true
                            //判断是否下一页存在
                            executePreOrNextPage = false
                            //如果存在设置动画方向
                            direction = Direction.NEXT
                            listener?.apply {
                                //如果下一页不存在 表示没有下一页了
                                if (!hasNext(executePreOrNextPage)) {
                                    noNext = true
                                    return true
                                }
                            }
                        }
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
//                    if (this !is NonePageAnim) view!!.invalidate()
                    view.invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isMove) {
                    isNext = x >= screenWidth / 2
                    if (isNext) {
                        //设置动画方向
                        direction = Direction.NEXT
                        //判断是否下一页存在
                        listener?.run {
                            if (!hasNext(true)) {
                                return true
                            }
                        }
                    } else {
                        direction = Direction.PRE
                        listener?.run {
                            if (!hasPrev(true)) {
                                return true
                            }
                        }
                    }
                }

                // 是否取消翻页
                if (isCancel) {
                    // 无动画，不执行取消回调
//                    if (this !is NonePageAnim) listener!!.pageCancel()
                    listener?.pageCancel()
                } else {
                    listener?.turnPage()
                }

                // 开启翻页效果
                if (!noNext) {
                    startAnim()
                    view.invalidate()
                }
            }
        }
        return true
    }

    override fun draw(canvas: Canvas?) {
        if (isRunning) {
            drawMove(canvas)
        } else {
            if (isCancel) {
                nextBitmap = mCurBitmap!!.copy(Bitmap.Config.RGB_565, true)
            }
            drawStatic(canvas)
        }
    }

    override fun scrollAnim() {
        if (scroller.computeScrollOffset()) {
            val x = scroller.currX
            val y = scroller.currY
            setTouchPoint(x.toFloat(), y.toFloat())
            if (scroller.finalX == x && scroller.finalY == y) {
                isRunning = false

                // 自动翻页后，重新记录下down的位置，否则翻页前的动画不知道down的起始点
                if (autoPageIsRunning) {
                    isCancel = true
                    simulationTouchDown(0, 0)
                    setTouchPoint(scroller.finalX.toFloat(), scroller.finalY.toFloat())
                    autoPageIsRunning = false
                }
            }
            view.postInvalidate()
        }
    }

    override fun abortAnim() {
        if (!scroller.isFinished) {
            scroller.abortAnimation()
            isRunning = false
            autoPageIsRunning = false
            setTouchPoint(scroller.finalX.toFloat(), scroller.finalY.toFloat())
            view.postInvalidate()
        }
    }

}