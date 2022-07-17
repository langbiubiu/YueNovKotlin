package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 发现首页item中书的item
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class FindItemBookItemModel (
    /** 书籍列表 **/
    var bookList: List<CategoriesListItem>,
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
): Parcelable