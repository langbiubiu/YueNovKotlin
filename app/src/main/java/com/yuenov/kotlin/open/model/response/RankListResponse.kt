package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 榜单
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RankListResponse(
    /** 获取所有的频道榜单，目前有男生频道和女生频道 **/
    var channels: ArrayList<RankListInfoItem>?
) : Parcelable