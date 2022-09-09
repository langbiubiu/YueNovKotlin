package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuenov.kotlin.open.adapter.SpecialListAdapter
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentCategorybooklistBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.model.response.SpecialInfoItem
import com.yuenov.kotlin.open.utils.ConvertUtils
import com.yuenov.kotlin.open.viewmodel.SpecialListFragmentViewModel
import me.hgj.jetpackmvvm.ext.nav

class SpecialListFragment :
    BaseFragment<SpecialListFragmentViewModel, FragmentCategorybooklistBinding>(),
    SpecialListAdapter.IBookBlItemAdapter {

    private var adapter: SpecialListAdapter = SpecialListAdapter()

    // 每个分类翻到了第几页
    private val specialPageMap: MutableMap<String, Int> = mutableMapOf()

    // 每个分类的书籍总数
    private val specialCountMap: MutableMap<String, Int> = mutableMapOf()

    private var pageNum = 1

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            myAppTitle.getCenterView().text = "专题"
            resetVisibility(false, llCcfiFilter)
            setClickListener(myAppTitle.getLeftView()) {
                when (it) {
                    myAppTitle.getLeftView() -> nav().navigateUp()
                }
            }
            srlCcnList.setOnRefreshListener {
                mViewModel.getSpecialList(
                    pageNum,
                    InterfaceConstants.categoriesListPageSize
                )
            }
            adapter.setListener(this@SpecialListFragment)
            val padding = ConvertUtils.dp2px(15f)
            rvCcnList.setPadding(padding, padding, padding, padding)
            rvCcnList.adapter = adapter
            rvCcnList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun lazyLoadData() {
        mViewModel.getSpecialList(
            pageNum,
            InterfaceConstants.categoriesListPageSize
        )
    }

    override fun createObserver() {
        mViewModel.apply {
            getSpecialListState.observe(viewLifecycleOwner) { listState ->
                if (listState.isSuccess && !listState.isEmpty) {
                    val specialList = mutableListOf<SpecialInfoItem>()
                    specialList.addAll(listState.listData)
                    adapter.setNewInstance(specialList)
                    setCacheContent(PreferenceConstants.TYPE_FIND_INDEX, gson.toJson(specialList))
                    specialList.forEach {
                        specialPageMap[it.name!!] = 1
                    }
                }
                mViewBind.srlCcnList.isRefreshing = false
            }
            getSpecialPageState.observe(viewLifecycleOwner) {
                if (it == null || it.second.list.isNullOrEmpty()) return@observe
                specialPageMap[it.first] = it.second.pageNum!!
                specialCountMap[it.first] = it.second.total!!
                val index =
                    adapter.data.indexOfFirst { special -> special.name == it.first }
                adapter.data[index].bookList = it.second.list
                adapter.notifyItemChanged(index + 1)
            }
        }
    }

    override fun onBookBlItemClick(bookInfo: BookInfoItem) {
        logD(CLASS_TAG, "onBookBlItemClick")
        toDetail(bookInfo.bookId)
    }

    override fun onBookBlItemAdapterShowAll(special: SpecialInfoItem) {
        toSpecialBookList(special.name!!, special.id)
    }

    override fun onBookBlItemAdapterReplace(special: SpecialInfoItem) {
        special.apply {
            var pageNum = specialPageMap[name] ?: 0
            val pageSize = bookList?.size ?: 8
            val total = specialCountMap[name] ?: 0
            // 超出总数，pageNum归零
            if (pageNum * pageSize >= total) {
                pageNum = 0
            }
            mViewModel.getSpecialPage(
                pageNum + 1,
                pageSize,
                name!!,
                id
            )
        }
    }
}