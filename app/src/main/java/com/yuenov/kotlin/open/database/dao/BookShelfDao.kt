package com.yuenov.kotlin.open.database.dao

import androidx.room.*
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookShelf

@Dao
interface BookShelfDao {
    @Update
    fun update(vararg entities: TbBookShelf)

    @Insert
    fun insert(vararg entities: TbBookShelf)

    @Delete
    fun delete(vararg entities: TbBookShelf)

    @Query("select * from TbBookShelf order by addTime desc")
    fun getAllList(): List<TbBookShelf>?

    @Query("delete from TbBookShelf where bookId = :bookId")
    fun delete(bookId: Int)

    @Query("select * from TbBookShelf where bookId = :bookId")
    fun getEntity(bookId: Int): TbBookShelf?

    @Query("update TbBookShelf set hasUpdate = :hasUpdate , addTime = :updateTime where bookId = :bookId")
    fun updateHasUpdate(bookId: Int, hasUpdate: Boolean, updateTime: Long)

    fun exists(bookId: Int): Boolean {
        return getEntity(bookId) != null
    }

    fun addOrUpdate(tbBookShelf: TbBookShelf?) {
        tbBookShelf?.let {
            if (it.bookId < 1) return
            try {
                val existsTbBookShelf = getEntity(it.bookId)
                if (existsTbBookShelf != null) {
                    it.id = existsTbBookShelf.id
                    update(it)
                } else {
                    insert(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * 删除书架上的书，同时删除目录中的书
     *
     * @param bookId
     */
    fun deleteByBookId(bookId: Int) {
        try {
            delete(bookId)
            appDb.chapterDao.delete(bookId)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}