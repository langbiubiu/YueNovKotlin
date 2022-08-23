package com.yuenov.kotlin.open.model.request

/**
 * 书籍章节下载请求body，需要转为json
 */
data class DownloadChapterRequest(
    /** 下载的书籍号 **/
    var bookId: Int,
    /** 下载的章节号列表 **/
    var chapterIdList: List<Long>,
    /** 书籍的版本号 **/
    var v: Int?
)