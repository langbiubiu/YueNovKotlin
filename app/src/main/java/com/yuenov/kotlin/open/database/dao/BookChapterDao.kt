package com.yuenov.kotlin.open.database.dao

import androidx.room.*
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.model.response.ChapterInfoItem

@Dao
interface BookChapterDao {
    @Update
    fun update(vararg entities: TbBookChapter?)

    @Insert
    fun insert(vararg entities: TbBookChapter?)

    @Delete
    fun delete(vararg entities: TbBookChapter?)

    @Query("select * from TbBookChapter")
    fun getAll(): TbBookChapter?

    /**
     * 获取章节信息，不包含content字段
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select id,bookId,chapterId,chapterName,v from TbBookChapter where bookId = :bookId order by chapterId asc")
    fun getChapterList(bookId: Int): List<TbBookChapter>?

    /**
     * 获取章节信息，只有chapterId一个字段
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select id,bookId,chapterId,chapterName from TbBookChapter where bookId = :bookId order by chapterId asc")
    fun getAllColumnChapterList(bookId: Int): List<TbBookChapter>?

    @Query("select chapterId from TbBookChapter where bookId = :bookId")
    fun getChapterIds(bookId: Int): List<Long>

    @Query("select * from TbBookChapter where bookId = :bookId order by chapterId asc")
    fun getListByBookIdOrderByAsc(bookId: Int): List<TbBookChapter>?

    /**
     * content字段有内容返回0，无内容返回null
     * @param bookId
     * @return
     */
    @Query("select id,bookId,ChapterId,ChapterName,v,(case when content is null then null else '0' end) as content from TbBookChapter where bookId = :bookId order by chapterId asc")
    fun getChapterListByBookIdOrderByAsc(bookId: Int): List<TbBookChapter>?

    @Query("select * from TbBookChapter where bookId = :bookId order by chapterId desc")
    fun getListByBookIdOrderByDesc(bookId: Int): List<TbBookChapter>?

    @Query("select * from TbBookChapter where id = :id")
    fun getEntity(id: Int): TbBookChapter?

    @Query("select * from TbBookChapter where bookId = :bookId and chapterId = :chapterId")
    fun getEntity(bookId: Int, chapterId: Long): TbBookChapter?

    @Query("delete from TbBookChapter where bookId = :bookId")
    fun delete(bookId: Int)

    /**
     * 获取上一章
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId < :chapterId order by chapterId desc limit 1 offset 0")
    fun getPreEntity(bookId: Int, chapterId: Long): TbBookChapter?

    /**
     * 获取下一章
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId > :chapterId order by chapterId limit 1 offset 0")
    fun getNextEntity(bookId: Int, chapterId: Long): TbBookChapter?

    @Query("select count(*) from TbBookChapter where bookId = :bookId")
    fun getCountsByBookId(bookId: Int): Int?

    /**
     * 获取已下载的最后一章
     *
     * @param bookId
     * @return
     */
    @Query("select max(chapterId) from TbBookChapter where bookId = :bookId and [content] is not null")
    fun getLastDownloadChapterId(bookId: Int): Long

    /**
     * 获取该章节之后的章节
     *
     * @param bookId
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and content is null")
    fun getAllUnDownloadChapterId(bookId: Int): List<TbBookChapter>?

    /**
     * 获取该章节之后的章节
     *
     * @param bookId
     * @param chapterId
     * @param downloadCounts 个数
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId > :chapterId order by chapterId limit :downloadCounts offset 0")
    fun getAfterChapterId(
        bookId: Int,
        chapterId: Long,
        downloadCounts: Int
    ): List<TbBookChapter>?

    /**
     * 获取该章节之后的章节
     *
     * @param bookId
     * @param chapterId
     * @param downloadCounts 个数
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId > :chapterId and content is null order by chapterId limit :downloadCounts offset 0")
    fun getUnDownloadAfterChapterId(
        bookId: Int,
        chapterId: Long,
        downloadCounts: Int
    ): List<TbBookChapter>?

    /**
     * 获取该章节之前的章节
     *
     * @param bookId
     * @param chapterId
     * @param downloadCounts 个数
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId < :chapterId order by chapterId desc limit :downloadCounts offset 0")
    fun getBeforeChapterId(
        bookId: Int,
        chapterId: Long,
        downloadCounts: Int
    ): List<TbBookChapter>?

    /**
     * 获取所有待下载章节id
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Query("select chapterId from TbBookChapter where bookId = :bookId and chapterId > :chapterId and [content] is null order by chapterId")
    fun getAllDownloadChapterId(bookId: Int, chapterId: Long): List<Long>?

    /**
     * 获取第一章信息
     *
     * @param bookId
     * @return
     */
    @Query("select * from TbBookChapter where booKId = :bookId order by chapterId asc limit 1 offset 0")
    fun getFirstChapter(bookId: Int): TbBookChapter?

    /**
     * 获取最后一章信息
     *
     * @param bookId
     * @return
     */
    @Query("select * from TbBookChapter where booKId = :bookId order by chapterId desc limit 1 offset 0")
    fun getLastChapter(bookId: Int): TbBookChapter?

    /**
     * 只有bookId，和chapterId 两个字段
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select TbBookChapter.id,TbBookChapter.bookId,(max(chapterId)) chapterId from TbBookShelf left join TbBookChapter on TbBookShelf.bookId = TbBookChapter.bookId group by TbBookChapter.bookId")
    fun getShelfUpdateInfo(): List<TbBookChapter>?

    @Transaction
    fun addChapter(list: List<TbBookChapter>) {
        if (list.isEmpty()) return
        try {
            // 查询出已存在的
            val lisChapterIds = getChapterIds(list[0].bookId)
            val hsChapterIds = HashSet<Long>()
            lisChapterIds.forEach {
                hsChapterIds.add(it)
            }

            // 不存在才插入
            list.forEach {
                if (!hsChapterIds.contains(it.chapterId)) insert(it)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Transaction
    fun addChapter(bookId: Int, list: List<ChapterInfoItem>) {
        if (bookId < 1 || list.isEmpty()) return
        try {
            val listTbBookChapter: MutableList<TbBookChapter> = ArrayList()
            var tbBookChapter: TbBookChapter
            list.forEach { chapter ->
                tbBookChapter = TbBookChapter(bookId, chapter.id, chapter.name, chapter.content, chapter.v)
                listTbBookChapter.add(tbBookChapter)
            }
            addChapter(listTbBookChapter)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Transaction
    fun addContent(list: List<TbBookChapter>) {
        if (list.isEmpty()) return
        try {
            var existsEntity: TbBookChapter?
            list.forEach { chapter ->
                // 查询出已存在的
                existsEntity = getEntity(chapter.bookId, chapter.chapterId)
                if (existsEntity == null) {
                    insert(chapter)
                } else {
                    // 已存在 但内容为空 则更新
                    existsEntity!!.apply {
                        if (content.isNullOrEmpty()) {
                            chapterName = chapter.chapterName
                            content = chapter.content
                            v = chapter.v
                            update(this)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Transaction
    fun addContent(bookId: Int, list: List<ChapterInfoItem>) {
        if (bookId < 1 || list.isEmpty()) return
        try {
            val listAdd = list.map { chapter ->
                TbBookChapter(bookId, chapter.id, chapter.name, chapter.content, chapter.v)
            }
            addContent(listAdd)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}