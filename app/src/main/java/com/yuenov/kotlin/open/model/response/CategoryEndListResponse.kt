package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 完本书籍的返回结果
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryEndListResponse(
    /** 全部完结书籍的分类列表 **/
    var list: List<CategoryInfoItem>?,
    /** 请求第几页的数据，pageNum最小值为1 **/
    var pageNum: Int?,
    /** 请求每页多少条的数据 **/
    var pageSize: Int?,
    /** 总共有多少条数据 **/
    var total: Int?,
) : Parcelable
