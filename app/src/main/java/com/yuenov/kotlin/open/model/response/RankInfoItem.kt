package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 榜单
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class RankInfoItem(
    /** 榜单号 **/
    var rankId: Int,
    /** 榜单名 **/
    var rankName: String?,
    /** 榜单的封面列表，包含该榜单排名前三书籍的封面路径并非URL地址，需要手动拼接上域名+端口 **/
    var coverImgs: List<String>?
) : Parcelable