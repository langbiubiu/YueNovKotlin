package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.drawable.GradientDrawable
import android.graphics.Canvas
import android.graphics.Rect
import com.yuenov.kotlin.open.widget.page.PageView
import kotlin.math.abs

class CoverPageAnimation : HorizontalPageAnimation() {
    private lateinit var mSrcRect: Rect
    private lateinit var mDestRect: Rect
    private val mBackShadowDrawableLR: GradientDrawable
    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(curBitmap, 0f, 0f, null)
    }

    override fun drawMove(canvas: Canvas) {
        when (direction) {
            Direction.NEXT -> {
                var dis = (viewWidth - startX + touchX).toInt()
                if (dis > viewWidth) {
                    dis = viewWidth
                }
                //计算bitmap截取的区域
                mSrcRect.left = viewWidth - dis
                //计算bitmap在canvas显示的区域
                mDestRect.right = dis
                canvas.drawBitmap(nextBitmap, 0f, 0f, null)
                canvas.drawBitmap(curBitmap, mSrcRect, mDestRect, null)
                addShadow(dis, canvas)
            }
            else -> {
                mSrcRect.left = (viewWidth - touchX).toInt()
                mDestRect.right = touchX.toInt()
                canvas.drawBitmap(curBitmap, 0f, 0f, null)
                canvas.drawBitmap(nextBitmap, mSrcRect, mDestRect, null)
                addShadow(touchX.toInt(), canvas)
            }
        }
    }

    //添加阴影
    private fun addShadow(left: Int, canvas: Canvas?) {
        mBackShadowDrawableLR.setBounds(left, 0, left + 30, screenHeight)
        mBackShadowDrawableLR.draw(canvas!!)
    }

    override fun setView(view: PageView) {
        super.setView(view)
        mSrcRect = Rect(0, 0, viewWidth, viewHeight)
        mDestRect = Rect(0, 0, viewWidth, viewHeight)
    }

    override fun startAnim() {
        super.startAnim()
        val dx: Int
        when (direction) {
            Direction.NEXT -> if (isCancel) {
                var dis = (viewWidth - startX + touchX).toInt()
                if (dis > viewWidth) {
                    dis = viewWidth
                }
                dx = viewWidth - dis
            } else {
                dx = (-(touchX + (viewWidth - startX))).toInt()
            }
            else -> dx = if (isCancel) {
                (-touchX).toInt()
            } else {
                (viewWidth - touchX).toInt()
            }
        }

        //滑动速度保持一致
        val duration: Int = 400 * abs(dx) / viewWidth
        scroller?.startScroll(touchX.toInt(), 0, dx, 0, duration)
    }

    init {
        val mBackShadowColors = intArrayOf(0x66000000, 0x00000000)
        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        mBackShadowDrawableLR.gradientType = GradientDrawable.LINEAR_GRADIENT
    }
}