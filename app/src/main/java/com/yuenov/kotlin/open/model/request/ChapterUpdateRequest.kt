package com.yuenov.kotlin.open.model.request

import java.util.ArrayList

/**
 * 更新章节详情请求body，需要转为json
 */
data class ChapterUpdateRequest(
    /** 下载的书籍号 **/
    var bookId: Int,
    /** 下载的章节号列表 **/
    var chapterIdList: ArrayList<Long>?
)