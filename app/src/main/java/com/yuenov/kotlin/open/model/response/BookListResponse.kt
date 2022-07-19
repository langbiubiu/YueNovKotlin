package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 书籍列表的请求结果，可能是某一分类、榜单或相关推荐下的书籍列表或是关键字的搜索结果
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookListResponse(
    /** 总共有多少条数据 **/
    var total: Int?,
    /** 请求第几页的数据，pageNum最小值为1 **/
    var pageNum: Int?,
    /** 请求每页多少条的数据 **/
    var pageSize: Int?,
    /** 榜单书籍列表 **/
    var list: List<BookInfoItem>?
) : Parcelable