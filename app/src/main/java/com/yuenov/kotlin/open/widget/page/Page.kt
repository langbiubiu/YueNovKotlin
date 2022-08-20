package com.yuenov.kotlin.open.widget.page

/**
 * 一页文本的显示Model
 */
data class Page(
    var pageNum: Int = 0,
    var chapterName: String = "",
    val textLines: MutableList<TextLine> = mutableListOf()
)
