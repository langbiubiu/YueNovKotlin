package com.yuenov.kotlin.open.model.request

import com.yuenov.kotlin.open.model.response.CheckUpdateItem

/**
 * 批量检查书籍是否有更新，需要转为json
 */
data class BookCheckUpdateRequest(
    var books: List<CheckUpdateItem>
)
