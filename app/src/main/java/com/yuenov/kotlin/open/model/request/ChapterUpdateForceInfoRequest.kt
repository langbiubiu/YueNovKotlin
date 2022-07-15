package com.yuenov.kotlin.open.model.request

import com.yuenov.kotlin.open.application.gson
import java.util.ArrayList

/**
 * 更新章节详情请求body，需要转为json
 */
data class ChapterUpdateForceInfoRequest(
    var bookId: Int = 0,
    var chapterIdList: ArrayList<Long>? = null
)