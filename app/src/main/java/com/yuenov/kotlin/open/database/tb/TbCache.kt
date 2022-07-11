package com.yuenov.kotlin.open.database.tb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 缓存
 */
@Entity(indices = [Index(value = ["id", "cType"])])
data class TbCache(
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @JvmField
    @ColumnInfo(name = "cType")
    var cType: String?,

    @JvmField
    @ColumnInfo(name = "cContent")
    var cContent: String?
)