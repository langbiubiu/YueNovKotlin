package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 榜单列表
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RankListInfoItem (
    /** 频道号 **/
    var channelId: Int,
    /** 频道名称 **/
    var channelName: String?,
    /** 榜单信息列表 **/
    var ranks: List<RankInfoItem>?
): Parcelable