package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class BPreviewUpdateInfo (
    var chapterId: Long = 0,
    var chapterName: String? = null,
    var time: Long = 0,

    /**
     * 状态
     */
    var chapterStatus: String? = null
): Parcelable