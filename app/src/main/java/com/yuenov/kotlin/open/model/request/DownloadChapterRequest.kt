package com.yuenov.kotlin.open.model.request

import java.util.ArrayList

/**
 * 书籍章节下载请求body，需要转为json
 */
data class DownloadChapterRequest (
    var bookId: Int = 0,
    var chapterIdList: List<Long> = ArrayList(),
    var v: Int = 0
)