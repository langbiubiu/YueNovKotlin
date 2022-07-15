package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 榜单列表
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RankListInfo (
    var channelId: Int = 0,
    var channelName: String? = null,
    var ranks: List<RankItemInfo>? = null
): Parcelable