package com.yuenov.kotlin.open.viewmodel

import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.network.apiService
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

/**
 * 用来实现一些公用的数据操作
 */
class CommonViewModel : BaseViewModel() {

    fun updateChapterList(bookId: Int, isShowLoading: Boolean) {
        request(
            {
                val bookChapter = appDb.chapterDao.getLastChapter(bookId)
                apiService.getChapterByBookId(bookId, bookChapter?.chapterId ?: 0)
            },
            {},
            {}, isShowLoading
        )
    }
}