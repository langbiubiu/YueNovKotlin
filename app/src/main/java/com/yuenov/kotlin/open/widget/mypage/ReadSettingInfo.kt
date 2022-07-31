package com.yuenov.kotlin.open.widget.mypage

import com.yuenov.kotlin.open.widget.page.PageInfoConstant
import com.yuenov.kotlin.open.widget.page.PageUtil

/**
 * PageOperationView相关的配置信息
 */
data class ReadSettingInfo (
    /**
     * 背景颜色和字体颜色
     */
    var bgColor: PageBackground = PageBackground.TYPE_1,

    /**
     * 亮度值
     */
    var lightValue: Int = 0,

    /**
     * 字体大小
     */
    var fontSize: Float = PageInfoConstant.textSize,

    /**
     * 字体上下行间距，暂时没用
     */
    var lineSpacingExtra: Int = PageUtil.getLineSpacingExtra(fontSize),

    /**
     * 翻页动画类型
     */
    var pageAnimType: PageAnimationType = PageAnimationType.SIMULATION
)