package com.yuenov.kotlin.open.database.dao

import androidx.room.*
import com.yuenov.kotlin.open.database.tb.TbCache

@Dao
interface CacheDao {
    @Update
    fun update(vararg entities: TbCache?)

    @Insert
    fun insert(vararg entities: TbCache?)

    @Delete
    fun delete(vararg entities: TbCache?)

    @Query("select * from TbCache where cType = :cType")
    fun getEntity(cType: String?): TbCache?

    fun exists(cType: String?): Boolean {
        return !getEntity(cType)?.cContent.isNullOrEmpty()
    }

    @Transaction
    fun addOrUpdate(tbCache: TbCache?) {
        if (tbCache == null) return

        try {
            val entity = getEntity(tbCache.cType)
            if (entity == null) {
                insert(tbCache)
            } else {
                tbCache.id = entity.id
                update(tbCache)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}