package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 目录
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CategoriesListResponse(
    /** 总共有多少条数据 **/
    var total: Int?,
    /** 请求第几页的数据，pageNum最小值为1 **/
    var pageNum: Int?,
    /** 请求每页多少条的数据 **/
    var pageSize: Int?,
    /** 榜单书籍列表 **/
    var list: List<CategoriesListItem>?
) : Parcelable