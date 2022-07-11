package com.yuenov.kotlin.open.model.standard

data class CategoryMenuItem(
    val categoryId: Int,
    val categoryName: String,
    val coverImg: String,
    val coverImgs: List<String>
)
