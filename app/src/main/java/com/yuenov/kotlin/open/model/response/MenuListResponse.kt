package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 目录
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class MenuListResponse (
    /** 作者 **/
    var author: String?,
    /** 书籍号 **/
    var bookId: Int,
    /** 返回的是书籍封面的路径并非URL地址，需要手动拼接上域名+端口 **/
    var coverImg: String?,
    /** 书籍号 **/
    var desc: String?,
    /** 书籍的名称 **/
    var title: String?,
    /** 书籍的字数 **/
    var word: String?,
    /** 书籍的目录列表 **/
    var chapters: List<BookMenuItemInfo>
): Parcelable