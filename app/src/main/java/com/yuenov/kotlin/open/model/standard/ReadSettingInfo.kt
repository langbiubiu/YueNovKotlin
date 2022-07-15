package com.yuenov.kotlin.open.model.standard

import com.yuenov.kotlin.open.constant.ConstantPageInfo

/**
 * 设计信息
 */
data class ReadSettingInfo (
    /**
     * 亮度背景
     */
    var lightType: Int = ConstantPageInfo.lightType,

    /**
     * 亮度值
     */
    var lightValue: Int = 0,

    /**
     * 字体大小
     */
    var frontSize: Float = ConstantPageInfo.textSize,

    /**
     * 字体颜色
     */
    var frontColor: Int = ConstantPageInfo.textColor,

    /**
     * 字体上下行间距
     */
//    var lineSpacingExtra: Int = UtilityMeasure.getLineSpacingExtra(frontSize),

    /**
     * 翻页动画类型
     */
//    var pageAnimType: PageMode = PageMode.SIMULATION
)