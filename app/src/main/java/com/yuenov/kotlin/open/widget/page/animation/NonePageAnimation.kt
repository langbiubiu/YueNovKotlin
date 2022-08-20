package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Canvas
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.widget.page.PageView

class NonePageAnimation: HorizontalPageAnimation() {

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(curBitmap, 0f, 0f, null)
    }

    // isRunning一直为false，不会调用drawMove
    override fun drawMove(canvas: Canvas) {}

    override fun startAnim() {
        isRunning = false
        if (!isCancel) changePage()
    }
}