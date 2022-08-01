package com.yuenov.kotlin.open.widget.page

import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation

interface IPagerLoader {
    fun onPreChapter(newBookChapter: TbBookChapter)
    fun onNextChapter(newBookChapter: TbBookChapter)
    fun onTurnPage()
    fun showAd()
    fun updateBattery(value: Int)
    fun updateTime()
    fun openChapter(chapterId: Long)
    fun setBgColor(bgColor: Int)
    fun setTextColor(textColor: Int)
    fun setFontSize(fontSize: Float)
    fun setAnimation(pageAnimation: PageAnimation)
}