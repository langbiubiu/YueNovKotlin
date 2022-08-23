package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.VelocityTracker
import com.yuenov.kotlin.open.widget.page.PageView

//TODO 原理还没看，暂时先不管
class VerticalPageAnimation : PageAnimation() {
    // 滑动追踪的时间
    private val velocityDuration = 1000
    private var velocity: VelocityTracker? = null
    private val bitmapViewSize = 2

    override lateinit var bgBitmap: Bitmap
    override var curBitmap: Bitmap
        get() = bgBitmap
        set(_) {}

    override lateinit var nextBitmap: Bitmap

    // 被废弃的图片列表
    private val scrapViews = ArrayDeque<BitmapView>(bitmapViewSize)

    // 正在被利用的图片列表
    private val activeViews = ArrayList<BitmapView>(bitmapViewSize)

    // 是否处于刷新阶段
    private var isRefresh: Boolean = true

    init {
        // 创建两个BitmapView
        for (i in scrapViews.indices) {
            val view = BitmapView(
                Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.RGB_565),
                Rect(0, 0, viewWidth, viewHeight),
                Rect(0, 0, viewWidth, viewHeight),
                0,
                viewHeight
            )
            scrapViews.addFirst(view)
        }
        onLayout()
        isRefresh = false
    }

    // 修改布局,填充内容
    private fun onLayout() {
        // 如果还没有开始加载，则从上到下进行绘制
        if (activeViews.size == 0) {
            fillDown(0, 0)
            direction = Direction.NONE
        } else {
            val offset = (touchY - lastY).toInt()
            // 判断是下滑还是上拉 (下滑)
            if (offset > 0) {
                fillUp(activeViews.first().top, offset)
            } else { //下拉
                fillDown(activeViews.last().bottom, offset)
            }
        }
    }

    /**
     * 创建View填充底部空白部分
     *
     * @param bottomEdge :当前最后一个View的底部，在整个屏幕上的位置,即相对于屏幕顶部的距离
     * @param offset     :滑动的偏移量
     */
    private fun fillDown(bottomEdge: Int, offset: Int) {
        val iterator = activeViews.iterator()

        // 进行删除
        while (iterator.hasNext()) {
            val bitmapView = iterator.next()
            bitmapView.top += offset
            bitmapView.bottom += offset
            // 设置允许显示的范围
            bitmapView.destRect.top = bitmapView.top
            bitmapView.destRect.bottom = bitmapView.bottom

            // 判读是否越界
            if (bitmapView.bottom <= 0) {
                // 添加到废弃的View中
                scrapViews.add(bitmapView)
                // 从Active中移除
                iterator.remove()
                // 如果原先是从上加载，现在变成从下加载，则表示取消
                if (direction == Direction.UP) {
                    pageView.turnPageCanceled()
                    direction = Direction.NONE
                }
            }
        }

        // 滑动之后的最后一个 View 的距离屏幕顶部上的实际位置
        var realEdge = bottomEdge + offset

        // 进行填充
        while (realEdge < viewHeight && activeViews.size < bitmapViewSize) {
            // 从废弃的Views中获取一个
            val view = scrapViews.first()

            val cancelBitmap = nextBitmap
            nextBitmap = view.bitmap

            if (!isRefresh) {
                // 如果不存在next,则进行还原
                if (!pageView.hasNext(direction == Direction.PRE)) {
                    nextBitmap = cancelBitmap
                    for (activeView in activeViews) {
                        activeView.top = 0
                        activeView.bottom = viewHeight
                        // 设置允许显示的范围
                        activeView.destRect.top = activeView.top
                        activeView.destRect.bottom = activeView.bottom
                    }
                    abortAnim()
                    return
                }
            }

            // 如果加载成功，那么就将View从ScrapViews中移除
            scrapViews.removeFirst()
            // 添加到存活的Bitmap中
            activeViews.add(view)
            direction = Direction.DOWN

            // 设置Bitmap的范围
            view.top = realEdge
            view.bottom = realEdge + view.bitmap.height
            // 设置允许显示的范围
            view.destRect.top = view.top
            view.destRect.bottom = view.bottom

            realEdge += view.bitmap.height
        }
    }

    /**
     * 创建View填充底部空白部分
     *
     * @param bottomEdge :当前最后一个View的底部，在整个屏幕上的位置,即相对于屏幕顶部的距离
     * @param offset     :滑动的偏移量
     */
    private fun fillUp(topEdge: Int, offset: Int) {
        // 首先进行布局的调整
        val iterator = activeViews.iterator()
        while (iterator.hasNext()) {
            val bitmapView = iterator.next()
            bitmapView.top += offset
            bitmapView.bottom += offset
            // 设置允许显示的范围
            bitmapView.destRect.top = bitmapView.top
            bitmapView.destRect.bottom = bitmapView.bottom

            // 判读是否越界
            if (bitmapView.top >= viewHeight) {
                // 添加到废弃的View中
                scrapViews.add(bitmapView)
                // 从Active中移除
                iterator.remove()

                // 如果原先是下，现在变成从上加载了，则表示取消加载
                if (direction == Direction.DOWN) {
                    pageView.turnPageCanceled()
                    direction = Direction.NONE
                }
            }
        }

        // 滑动之后，第一个 View 的顶部距离屏幕顶部的实际位置。
        var realEdge = topEdge + offset

        // 对布局进行View填充
        while (realEdge > 0 && activeViews.size < bitmapViewSize) {
            // 从废弃的Views中获取一个
            val view = scrapViews.first()

            // 判读是否存在上一章节
            val cancelBitmap = nextBitmap
            nextBitmap = view.bitmap
            if (!isRefresh) {
                // 如果不存在next，则进行还原
                if (!pageView.hasNext(direction == Direction.DOWN)) {
                    nextBitmap = cancelBitmap
                    for (activeView in activeViews) {
                        activeView.top = 0
                        activeView.bottom = viewHeight
                        //设置允许显示的范围
                        activeView.destRect.top = activeView.top
                        activeView.destRect.bottom = activeView.bottom
                    }
                    abortAnim()
                    return
                }
            }
            // 如果加载成功，那么就将View从ScrapViews中移除
            scrapViews.removeFirst()
            // 加入到存活的对象中
            activeViews.add(0, view)
            direction = Direction.UP
            //设置Bitmap的范围
            view.top = realEdge - view.bitmap.height
            view.bottom = realEdge

            // 设置允许显示的范围
            view.destRect.top = view.top
            view.destRect.bottom = view.bottom
            realEdge -= view.bitmap.height
        }
    }

    /**
     * 重置位移
     */
    fun resetBitmap() {
        isRefresh = true
        // 将所有的Active加入到Scrap中
        for (view in activeViews) {
            scrapViews.add(view)
        }
        // 清除所有的Active
        activeViews.clear()
        // 重新进行布局
        onLayout()
        isRefresh = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        // 初始化速度追踪器
        if (velocity == null) velocity = VelocityTracker.obtain()

        velocity?.addMovement(event)
        // 设置触碰点
        setTouchPoint(x, y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isRunning = false
                // 设置起始点
                setStartPoint(x, y)
                // 停止动画
                abortAnim()
            }
            MotionEvent.ACTION_MOVE -> {
                velocity?.computeCurrentVelocity(velocityDuration)
                isRunning = true
                // 进行刷新
                pageView.postInvalidate()
            }
            MotionEvent.ACTION_UP -> {
                isRunning = false
                // 开启动画
                startAnim()
                // 删除追踪器
                velocity?.recycle()
                velocity = null
            }
            MotionEvent.ACTION_CANCEL -> {
                velocity?.recycle()
                velocity = null
            }
        }
        return true
    }

    override fun draw(canvas: Canvas) {
        // 进行布局
        onLayout()

        // 绘制背景
        canvas.drawBitmap(bgBitmap, 0f, 0f, null)
        // 绘制内容
        canvas.save()
        // 移动位置
        canvas.translate(0f, marginHeight.toFloat())
        // 裁剪显示区域
        canvas.clipRect(0, 0, viewWidth, viewHeight)
        // 绘制bitmap
        for (view in activeViews) {
            canvas.drawBitmap(view.bitmap, view.srcRect, view.destRect, null)
        }
        canvas.restore()
    }

    override fun setView(view: PageView) {
        super.setView(view)
        bgBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565)
    }

    @Synchronized
    override fun startAnim() {
        isRunning = true
        scroller?.fling(
            0,
            touchY.toInt(),
            0,
            velocity!!.yVelocity.toInt(),
            0,
            0,
            Int.MIN_VALUE,
            Int.MAX_VALUE
        )
    }

    override fun scrollAnim() {
        scroller?.apply {
            if (computeScrollOffset()) {
                val x = currX
                val y = currY
                setTouchPoint(x.toFloat(), y.toFloat())
                if (finalX == x && finalY == y) {
                    isRunning = false
                    autoPageIsRunning = false
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
                autoPageIsRunning = false
            }
        }
    }

    private data class BitmapView(
        val bitmap: Bitmap,
        val srcRect: Rect,
        val destRect: Rect,
        var top: Int,
        var bottom: Int
    )
}