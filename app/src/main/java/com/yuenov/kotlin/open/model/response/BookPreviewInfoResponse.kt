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
    var word: String? = null,
    var title: String? = null,
    var desc: String? = null,
    var categoryName: String? = null,
    var author: String? = null,
    var bookId: Int = 0,
    var coverImg: String? = null,
    var chapterNum: Int = 0,

    /**
     * 更新信息
     */
    var update: BPreviewUpdateInfo? = null,

    /**
     * 推荐信息
     */
    var recommend: List<CategoriesListItem>? = null
): Parcelable