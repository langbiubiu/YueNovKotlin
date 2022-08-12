package com.yuenov.kotlin.open.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.database.dao.BookChapterDao
import com.yuenov.kotlin.open.database.dao.BookShelfDao
import com.yuenov.kotlin.open.database.dao.CacheDao
import com.yuenov.kotlin.open.database.dao.ReadHistoryDao
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.database.tb.TbCache
import com.yuenov.kotlin.open.database.tb.TbReadHistory
import java.util.Locale

val appDb by lazy { AppDatabase.createDatabase() }

@Database(
    entities = [TbBookChapter::class, TbReadHistory::class, TbBookShelf::class, TbCache::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val chapterDao: BookChapterDao
    abstract val readHistoryDao: ReadHistoryDao
    abstract val bookShelfDao: BookShelfDao
    abstract val cacheDao: CacheDao

    override fun clearAllTables() {}

    companion object {
        private const val DATABASE_NAME = "BookInfo.db"

        fun createDatabase() = Room
            .databaseBuilder(MyApplication.appContext, AppDatabase::class.java, DATABASE_NAME)
            .setJournalMode(JournalMode.TRUNCATE)
            .allowMainThreadQueries()
            .addCallback(dbCallback)
            .build()

        private val dbCallback = object : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                db.setLocale(Locale.CHINESE)
            }

        }
    }
}