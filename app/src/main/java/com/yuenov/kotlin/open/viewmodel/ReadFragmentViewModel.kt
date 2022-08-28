package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.database.tb.TbReadHistory
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.model.request.ChapterUpdateRequest
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.ext.launch

class ReadFragmentViewModel : BaseFragmentViewModel() {

    val hasBookShelfState: BooleanLiveData = BooleanLiveData()
    val getChapterListState: MutableLiveData<ListDataUiState<TbBookChapter>> = MutableLiveData()
    val getStartChapterAndPageState: MutableLiveData<Pair<Long, Int>?> = MutableLiveData()
    val addBookShelfState: BooleanLiveData = BooleanLiveData()
    val updateChapterContentState: BooleanLiveData = BooleanLiveData()

    fun getChapter(bookId: Int, chapterId: Long): TbBookChapter? {
        return appDb.chapterDao.getEntity(bookId, chapterId)
    }

    fun hasBookShelf(bookId: Int, hasUpdate: Boolean) {
        launch(
            {
                val hasBookShelf = appDb.bookShelfDao.exists(bookId)
                if (hasBookShelf) {
                    // 如果在书架上，则更新最后的阅读时间
                    appDb.bookShelfDao.updateHasUpdate(
                        bookId,
                        hasUpdate,
                        System.currentTimeMillis()
                    )
                }
                hasBookShelf
            },
            { hasBookShelfState.value = it }
        )
    }

    fun addBookShelf(bookInfo: BookBaseInfo) {
        launch(
            {
                appDb.bookShelfDao.addOrUpdate(
                    TbBookShelf(
                        bookInfo.bookId,
                        bookInfo.title,
                        bookInfo.coverImg,
                        bookInfo.author,
                        false,
                        System.currentTimeMillis()
                    )
                )
                appDb.readHistoryDao.resetAddBookShelfStat(bookInfo.bookId, true)
            },
            { addBookShelfState.value = true },
            { addBookShelfState.value = false }
        )
    }

    fun getChapterList(bookId: Int) {
        launch(
            {
                logD(CLASS_TAG, "getChapterList start")
                appDb.chapterDao.getChapterListByBookIdOrderByAsc(bookId)?.let { ArrayList(it) }
            },
            {
                logD(CLASS_TAG, "getChapterList end")
                getChapterListState.value = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it?.isEmpty() ?: true,
                    listData = it ?: listOf()
                )
            },
            {
                getChapterListState.value = ListDataUiState(
                    isSuccess = true,
                    errMessage = it.message,
                    isEmpty = true,
                    listData = listOf()
                )
            }
        )
    }

    fun updateChapterContent(bookId: Int, chapterId: Long) {
        requestDelay(
            { apiService.updateChapter(ChapterUpdateRequest(bookId, arrayListOf(chapterId))) },
            {
                if (!it.list.isNullOrEmpty()) {
                    appDb.chapterDao.addContent(bookId, it.list!!)
                    updateChapterContentState.value = true
                } else {
                    updateChapterContentState.value = false
                }
            },
            { updateChapterContentState.value = false },
            isShowDialog = true
        )
    }

    fun getStartChapterAndPage(bookId: Int, chapterId: Long) {
        launch(
            {
                if (chapterId > 0) {
                    val readHistory = appDb.readHistoryDao.getEntity(bookId)
                    if (readHistory != null && readHistory.chapterId == chapterId) {
                        Pair(chapterId, readHistory.page)
                    } else {
                        Pair(chapterId, 0)
                    }
                } else {
                    val firstChapter = appDb.chapterDao.getFirstChapter(bookId)
                    firstChapter?.let { Pair(firstChapter.chapterId, 0) }
                }
            },
            { getStartChapterAndPageState.value = it },
            { getStartChapterAndPageState.value = null }
        )
    }

    fun addReadHistory(bookInfo: BookBaseInfo, chapterId: Long, pageNum: Int) {
        launch(
            {
                val hasBookShelf = appDb.bookShelfDao.exists(bookInfo.bookId)
                val readHistory = TbReadHistory(
                    bookInfo.bookId,
                    bookInfo.title,
                    chapterId,
                    pageNum,
                    bookInfo.coverImg,
                    bookInfo.author,
                    hasBookShelf,
                    System.currentTimeMillis()
                )
                appDb.readHistoryDao.addOrUpdateByReadDetail(readHistory)
            }, {},
            { it.printStackTrace() }
        )
    }

}