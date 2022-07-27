package com.yuenov.kotlin.open.database.tb

import androidx.room.*

/**
 * 书架
 */
@Entity(indices = [Index(value = ["id", "bookId"])])
data class TbBookShelf(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "bookId")
    var bookId: Int,

    @ColumnInfo(name = "title")
    var title: String?,

    /**
     * 图片地址
     */
    @ColumnInfo(name = "coverImg")
    var coverImg: String?,

    /**
     * 作者
     */
    @ColumnInfo(name = "author")
    var author: String?,

    /**
     * 需要更新
     */
    @ColumnInfo(name = "hasUpdate")
    var hasUpdate: Boolean = false,

    /**
     * 加入时间
     */
    @ColumnInfo(name = "addTime")
    var addTime: Long = 0
) {
    @Ignore
    constructor(
        bookId: Int,
        title: String?,
        coverImg: String?,
        author: String?,
        hasUpdate: Boolean,
        addTime: Long
    ) : this(0, bookId, title, coverImg, author, hasUpdate, addTime)
}