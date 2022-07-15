package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 目录
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class MenuListResponse (
    var id: Int = 0,
    var title: String? = null,
    var author: String? = null,
    var desc: String? = null,
    var word: String? = null,
    var coverImg: String? = null,
    var chapters: List<BookMenuItemInfo>? = null
): Parcelable