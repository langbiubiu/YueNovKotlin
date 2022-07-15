package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * 下载
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class DownloadListResponse (
    var list: List<DownloadBookContentItemInfo>? = null
): Parcelable