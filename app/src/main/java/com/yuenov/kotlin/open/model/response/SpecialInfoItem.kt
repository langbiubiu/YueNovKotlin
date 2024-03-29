package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 发现首页item中书的item
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class SpecialInfoItem(
    /** 专题号 **/
    var id: Int,
    /** 每个专题的名称 **/
    var name: String?,
    /** 书籍列表 **/
    var bookList: List<BookInfoItem>?
) : Parcelable