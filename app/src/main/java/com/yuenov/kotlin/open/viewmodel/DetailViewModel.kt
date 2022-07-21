package com.yuenov.kotlin.open.viewmodel

import androidx.lifecycle.MutableLiveData
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.demo.app.network.stateCallback.ListDataUiState
import me.hgj.jetpackmvvm.ext.launch

class DetailViewModel: BaseViewModel() {

    /**
     * 书架信息，仅用于界面初始化，后续的增删改不需要变更这里的数据，仅修改UI adapter内的数据
     */
    var bookShelfDataState: MutableLiveData<ListDataUiState<TbBookShelf>> = MutableLiveData()

    init {
        //先写一些假数据
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
    fun getBookShelfData(): ArrayList<TbBookShelf> {
        return try {
            appDb.bookShelfDao.getAllList()?.let { ArrayList(it) } ?: arrayListOf()
        } catch (ex:Exception) {
            arrayListOf()
        }
    }
}