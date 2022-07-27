package com.yuenov.kotlin.open.database.tb

import androidx.room.*

/**
 * 缓存
 */
@Entity(indices = [Index(value = ["id", "cType"])])
data class TbCache(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "cType")
    var cType: String?,

    @ColumnInfo(name = "cContent")
    var cContent: String?
) {
    @Ignore
    constructor(
        cType: String?,
        cContent: String?
    ): this(0, cType, cContent)
}