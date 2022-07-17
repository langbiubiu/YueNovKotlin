package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryMenuItem(
    /** 分类号 **/
    val categoryId: Int,
    /** 分类名 **/
    val categoryName: String?,
    /** 分类的封面列表，包含该分类排名前三书籍的封面路径，并非URL地址，需要手动拼接上域名+端口 **/
    val coverImgs: List<String>?
): Parcelable
