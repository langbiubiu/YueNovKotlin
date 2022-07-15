package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 目录item
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookMenuItemInfo(
    var id: Long = 0,
    var name: String? = null
): Parcelable