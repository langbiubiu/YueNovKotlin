package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
data class CheckUpdateItemInfo(
    var bookId: Int = 0,
    var chapterId: Long = 0
) : Parcelable