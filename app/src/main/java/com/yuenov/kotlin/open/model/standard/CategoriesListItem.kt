package com.yuenov.kotlin.open.model.standard

data class CategoriesListItem(
    val categoryName: String,
    val bookId: Int,
    val title: String,
    val author: String,
    val coverImg: String,
    val word: String,
    val desc: String,
    val chapterStatus: String
)