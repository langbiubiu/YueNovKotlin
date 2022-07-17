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
    /** 章节号 **/
    var id: Long,
    /** 章节名 **/
    var name: String?,
    /** 章节内容 **/
    var content: String?
): Parcelable