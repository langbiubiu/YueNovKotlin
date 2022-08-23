package com.yuenov.kotlin.open.model.standard

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * 图书基本信息，在不同界面间传递图书参数时提供基本信息，例如startActivity
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookBaseInfo(
    var bookId: Int = 0,
    var title: String?,
    var author: String?,
    var coverImg: String?,
    var chapterStatus: String?
) : Parcelable