package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Canvas
import android.graphics.Rect
import com.yuenov.kotlin.open.widget.page.PageView
import kotlin.math.abs

class SlidePageAnimation : HorizontalPageAnimation() {

    private lateinit var srcRect: Rect
    private lateinit var destRect: Rect
    private lateinit var nextSrcRect: Rect
    private lateinit var nextDestRect: Rect

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(curBitmap, 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        var dis: Int
        when (direction) {
            Direction.NEXT -> {
                dis = (screenWidth - startX + touchX).toInt()
                if (dis > screenWidth) dis = screenWidth
                //计算bitmap截取的区域
                srcRect.left = screenWidth - dis
                //计算bitmap在canvas显示的区域
                destRect.right = dis
                //计算下一页截取的区域
                nextSrcRect.right = screenWidth - dis
                //计算下一页在canvas显示的区域
                nextDestRect.left = dis

                canvas.drawBitmap(nextBitmap, nextSrcRect, nextDestRect, null)
                canvas.drawBitmap(curBitmap, srcRect, destRect, null)
            }
            else -> {
                dis = (touchX - startX).toInt()
                if (dis < 0) {
                    dis = 0
                    startX = touchX
                }
                //计算bitmap截取的区域
                srcRect.left = screenWidth - dis
                //计算bitmap在canvas显示的区域
                destRect.right = dis
                //计算下一页截取的区域
                nextSrcRect.right = screenWidth - dis
                //计算下一页在canvas显示的区域
                nextDestRect.left = dis

                canvas.drawBitmap(curBitmap, nextSrcRect, nextDestRect, null)
                canvas.drawBitmap(nextBitmap, srcRect, destRect, null)
            }
        }
    }

    override fun setView(view: PageView) {
        super.setView(view)
        srcRect = Rect(0, 0, viewWidth, viewHeight)
        destRect = Rect(0, 0, viewWidth, viewHeight)
        nextSrcRect = Rect(0, 0, viewWidth, viewHeight)
        nextDestRect = Rect(0, 0, viewWidth, viewHeight)
    }

    override fun startAnim() {
        super.startAnim()
        val dx: Int
        when (direction) {
            Direction.NEXT -> {
                if (isCancel) {
                    var dis = (screenWidth - startX + touchX).toInt()
                    if (dis > screenWidth) dis = screenWidth
                    dx = screenWidth - dis
                } else {
                    dx = (-(screenWidth - startX + touchX)).toInt()
                }
            }
            else -> {
                dx = if (isCancel) {
                    (-abs(touchX - startX)).toInt()
                } else {
                    (screenWidth - (touchX - startX)).toInt()
                }
            }
        }
        val duration = (400 * abs(dx)) / screenWidth
        scroller?.startScroll(touchX.toInt(), 0, dx, 0, duration)
    }
}