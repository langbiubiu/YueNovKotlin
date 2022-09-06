package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.model.request.BookCheckUpdateRequest
import com.yuenov.kotlin.open.model.response.CheckUpdateItem
import com.yuenov.kotlin.open.model.response.CheckUpdateResponse
import com.yuenov.kotlin.open.network.apiService
import com.yuenov.kotlin.open.network.stateCallback.ListDataUiState
import com.yuenov.kotlin.open.network.stateCallback.UpdateUiState
import me.hgj.jetpackmvvm.ext.launch

class BookShelfFragmentViewModel : BaseFragmentViewModel() {

    /**
     * 书架内的书籍信息
     */
    var getBookShelfListState: MutableLiveData<ListDataUiState<TbBookShelf>> = MutableLiveData()

    /**
     * 请求书籍更新信息
     */
    val checkUpdateDataState: MutableLiveData<UpdateUiState<CheckUpdateResponse>> =
        MutableLiveData()

    init {
        //TODO:先写一些假数据，后续需要删除
        val books = ArrayList<TbBookShelf>()
        books.add(
            TbBookShelf(
                1,
                74585,
                "圣墟",
                "/file/group1/book/23515770-5434-446c-9323-6924df55c018.jpg",
                "辰东",
                false,
                1657470501003
            )
        )
        books.add(
            TbBookShelf(
                2,
                56124,
                "万族之劫",
                "/file/group1/book/6b16573e-60e4-4a96-a06a-c333a0f6db8b.jpg",
                "老鹰吃小鸡",
                false,
                1657457138955
            )
        )
        books.add(
            TbBookShelf(
                3,
                56931,
                "大奉打更人",
                "/file/group1/book/f619e329-5390-487d-94f9-aec455bded20.jpg",
                "卖报小郎君",
                false,
                1657457185912
            )
        )
        books.add(
            TbBookShelf(
                4,
                78423,
                "盗墓笔记",
                "/file/group1/book/f80a88c7-f867-4dee-9f2f-20d06c57b79c.jpg",
                "南派三叔",
                false,
                1657488492943
            )
        )

        books.forEach {
            if (appDb.bookShelfDao.getEntity(it.bookId) != null)
                appDb.bookShelfDao.update(it)
            else
                appDb.bookShelfDao.insert(it)
        }
    }

    /**
     * 从数据库读取书架信息，如果读取失败也会将一个空的ArrayList写入listBookShelf，防止空异常
     */
    fun getBookShelfList() {
        launch(
            { appDb.bookShelfDao.getAllList()?.let { ArrayList(it) } },
            {
                getBookShelfListState.value = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it?.isEmpty() ?: true,
                    listData = it ?: listOf()
                )
            },
            {
                getBookShelfListState.value = ListDataUiState(
                    isSuccess = false,
                    errMessage = it.message,
                    isEmpty = true,
                    listData = listOf()
                )
            })
    }

    /**
     * 根据bookId删除书架图书
     */
    fun deleteBookShelf(bookId: Int) {
        launch(
            { appDb.bookShelfDao.deleteByBookId(bookId) },
            { //删除后更新书架信息，通知UI更新
                getBookShelfList()
            },
            { it.printStackTrace() }
        )
    }

    /**
     * 同步浏览记录
     */
    fun resetAddBookShelfStat(bookId: Int, stat: Boolean) {
        launch(
            { appDb.readHistoryDao.resetAddBookShelfStat(bookId, stat) },
            { logD(CLASS_TAG, "resetAddBookShelfStat success") },
            { it.printStackTrace() }
        )
    }

    /**
     * 书架书籍更新信息
     */
    fun checkBookShelfUpdate() {
        requestDelay(
            { //先请求更新数据
                logD(CLASS_TAG, "checkBookShelfUpdate")
                val lisUpdateInfo = appDb.chapterDao.getShelfUpdateInfo()
                val checkUpdateItems = arrayListOf<CheckUpdateItem>()
                if (!lisUpdateInfo.isNullOrEmpty()) {
                    for (book in lisUpdateInfo) {
                        checkUpdateItems.add(CheckUpdateItem(book.bookId, book.chapterId))
                    }
                }
//                val request = BookCheckUpdateRequest(checkUpdateItems)
                // TODO: 万族之劫 测试数据，等阅读模块完成后删除
                val request =
                    BookCheckUpdateRequest(
                        arrayListOf(CheckUpdateItem(56124, 1257233517545373698))
                    )
                logD(CLASS_TAG, "request json: ${gson.toJson(request)}")
                apiService.checkUpdate(request)
            },
            { response ->
                //请求成功后，更新BookShelf数据库中的hasUpdate字段
                response.updateList?.apply {
                    for (checkBook in this) {
                        val bookShelfItem = appDb.bookShelfDao.getEntity(checkBook.bookId)
                        if (bookShelfItem != null && !bookShelfItem.hasUpdate) {
                            appDb.bookShelfDao.updateHasUpdate(
                                checkBook.bookId,
                                true,
                                System.currentTimeMillis()
                            )
                        }
                    }
                }
                //更新成功后，重新获取书架书籍信息，触发observe回调，进而更新界面
                checkUpdateDataState.value = UpdateUiState(
                    isSuccess = true,
                    data = response,
                )
                getBookShelfList()
            },
            {
                checkUpdateDataState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            })
    }

}