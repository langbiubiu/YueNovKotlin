package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 频道信息
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class ChannelInfoItem (
    /** 频道号 **/
    var channelId: Int,
    /** 频道名称 **/
    var channelName: String,
    /** 分类信息列表 **/
    var categories: ArrayList<CategoryInfoItem>
): Parcelable