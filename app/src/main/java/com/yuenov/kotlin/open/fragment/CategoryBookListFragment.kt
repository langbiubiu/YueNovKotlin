package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.CategoryBookListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentCategorybooklistBinding
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.ext.showToast
import com.yuenov.kotlin.open.ext.toDetail
import com.yuenov.kotlin.open.model.response.BookInfoItem
import me.hgj.jetpackmvvm.ext.nav

class CategoryBookListFragment :
    BaseFragment<BaseFragmentViewModel, FragmentCategorybooklistBinding>() {

    private var categoryName: String? = null
    private var categoryId = 0
    private var channelId = 0
    private var filterPosition = -1
    private val adapter = CategoryBookListAdapter()
    private var pageNum = 0
    private val filterArray = arrayOf("NEWEST", "HOT", "END")

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            setClickListener(tvCcfiName1, tvCcfiName2, tvCcfiName3) {
                when (it) {
                    tvCcfiName1 -> loadFilter(0)
                    tvCcfiName2 -> loadFilter(1)
                    tvCcfiName3 -> loadFilter(2)
                }
            }
            srlCcnList.setOnRefreshListener { loadData(true) }
            adapter.setOnItemClickListener { adapter, _, position ->
                val bookItem = adapter.getItem(position) as BookInfoItem
                toDetail(bookItem.bookId)
            }
            adapter.loadMoreModule.setOnLoadMoreListener { loadData(false) }
            rvCcnList.adapter = adapter
            rvCcnList.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initData() {
        categoryName = requireArguments().getString(PreferenceConstants.EXTRA_STRING_CATEGORY_NAME)
        categoryId = requireArguments().getInt(PreferenceConstants.EXTRA_INT_CATEGORY_ID)
        channelId = requireArguments().getInt(PreferenceConstants.EXTRA_INT_CHANNEL_ID)
        mViewBind.myAppTitle.getCenterView().text = categoryName
        mViewBind.myAppTitle.getLeftView().setOnClickListener { nav().navigateUp() }
        loadFilter(0)
    }

    override fun createObserver() {
        mViewModel.getBookListByCategoryIdState.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                if (!it.data!!.list.isNullOrEmpty()) {
                    val bookList = ArrayList(it.data!!.list!!)
                    val isLoadHeader = it.data!!.pageNum == 1
                    if (bookList.isEmpty()) {
                        adapter.loadMoreModule.loadMoreEnd(false)
                        return@observe
                    }
                    if (isLoadHeader) {
                        adapter.setNewInstance(bookList)
                    } else {
                        adapter.addData(bookList)
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
    }

    private fun loadFilter(pos: Int) {
        if (pos == filterPosition) return
        filterPosition = pos
        mViewBind.apply {
            val orderViewArray = arrayOf(tvCcfiName1, tvCcfiName2, tvCcfiName3)
            for (i in orderViewArray.indices) {
                orderViewArray[i].setTextColor(
                    resources.getColor(
                        if (i == filterPosition) R.color._b383 else R.color.gray_6666,
                        null
                    )
                )
            }
        }
        loadData(true)
    }

    private fun loadData(isLoadHeader: Boolean) {
        if (isLoadHeader) {
            pageNum = 1
            mViewBind.srlCcnList.isRefreshing = true
        }
        mViewModel.getBookListByCategoryId(
            pageNum,
            InterfaceConstants.categoriesListPageSize,
            categoryId,
            channelId,
            filterArray[filterPosition]
        )
    }
}