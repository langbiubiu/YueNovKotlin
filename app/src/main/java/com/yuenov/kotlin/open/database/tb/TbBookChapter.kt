package com.yuenov.kotlin.open.database.tb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 文章目录
 */
@Entity(indices = [Index(value = ["id", "bookId", "chapterId"])])
data class TbBookChapter(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "bookId")
    var bookId: Int,

    @ColumnInfo(name = "chapterId")
    var chapterId: Long = 0,

    @ColumnInfo(name = "chapterName")
    var chapterName: String?,

    @ColumnInfo(name = "content")
    var content: String?
)