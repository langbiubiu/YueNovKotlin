package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 发现首页item中书的item
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class SpecialItemModel (
    var id: Int = 0,
    var name: String? = null,
    var page: Int = 1,
    var bookList: List<CategoriesListItem> = ArrayList()
): Parcelable