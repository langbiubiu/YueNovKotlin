package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 分类信息
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryInfoItem(
    /** 书籍列表 **/
    var bookList: List<BookInfoItem>?,
    /** 只有当type=CATEGORY时才有值表示书籍分类号 **/
    val categoryId: Int?,
    /** 分类名 **/
    val categoryName: String?,
    /** 分类的封面列表，包含该分类排名前三书籍的封面路径，并非URL地址，需要手动拼接上域名+端口 **/
    val coverImgs: List<String>?,
    /** 每个分类的类型
     * READ_MOST : 大家都在看
     * RECENT_UPDATE : 最近更新
     * CATEGORY : 书籍分类
     *
     * 在表示完本分类时，该字段为空
     **/
    var type: String?
) : Parcelable
