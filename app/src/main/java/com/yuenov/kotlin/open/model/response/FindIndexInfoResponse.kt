package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 发现首页
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class FindIndexInfoResponse(
    /** 发现页书籍分类列表 **/
    var list: List<CategoryInfoItem>?,
) : Parcelable