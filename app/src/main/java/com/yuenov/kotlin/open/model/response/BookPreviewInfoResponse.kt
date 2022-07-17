package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 预览信息
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookPreviewInfoResponse (
    /** 书籍作者 **/
    var author: String?,
    /** 书籍号 **/
    var bookId: Int,
    /** 书籍所属分类名 **/
    var categoryName: String,
    /** 总共有多少章节 **/
    var chapterNum: Int?,
    /** 书籍的封面路径，返回的是书籍封面的路径并非URL地址，需要手动拼接上域名+端口 **/
    var coverImg: String?,
    /** 书籍内容介绍 **/
    var desc: String?,
    /** 书籍的名称 **/
    var title: String?,
    /** 书籍更新信息 **/
    var update: BPreviewUpdateInfo?,
    /** 书籍的字数 **/
    var word: String?,
    /** 相关书籍推荐列表 **/
    var recommend: List<CategoriesListItem>?
): Parcelable