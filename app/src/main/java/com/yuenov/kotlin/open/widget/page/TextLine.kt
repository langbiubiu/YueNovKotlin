package com.yuenov.kotlin.open.widget.page

/**
 * 一行字符串的显示Model
 */
data class TextLine (
    /**
     * 内容
     */
    var text: String? = null,

    /**
     * 字体大小(sp)
     */
    var textSize: Float = 0f,

    /**
     * 字体是否加粗
     */
    var fakeBoldText: Boolean = false,

    /**
     * 内容长度
     */
    var textLength: Int = 0,

    /**
     * 内容高度
     */
    var height: Int = 0,

    /**
     * 是否顶部标题
     */
    var isChapter: Boolean = false,

    /**
     * 是否标题
     */
    var isTitle: Boolean = false,

    /**
     * 段落首行
     */
    var partFirstLine: Boolean = false
)