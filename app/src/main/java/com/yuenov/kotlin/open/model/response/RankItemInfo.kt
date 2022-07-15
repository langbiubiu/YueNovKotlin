package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 榜单
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RankItemInfo (
    var rankId: Int = 0,
    var rankName: String? = null,
    var coverImgs: List<String>? = null
): Parcelable