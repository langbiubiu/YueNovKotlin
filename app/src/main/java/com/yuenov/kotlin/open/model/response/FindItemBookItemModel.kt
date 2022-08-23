package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 包含书籍列表的分类信息，内容整合到[CategoryInfoItem]中
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Suppress("unused")
data class FindItemBookItemModel(
    /** 书籍列表 **/
    var bookList: List<BookInfoItem>,
    /** 只有当type=CATEGORY时才有值表示书籍分类号 **/
    var categoryId: Int?,
    /** 每个分类的名称 **/
    var categoryName: String,
    /** 每个分类的类型
     * READ_MOST : 大家都在看
     * RECENT_UPDATE : 最近更新
     * CATEGORY : 书籍分类
     *
     * 在表示完本分类时，该字段为空
     **/
    var type: String?
) : Parcelable