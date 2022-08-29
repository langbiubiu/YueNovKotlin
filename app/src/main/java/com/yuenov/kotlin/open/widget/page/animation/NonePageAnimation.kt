package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.Canvas

class NonePageAnimation : HorizontalPageAnimation() {

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(curBitmap, 0f, 0f, null)
        //回调不能放这里，因为drawStatic可能会连续触发两次
//        if (!isCancel) pageView.turnPageCompleted()
    }

    // isRunning一直为false，不会调用drawMove
    override fun drawMove(canvas: Canvas) {}

    override fun startAnim() {
        super.startAnim()
        isRunning = false
        // 一般情况下，changePage和turnPageCompleted会在动画结束后被调用，但是NonePageAnimation不执行动画，
        // 因此在startAnim中主动调用，确保bitmap和数据完成交换，以及回调的正常调用
        if (!isCancel) {
            changePage()
            pageView.turnPageCompleted()
        }
    }
}