package com.yuenov.kotlin.open.model.standard

import com.yuenov.kotlin.open.constant.InterFaceConstants.CHAPTER_STATUS_END


/**
 * 图书基本信息，在不同界面间传递图书参数时提供基本信息，例如startActivity
 */
data class BookBaseInfo(
    var bookId: Int = 0,
    var title: String,
    var author: String,
    var coverImg: String? = null,
    var chapterStatus: String = CHAPTER_STATUS_END
)