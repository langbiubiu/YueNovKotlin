package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 专题首页
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class SpecialListResponse (
    /** 全部的专题列表 **/
    var specialList: List<SpecialInfoItem>?
): Parcelable