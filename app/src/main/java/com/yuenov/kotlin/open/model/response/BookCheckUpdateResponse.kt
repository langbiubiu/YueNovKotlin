package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class BookCheckUpdateResponse(
    var updateList: List<CheckUpdateItemInfo>? = null
) : Parcelable