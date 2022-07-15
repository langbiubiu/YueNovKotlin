package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 发现首页
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class FindIndexInfoResponse (
    var list: List<FindItemBookItemModel> = ArrayList<FindItemBookItemModel>(),
    var total: Int = 0,
    var pageNum: Int = 0,
    var pageSize: Int = 0
): Parcelable