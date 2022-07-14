package com.yuenov.kotlin.open.model

import com.yuenov.kotlin.open.model.standard.CategoriesListItem
import com.yuenov.kotlin.open.model.standard.CategoryMenuItem

/**
 * 用来存储一些全局数据
 */
data class AppConfigInfo(
    //分类列表
    var categories: List<CategoryMenuItem>?,
    //热搜书籍列表
    var hotSearch: List<CategoriesListItem>?,
    //接口使用的端口号列表
    var ports: List<Int>?
) {
    fun isEmpty(): Boolean {
        return !categories.isNullOrEmpty() &&
                !hotSearch.isNullOrEmpty() &&
                !ports.isNullOrEmpty()
    }
}