package com.yuenov.kotlin.open.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class BookCheckUpdateResponse(
    /** 检查更新的书籍列表 **/
    var updateList: List<CheckUpdateItemInfo>?
) : Parcelable