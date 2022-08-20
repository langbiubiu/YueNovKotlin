package com.yuenov.kotlin.open.widget.page

import androidx.annotation.ColorRes
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation

/**
 * 为PageView提供数据，类似Adapter
 */
interface IPagerLoader {
    // 可能需要添加一个listener来回调这些
//    fun onPreChapter(newBookChapter: TbBookChapter)
//    fun onNextChapter(newBookChapter: TbBookChapter)
//    fun onTurnPage()
//    fun showAd()

    /**
     * 获取电池电量，值范围0-100
     */
    fun getBattery(): Int

    /**
     * 获取显示内容（章节内容）
     */
    fun getContent(): String

    /**
     * 是否存在下一章内容
     * @param isNext 翻页方向，true为向前，false为向后
     */
    fun getNextContent(isNext: Boolean): String

    /**
     * 获取时间
     */
    fun getTime(): String

    /**
     * 获取章节名
     */
    fun getTitle(): String

    /**
     * 是否存在下一章名称
     * @param isNext 翻页方向，true为向前，false为向后
     */
    fun getNextTitle(isNext: Boolean): String

    /**
     * 获取阅读进度百分比
     */
    fun getProgress(pageNum: Int, pageCount: Int, isNextChapter: Boolean): String

    /**
     * 获取背景色
     */
    @ColorRes
    fun getBgColor(): Int

    /**
     * 获取字体颜色
     */
    @ColorRes
    fun getTextColor(): Int

    /**
     * 获取字体大小
     */
    fun getTextSize(): Float

    /**
     * 获取翻页动画
     */
    fun getPageAnimation(): PageAnimation

    /**
     * 是否允许响应触摸事件
     */
    fun canTouch(): Boolean

    /**
     * 是否允许翻页，当主界面其他UI组件正在显示时，需要暂时拦截翻页动作
     */
    fun allowPageAnimation(): Boolean

    /**
     * 是否存在下一张
     */
    fun hasNextChapter(isNext: Boolean): Boolean

}