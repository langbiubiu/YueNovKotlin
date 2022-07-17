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
    /** 全部的专题列表 **/
    var specialList: List<SpecialItemModel>?
): Parcelable