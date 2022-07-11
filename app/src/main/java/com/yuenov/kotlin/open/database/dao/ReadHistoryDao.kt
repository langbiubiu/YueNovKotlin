package com.yuenov.kotlin.open.database.dao

import androidx.room.*
import com.yuenov.kotlin.open.database.tb.TbReadHistory
import java.lang.Exception

@Dao
abstract class ReadHistoryDao {
    @Update
    abstract fun update(vararg entities: TbReadHistory?)
    @Insert
    abstract fun insert(vararg entities: TbReadHistory?)
    @Delete
    abstract fun delete(vararg entities: TbReadHistory?)

    /**
     * 获取某条阅读记录
     */
    @Query("select * from TbReadHistory where bookId = :bookId")
    abstract fun getEntity(bookId: Int): TbReadHistory?

    /**
     * 获取所有阅读记录
     */
    @get:Query("select * from TbReadHistory order by lastReadTime desc")
    abstract val allList: List<TbReadHistory?>?

    @Query("delete from TbReadHistory")
    abstract fun clear()

    @Query("delete from TbReadHistory where bookId = :bookId")
    abstract fun deleteByBookId(bookId: Int)

    @Query("update TbReadHistory set addBookShelf = :stat where bookId = :bookId")
    abstract fun resetAddBookShelfStat(bookId: Int, stat: Boolean)

    fun exists(bookId: Int): Boolean {
        return getEntity(bookId) != null
    }

    /**
     * 是否阅读过，有章节id即表示阅读过
     *
     * @param bookId
     * @return
     */
    fun existsRealRead(bookId: Int): Boolean {
        return getEntity(bookId)?.chapterId!! > 0
    }

    fun addOrUpdateByPreview(entity: TbReadHistory) {
        try {
            val existsEntity = getEntity(entity.bookId)
            if (existsEntity == null) {
                insert(entity)
            } else {
                entity.id = existsEntity.id
                if (entity.chapterId < 1) {
                    entity.chapterId = existsEntity.chapterId
                    entity.page = existsEntity.page
                }
                update(entity)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun addOrUpdateByReadDetail(entity: TbReadHistory) {
        try {
            val existsEntity = getEntity(entity.bookId)
            if (existsEntity == null) {
                insert(entity)
            } else {
                entity.id = existsEntity.id
                update(entity)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}