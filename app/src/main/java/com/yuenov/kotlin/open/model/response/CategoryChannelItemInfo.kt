package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryChannelItemInfo (
    var channelId: Int = 0,
    var channelName: String? = null,
    var categories: ArrayList<CategoryMenuItem>
): Parcelable