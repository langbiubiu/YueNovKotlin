package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 书籍最新章节信息
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookChapterNewestInfo (
    /** 最新的章节号 **/
    var chapterId: Long,
    /** 最新的章节名称 **/
    var chapterName: String?,
    /** 书籍最近更新时间 **/
    var time: Long,

    /**
     * 书籍连载状态
     * END : 书籍已完结
     * SERIALIZE : 书籍连载中
     */
    var chapterStatus: String
): Parcelable