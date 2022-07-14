package com.yuenov.kotlin.open.model.standard

/**
 * 下载章节内容
 */
data class DownloadBookContentItemInfo(
    var id: Long = 0,
    var name: String? = null,
    var content: String? = null
)