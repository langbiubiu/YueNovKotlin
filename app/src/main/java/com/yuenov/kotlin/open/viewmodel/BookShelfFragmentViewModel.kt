package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.model.request.BookCheckUpdateRequest
import com.yuenov.kotlin.open.model.response.CheckUpdateItem
import com.yuenov.kotlin.open.model.response.CheckUpdateResponse
import com.yuenov.kotlin.open.network.apiService
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.demo.app.network.stateCallback.ListDataUiState
import me.hgj.jetpackmvvm.demo.app.network.stateCallback.UpdateUiState
import me.hgj.jetpackmvvm.ext.launch
import me.hgj.jetpackmvvm.ext.request

class BookShelfFragmentViewModel : BaseViewModel() {

    /**
     * 书架内的书籍信息
     */
    var bookShelfDataState: MutableLiveData<ListDataUiState<TbBookShelf>> = MutableLiveData()

    /**
     * 请求书籍更新信息
     */
    val checkUpdateDataState: MutableLiveData<UpdateUiState<CheckUpdateResponse>> = MutableLiveData()

//    /*
    init {
        //TODO:先写一些假数据
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
                54037,
                "我师兄实在太稳健了",
                "/file/group1/book/8c1dd37f-b32e-4769-a14b-eec5a7692a6c.jpg",
                "言归正传",
                false,
                1657457196445
            )
        )
        books.add(
            TbBookShelf(
                5,
                43928,
                "三寸人间",
                "/file/group1/book/1e8c878e-4453-4d01-8fac-3a455bc82da6.jpg",
                "耳根",
                false,
                1657457206820
            )
        )
        books.add(
            TbBookShelf(
                6,
                30746,
                "遮天",
                "/file/group1/book/2ea54da7-235f-403f-9da0-cc1d46edf682.jpg",
                "辰东",
                false,
                1657457219468
            )
        )
        books.add(
            TbBookShelf(
                7,
                93547,
                "天降鬼才",
                "/file/group1/book/9c9750a4-6084-4f32-a366-5c81556ca35d.jpg",
                "武异",
                false,
                1657465315064
            )
        )
        books.add(
            TbBookShelf(
                8,
                35707,
                "斗罗大陆",
                "/file/group1/book/d61fdbfe-58b3-4c52-9129-27c7bc5f9c0c.jpg",
                "唐家三少",
                false,
                1657465323064
            )
        )
        books.add(
            TbBookShelf(
                9,
                47813,
                "一剑独尊",
                "/file/group1/book/3e505d41-edf2-4c7f-b71b-3d1d00e52fd3.jpg",
                "青鸾峰上",
                false,
                1657465336151
            )
        )
        books.add(
            TbBookShelf(
                10,
                41255,
                "剑来",
                "/file/group1/book/41a98b0b-5cd4-45d2-93c9-5f4ad5d9bace.jpg",
                "烽火戏诸侯",
                false,
                1657465346486
            )
        )
        books.add(
            TbBookShelf(
                11,
                43422,
                "伏天氏",
                "/file/group1/book/0f07a0b0-0b38-4d5d-990e-b4e9ac0b7cf6.jpg",
                "净无痕",
                false,
                1657465357008
            )
        )
        books.add(
            TbBookShelf(
                12,
                34798,
                "元尊",
                "/file/group1/book/aaed46d4-1499-4628-94a4-af9189002293.jpg",
                "天蚕土豆",
                false,
                1657465368940
            )
        )
        books.add(
            TbBookShelf(
                13,
                48772,
                "斗罗大陆4终极斗罗",
                "/file/group1/book/2fcef4e6-8a20-4ec7-bddf-b0a55c0bacb7.jpg",
                "唐家三少",
                false,
                1657465380924
            )
        )

        books.forEach {
            if (appDb.bookShelfDao.getEntity(it.bookId) != null)
                appDb.bookShelfDao.update(it)
            else
                appDb.bookShelfDao.insert(it)
        }
    }
//    */

    /**
     * 从数据库读取书架信息，如果读取失败也会将一个空的ArrayList写入listBookShelf，防止空异常
     */
    fun getBookShelfData() {
        launch(
            { appDb.bookShelfDao.getAllList()?.let { ArrayList(it) } },
            {
                bookShelfDataState.value = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it?.isEmpty() ?: true,
                    listData = it?: arrayListOf())
            },
            {
                bookShelfDataState.value = ListDataUiState(
                    isSuccess = false,
                    errMessage = it.message,
                    isEmpty = true,
                    listData = arrayListOf())
            })
    }

    /**
     * 根据bookId删除书架图书
     */
    fun deleteBookShelfData(bookId: Int) {
        launch(
            { appDb.bookShelfDao.deleteByBookId(bookId) },
            { //删除后更新书架信息，通知UI更新
                getBookShelfData()
            },
            {
                it.printStackTrace()
            }
        )
    }

    /**
     * 同步浏览记录
     */
    fun resetAddBookShelfStat(bookId: Int, stat: Boolean) {
        launch(
            { appDb.readHistoryDao.resetAddBookShelfStat(bookId, stat) },
            {
                logd(CLASS_TAG, "resetAddBookShelfStat success")
            },
            {
                it.printStackTrace()
            }
        )
    }

    /**
     * 书架书籍更新信息
     */
    fun checkBookShelfUpdate() {
        request(
            { //先请求更新数据
                val lisUpdateInfo = appDb.chapterDao.getShelfUpdateInfo()
                val checkUpdateItems = arrayListOf<CheckUpdateItem>()
                if (!lisUpdateInfo.isNullOrEmpty()) {
                    for (book in lisUpdateInfo) {
                        checkUpdateItems.add(CheckUpdateItem(book!!.bookId, book.chapterId))
                    }
                }
//                val request = BookCheckUpdateRequest(checkUpdateItems)
                // TODO: 万族之劫 测试数据，等阅读模块完成后删除
                val request =
                    BookCheckUpdateRequest(
                        arrayListOf(CheckUpdateItem(56124, 1257233517545373698)))
                logd(CLASS_TAG, "request json: ${gson.toJson(request)}")
                apiService.checkUpdate(request)
            },
            { response ->
                //请求成功后，更新BookShelf数据库中的hasUpdate字段
                launch(
                    {
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
                    },
                    {
                        //更新成功后，重新获取书架书籍信息，触发observe回调，进而更新界面
                        checkUpdateDataState.value = UpdateUiState(
                            isSuccess = true,
                            data = response,
                        )
                        getBookShelfData()
                    },
                    {
                        checkUpdateDataState.value = UpdateUiState(
                            isSuccess = false,
                            errorMsg = it.message
                        )
                    })
            },
            {
                checkUpdateDataState.value = UpdateUiState(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
            })
    }

}