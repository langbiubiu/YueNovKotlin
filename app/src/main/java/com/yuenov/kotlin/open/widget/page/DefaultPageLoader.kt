package com.yuenov.kotlin.open.widget.page

import androidx.annotation.ColorRes
import com.yuenov.kotlin.open.widget.page.animation.NonePageAnimation
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation

/**
 * 一个默认的PageLoader实现
 */
class DefaultPageLoader(val view: PageView): IPagerLoader {

    override fun getBattery(): Int = 0
    override fun getContent(): String = "章节内容"
    override fun getTime(): String = "00:00"
    override fun getTitle(): String = "章节名"
    override fun getProgressPercent(): String = "0.0%"
    override fun getBgColor(): Int = android.R.color.white
    override fun getTextColor(): Int = android.R.color.black
    override fun getTextSize(): Float = 18f
    override fun getPageAnimation(): PageAnimation = NonePageAnimation(view)
    override fun allowTurnPage(isPrev: Boolean): Boolean = false
    override fun hasNextContent(isPrev: Boolean): Boolean = false

}