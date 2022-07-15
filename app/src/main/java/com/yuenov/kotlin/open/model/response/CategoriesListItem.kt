package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoriesListItem(
    val categoryName: String,
    val bookId: Int,
    val title: String,
    val author: String,
    val coverImg: String,
    val word: String,
    val desc: String,
    val chapterStatus: String
): Parcelable