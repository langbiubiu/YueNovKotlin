package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Canvas
import com.yuenov.kotlin.open.widget.page.PageView

class NonePageAnimation(pageView: PageView) : HorizontalPageAnimation(pageView) {

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0f, 0f, null)
        } else {
            canvas.drawBitmap(nextBitmap, 0f, 0f, null)
        }
    }

    override fun drawMove(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(curBitmap, 0f, 0f, null)
        } else {
            canvas.drawBitmap(nextBitmap, 0f, 0f, null)
        }
    }

    override fun startAnim() {}
}