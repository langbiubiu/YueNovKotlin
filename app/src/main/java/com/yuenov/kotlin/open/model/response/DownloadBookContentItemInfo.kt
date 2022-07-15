package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 下载章节内容
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class DownloadBookContentItemInfo(
    var id: Long = 0,
    var name: String? = null,
    var content: String? = null
): Parcelable