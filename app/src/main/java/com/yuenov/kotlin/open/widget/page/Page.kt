package com.yuenov.kotlin.open.widget.page

/**
 * 一页文本的显示Model
 */
data class Page(
    val pageNum: Int = 0,
    val chapterName: String = "",
    val textLines: List<TextLine> = arrayListOf()
)
