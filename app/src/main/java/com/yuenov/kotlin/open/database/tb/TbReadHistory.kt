package com.yuenov.kotlin.open.database.tb

import androidx.room.*

/**
 * 阅读记录
 */
@Entity(indices = [Index(value = ["id", "bookId"])])
data class TbReadHistory(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "bookId")
    var bookId: Int,

    @ColumnInfo(name = "title")
    var title: String?,

    @ColumnInfo(name = "chapterId")
    var chapterId: Long,

    @ColumnInfo(name = "page")
    var page: Int,

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
     * 是否加入书架
     */
    @ColumnInfo(name = "addBookShelf")
    var addBookShelf: Boolean,

    /**
     * 最后阅读时间
     */
    @ColumnInfo(name = "lastReadTime")
    var lastReadTime: Long
) {
    @Ignore
    constructor(
        bookId: Int,
        title: String?,
        chapterId: Long,
        page: Int,
        coverImg: String?,
        author: String?,
        addBookShelf: Boolean,
        lastReadTime: Long
    ): this(0, bookId, title, chapterId, page, coverImg, author, addBookShelf, lastReadTime)
}