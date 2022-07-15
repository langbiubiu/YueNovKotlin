package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryMenuItem(
    val categoryId: Int,
    val categoryName: String,
    val coverImg: String,
    val coverImgs: List<String>
): Parcelable
