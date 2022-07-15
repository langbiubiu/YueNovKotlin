package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 专题首页
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class IndexSpecialListResponse (
    var specialList: List<SpecialItemModel> = ArrayList<SpecialItemModel>()
): Parcelable