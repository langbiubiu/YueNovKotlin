package com.yuenov.kotlin.open.widget.page

import com.yuenov.kotlin.open.widget.page.animation.NonePageAnimation
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation

/**
 * 一个默认的PageLoader实现
 */
class DefaultPageLoader : IPagerLoader {
    private val animation = NonePageAnimation()

    override fun getBattery(): Int = 0
    override fun getContent(): String = "章节内容"
    override fun getNextContent(isNext: Boolean): String = ""
    override fun getTime(): String = "00:00"
    override fun getTitle(): String = "章节名"
    override fun getNextTitle(isNext: Boolean): String = ""
    override fun getProgress(pageNum: Int, pageCount: Int, isNextChapter: Boolean): String = "0.00%"
    override fun getBgColor(): Int = android.R.color.white
    override fun getTextColor(): Int = android.R.color.black
    override fun getTextSize(): Float = 18f
    override fun getPageAnimation(): PageAnimation = animation
    override fun canTouch(): Boolean = true
    override fun allowPageAnimation(): Boolean = false
    override fun hasNextChapter(isNext: Boolean): Boolean = false

}