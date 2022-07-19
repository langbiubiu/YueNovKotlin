package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 分类
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryChannelListResponse (
    /** 获取所有的频道分类列表，目前有男生频道和女生频道 **/
    var channels: ArrayList<ChannelInfoItem>?
): Parcelable