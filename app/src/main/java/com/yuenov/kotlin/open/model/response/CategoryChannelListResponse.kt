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
    var channels: ArrayList<CategoryChannelItemInfo> = ArrayList<CategoryChannelItemInfo>()
): Parcelable