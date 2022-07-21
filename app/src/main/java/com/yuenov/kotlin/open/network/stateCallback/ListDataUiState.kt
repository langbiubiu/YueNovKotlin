package me.hgj.jetpackmvvm.demo.app.network.stateCallback

import com.yuenov.kotlin.open.database.tb.TbBookShelf

/**
 * 作者　: hegaojian
 * 时间　: 2020/3/3
 * 描述　: 列表数据状态类
 */
data class ListDataUiState<T>(
    //是否请求成功
    val isSuccess: Boolean,
    //错误消息 isSuccess为false才会有
    val errMessage: String? = null,
//    //是否为刷新，用不到
//    val isRefresh: Boolean = false,
    //是否为空
    val isEmpty: Boolean = false,
//    //是否还有更多，用不到
//    val hasMore: Boolean = false,
//    //是第一页且没有数据，用不到
//    val isFirstEmpty: Boolean = false,
    //列表数据
    val listData: ArrayList<T> = arrayListOf()
)