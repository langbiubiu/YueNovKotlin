package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 一些全局数据
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class AppConfigInfo(
    /** 书籍默认的分类列表 **/
    var categories: List<CategoryInfoItem>?,
    /** 字体列表 **/
    var fonts: List<FontInfoItem>?,
    /** 热搜书籍列表 **/
    var hotSearch: List<BookInfoItem>?,
    /** 接口使用的端口号列表 **/
    var ports: List<Int>?
): Parcelable