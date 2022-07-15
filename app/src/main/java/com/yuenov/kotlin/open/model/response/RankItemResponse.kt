package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 榜单
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RankItemResponse (
    var channels: ArrayList<RankListInfo>? = null
): Parcelable