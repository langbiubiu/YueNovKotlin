package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryChannelItemInfo (
    /** 频道号 **/
    var channelId: Int,
    /** 频道名称 **/
    var channelName: String,
    /** 分类信息列表 **/
    var categories: ArrayList<CategoryMenuItem>
): Parcelable