package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 书籍检查更新结果
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CheckUpdateItem(
    /** 需要检查更新的书籍号 **/
    var bookId: Int = 0,
    /** 需要检查更新的书籍最后一章的章节号 **/
    var chapterId: Long = 0
) : Parcelable