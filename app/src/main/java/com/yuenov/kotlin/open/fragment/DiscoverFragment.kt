package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.yuenov.kotlin.open.adapter.CategoryListAdapter
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentDiscoverBinding
import com.yuenov.kotlin.open.databinding.ViewDiscoverCategoryBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.model.response.CategoryInfoItem
import com.yuenov.kotlin.open.viewmodel.DiscoverFragmentViewModel

/**
 * TODO:发现界面
 */
class DiscoverFragment : BaseFragment<DiscoverFragmentViewModel, FragmentDiscoverBinding>(),
    CategoryListAdapter.IBookBlItemAdapter {

    private lateinit var categoryBind: ViewDiscoverCategoryBinding
    private var categoryAdapter: CategoryListAdapter = CategoryListAdapter()

    // 每个分类翻到了第几页
    private val categoryPageMap: MutableMap<String, Int> = mutableMapOf()

    // 每个分类的书籍总数
    private val categoryCountMap: MutableMap<String, Int> = mutableMapOf()

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            categoryBind = ViewDiscoverCategoryBinding.inflate(layoutInflater)
            setClickListener(
                llSearch,
                ivSearch,
                tvSearch,
                categoryBind.llBst1,
                categoryBind.llBst2,
                categoryBind.llBst3,
                categoryBind.llBst4
            ) {
                if (isFastDoubleClick()) return@setClickListener
                when (it) {
                    llSearch, ivSearch, tvSearch -> toSearch()
                    categoryBind.llBst1 -> toCategoryChannel()
                    categoryBind.llBst2 -> toRank()
                    categoryBind.llBst3 -> toCategoryEnd()
                    categoryBind.llBst4 -> toSpecial()
                }
            }
            srlBsList.setOnRefreshListener { mViewModel.getCategoryDiscovery() }
            categoryAdapter.addHeaderView(categoryBind.root)
            categoryAdapter.setListener(this@DiscoverFragment)
            rvBsList.adapter = categoryAdapter
            rvBsList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initData() {
        // 获取缓存
        val data = mViewModel.getCacheContent(PreferenceConstants.TYPE_FIND_INDEX)
        var categoryList: ArrayList<CategoryInfoItem> = arrayListOf()
        if (data.isNotEmpty())
            categoryList =
                gson.fromJson(data, object : TypeToken<ArrayList<CategoryInfoItem>>() {}.type)
        if (categoryList.isNotEmpty()) {
            categoryAdapter.setNewInstance(categoryList)
            categoryList.forEach {
                categoryPageMap[it.categoryName!!] = 1
            }
        }
    }

    override fun createObserver() {
        mViewModel.apply {
            getCategoryDiscoveryState.observe(viewLifecycleOwner) { listState ->
                if (listState.isSuccess && !listState.isEmpty) {
                    val categoryList = mutableListOf<CategoryInfoItem>()
                    categoryList.addAll(listState.listData)
                    categoryAdapter.setNewInstance(categoryList)
                    setCacheContent(PreferenceConstants.TYPE_FIND_INDEX, gson.toJson(categoryList))
                    categoryList.forEach {
                        categoryPageMap[it.categoryName!!] = 1
                    }
                }
                mViewBind.srlBsList.isRefreshing = false
            }
            getCategoryBookListState.observe(viewLifecycleOwner) {
                if (it == null || it.second.list.isNullOrEmpty()) return@observe
                categoryPageMap[it.first] = it.second.pageNum!!
                categoryCountMap[it.first] = it.second.total!!
                val index =
                    categoryAdapter.data.indexOfFirst { category -> category.categoryName == it.first }
                categoryAdapter.data[index].bookList = it.second.list
                categoryAdapter.notifyItemChanged(index + 1)
            }
        }
    }

    override fun lazyLoadData() {
        mViewModel.getCategoryDiscovery()
    }

    override fun onBookBlItemClick(bookInfo: BookInfoItem) {
        toDetail(bookInfo.bookId)
    }

    override fun onBookBlItemAdapterShowAll(categoryInfo: CategoryInfoItem) {
        toDiscoverAll(categoryInfo.categoryName, categoryInfo.type, categoryInfo.categoryId)
    }

    override fun onBookBlItemAdapterReplace(categoryInfo: CategoryInfoItem) {
        categoryInfo.apply {
            var pageNum = categoryPageMap[categoryName] ?: 0
            val pageSize = bookList?.size ?: 8
            val total = categoryCountMap[categoryName] ?: 0
            // 超出总数，pageNum归零
            if (pageNum * pageSize >= total) {
                pageNum = 0
            }
            mViewModel.getCategoryBookList(
                categoryName!!,
                categoryId ?: 0,
                pageNum + 1,
                pageSize,
                type ?: ""
            )
        }
    }
}