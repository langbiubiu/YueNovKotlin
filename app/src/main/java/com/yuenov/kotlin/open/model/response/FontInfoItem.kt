package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class FontInfoItem(
    /** 字体名称**/
    val fontName: String,
    /** 字体路径，并非URL地址，需要手动拼接上域名+端口 **/
    val path: String
) : Parcelable
