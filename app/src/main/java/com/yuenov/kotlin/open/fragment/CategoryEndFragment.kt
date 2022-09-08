package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuenov.kotlin.open.adapter.CategoryListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.databinding.FragmentCategorybooklistBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.model.response.CategoryInfoItem
import com.yuenov.kotlin.open.utils.ConvertUtils
import com.yuenov.kotlin.open.viewmodel.CategoryEndFragmentViewModel
import me.hgj.jetpackmvvm.ext.nav

class CategoryEndFragment: BaseFragment<CategoryEndFragmentViewModel, FragmentCategorybooklistBinding>(),
    CategoryListAdapter.IBookBlItemAdapter {

    private var adapter: CategoryListAdapter = CategoryListAdapter()

    // 每个分类翻到了第几页
    private val categoryPageMap: MutableMap<String, Int> = mutableMapOf()

    // 每个分类的书籍总数
    private val categoryCountMap: MutableMap<String, Int> = mutableMapOf()

    private var pageNum = 1

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            myAppTitle.getCenterView().text = "完本"
            resetVisibility(false, llCcfiFilter)
            setClickListener(myAppTitle.getLeftView()) {
                when (it) {
                    myAppTitle.getLeftView() -> nav().navigateUp()
                }
            }
            srlCcnList.setOnRefreshListener { loadData(true) }
            adapter.setListener(this@CategoryEndFragment)
            adapter.loadMoreModule.setOnLoadMoreListener { loadData(false) }
            val padding = ConvertUtils.dp2px(15f)
            rvCcnList.setPadding(padding, padding, padding, padding)
            rvCcnList.adapter = adapter
            rvCcnList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun lazyLoadData() {
//        loadData(true)
        val categoryList = mutableListOf<CategoryInfoItem>()
        val bookList = mutableListOf<BookInfoItem>()
        for (i in 0..7) {
            bookList.add(BookInfoItem("author", 0, "category", "END", null, "desc", "title$i", ""))
        }
        for (i in 0..9) {
            val num = i + (pageNum - 1) * 10
            categoryList.add(CategoryInfoItem(bookList, num, "categoryName$num", null, "CATEGORY"))
        }
        adapter.setNewInstance(categoryList)
    }

    override fun createObserver() {
        mViewModel.apply {
            getCategoryEndState.observe(viewLifecycleOwner) {
                if (it.isSuccess) {
                    if (!it.data!!.list.isNullOrEmpty()) {
                        val categoryList = ArrayList(it.data!!.list!!)
                        val isLoadHeader = it.data!!.pageNum == 1
                        if (categoryList.isEmpty()) {
                            adapter.loadMoreModule.loadMoreEnd(false)
                            return@observe
                        }
                        if (isLoadHeader) {
                            adapter.setNewInstance(categoryList)
                        } else {
                            adapter.addData(categoryList)
                        }
                        categoryList.forEach {
                            categoryPageMap[it.categoryName!!] = 1
                        }
                        val isEnd =
                            (adapter.itemCount < (pageNum * InterfaceConstants.categoriesListPageSize))
                        pageNum++
                        if (isEnd) {
                            adapter.loadMoreModule.loadMoreEnd(false)
                        } else {
                            adapter.loadMoreModule.loadMoreComplete()
                        }
                    }
                } else {
                    showToast(it.errorMsg ?: "加载失败")
                    adapter.loadMoreModule.loadMoreFail()
                }
                mViewBind.srlCcnList.isRefreshing = false
            }
            getCategoryEndBookListState.observe(viewLifecycleOwner) {
                if (it == null || it.second.list.isNullOrEmpty()) return@observe
                categoryPageMap[it.first] = it.second.pageNum!!
                categoryCountMap[it.first] = it.second.total!!
                val index =
                    adapter.data.indexOfFirst { category -> category.categoryName == it.first }
                adapter.data[index].bookList = it.second.list
                adapter.notifyItemChanged(index + 1)
            }
        }
    }

    private fun loadData(isLoadHeader: Boolean) {
        logD(CLASS_TAG, "loadData $isLoadHeader $pageNum")
        if (isLoadHeader) {
            pageNum = 1
            mViewBind.srlCcnList.isRefreshing = true
        }
        mViewModel.getCategoryEnd(
            pageNum,
            InterfaceConstants.categoriesListPageSize
        )
    }

    override fun onBookBlItemClick(bookInfo: BookInfoItem) {
        logD(CLASS_TAG, "onBookBlItemClick")
        toDetail(bookInfo.bookId)
    }

    override fun onBookBlItemAdapterShowAll(categoryInfo: CategoryInfoItem) {
        toEndBookList(categoryInfo.categoryName, categoryInfo.categoryId)
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
            mViewModel.getCategoryEndBookList(
                pageNum + 1,
                pageSize,
                categoryName!!,
                categoryId ?: 0,
            )
        }
    }
}