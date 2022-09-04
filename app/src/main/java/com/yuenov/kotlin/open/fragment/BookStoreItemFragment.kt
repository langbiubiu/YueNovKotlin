package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.yuenov.kotlin.open.adapter.BookPreviewItemAdapter
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentBcBinding
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.ext.showToast
import com.yuenov.kotlin.open.ext.toDetail
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.viewmodel.BookListFragmentViewModel

class BookStoreItemFragment: BaseFragment<BookListFragmentViewModel, FragmentBcBinding>() {

    companion object {
        private const val EXTRA_NAME = "categoryName"
        private const val EXTRA_ID = "categoryId"

        fun getFragment(name: String, id: Int) : BookStoreItemFragment {
            val fragment = BookStoreItemFragment()
            fragment.arguments = Bundle().apply {
                if (name.isNotBlank()) putString(EXTRA_NAME, name)
                putInt(EXTRA_ID, id)
            }
            return fragment
        }
    }

    private lateinit var categoryName: String
    private var categoryId: Int = 0
    private var pageNum = 1
    private var isEnd = false
    private val bookItemAdapter = BookPreviewItemAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            srlBcList.setOnRefreshListener {
                loadData(true)
            }
            bookItemAdapter.setOnItemChildClickListener { adapter, _, position ->
                logD(CLASS_TAG, "onItemChildClick")
                val bookItem = adapter.getItem(position) as BookInfoItem
                toDetail(bookItem.bookId)
            }
            bookItemAdapter.setOnItemClickListener { adapter, _, position ->
                logD(CLASS_TAG, "onItemClick")
                val bookItem = adapter.getItem(position) as BookInfoItem
                toDetail(bookItem.bookId)
            }
            bookItemAdapter.loadMoreModule.setOnLoadMoreListener { loadData(false) }
            rvBcList.adapter = bookItemAdapter
            rvBcList.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initData() {
        categoryName = requireArguments().getString(EXTRA_NAME, "")
        categoryId = requireArguments().getInt(EXTRA_ID)
        if (categoryId < 1) {
            showToast("加载数据失败！")
        }
        // 获取缓存
        val data = mViewModel.getCacheContent(PreferenceConstants.TYPE_BOOKSTORE_START + categoryId)
        var bookList: ArrayList<BookInfoItem> = arrayListOf()
        if (data.isNotEmpty())
            bookList = gson.fromJson(data, object : TypeToken<ArrayList<BookInfoItem>>() {}.type)
        setupData(bookList, true)
    }

    override fun lazyLoadData() {
        loadData(true)
    }

    override fun createObserver() {
        mViewModel.apply {
            getBookListByCategoryIdState.observe(viewLifecycleOwner) {
                if (it.isSuccess) {
                    if (!it.data!!.list.isNullOrEmpty()) {
                        val bookList = ArrayList(it.data!!.list!!)
                        val isLoadHeader = it.data!!.pageNum == 1
                        setupData(bookList, isLoadHeader)
                        if (isLoadHeader)
                            setCacheContent(PreferenceConstants.TYPE_BOOKSTORE_START + categoryId, gson.toJson(bookList))
                    }
                } else {
                    showToast(it.errorMsg ?: "加载失败")
                    bookItemAdapter.loadMoreModule.loadMoreFail()
                }
                mViewBind.srlBcList.isRefreshing = false
            }
        }
    }

    private fun loadData(isLoadHeader: Boolean) {
        if (isLoadHeader) {
            pageNum = 1
            mViewBind.srlBcList.isRefreshing = true
        }
        mViewModel.getBookListByCategoryId(pageNum, InterfaceConstants.categoriesListPageSize, categoryId, null, null)
    }

    private fun setupData(bookList: ArrayList<BookInfoItem>, isLoadHeader: Boolean) {
        if (bookList.isEmpty()) {
            isEnd = true
            bookItemAdapter.loadMoreModule.loadMoreEnd(false)
            return
        }
        if (isLoadHeader) {
            bookItemAdapter.setNewInstance(bookList)
        } else {
            bookItemAdapter.addData(bookList)
        }
        isEnd = (bookItemAdapter.itemCount < (pageNum * InterfaceConstants.categoriesListPageSize))
        pageNum++
        if (isEnd) {
            bookItemAdapter.loadMoreModule.loadMoreEnd(false)
        } else {
            bookItemAdapter.loadMoreModule.loadMoreComplete()
        }
    }

}