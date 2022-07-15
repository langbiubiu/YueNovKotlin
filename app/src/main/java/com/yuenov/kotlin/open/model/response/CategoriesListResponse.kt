package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 目录
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CategoriesListResponse(
    var total: Int = 0,
    var page:Int = 0,
    var page_size: Int = 0,
    var list: List<CategoriesListItem>? = null
): Parcelable