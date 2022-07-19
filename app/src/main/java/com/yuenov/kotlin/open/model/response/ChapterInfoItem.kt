package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.yuenov.kotlin.open.network.ApiService

/**
 * 章节信息
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class ChapterInfoItem(
    /** 章节内容 **/
    var content: String?,
    /** 章节号 **/
    var id: Long,
    /** 章节名 **/
    var name: String?,
    /** 书籍的版本号，非常重要，书籍下载接口[ApiService.downloadChapter()]需要传递这个参数 **/
    var v: Int
): Parcelable