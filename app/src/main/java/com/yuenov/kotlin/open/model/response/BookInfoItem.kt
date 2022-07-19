package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 书籍信息
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookInfoItem(
    /** 作者 **/
    val author: String?,
    /** 书籍号 **/
    val bookId: Int,
    /** 书籍所属分类 **/
    val categoryName: String,
    /** 书籍连载状态
     * END : 书籍已完结
     * SERIALIZE : 书籍连载中
     **/
    val chapterStatus: String?,
    /** 书籍的封面路径，返回的是书籍封面的路径并非URL地址，需要手动拼接上域名+端口 **/
    val coverImg: String?,
    /** 书籍内容介绍 **/
    val desc: String?,
    /** 书籍的名称 **/
    val title: String?,
    /** 书籍的字数 **/
    val word: String?
) : Parcelable