package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.BookListItemAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentCategorybooklistBinding
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.ext.showToast
import com.yuenov.kotlin.open.ext.toDetail
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.viewmodel.RankBookListFragmentViewModel
import me.hgj.jetpackmvvm.ext.nav

class RankBookListFragment :
    BaseFragment<RankBookListFragmentViewModel, FragmentCategorybooklistBinding>() {

    private var rankName: String? = null
    private var rankId = 0
    private var channelId = 0
    private val adapter = BookListItemAdapter()
    private var pageNum = 0

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            resetVisibility(false, llCcfiFilter)
            setClickListener(myAppTitle.getLeftView()) {
                when (it) {
                    myAppTitle.getLeftView() -> nav().navigateUp()
                }
            }
            srlCcnList.setOnRefreshListener { loadData(true) }
            adapter.isShowOrder = true
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
        rankName = requireArguments().getString(PreferenceConstants.EXTRA_STRING_CATEGORY_NAME)
        rankId = requireArguments().getInt(PreferenceConstants.EXTRA_INT_CATEGORY_ID)
        channelId = requireArguments().getInt(PreferenceConstants.EXTRA_INT_CHANNEL_ID)
        mViewBind.myAppTitle.getCenterView().text = rankName
        loadData(true)
    }

    override fun createObserver() {
        mViewModel.getRankPageState.observe(viewLifecycleOwner) {
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

    private fun loadData(isLoadHeader: Boolean) {
        if (isLoadHeader) {
            pageNum = 1
            mViewBind.srlCcnList.isRefreshing = true
        }
        mViewModel.getRankPage(
            channelId,
            rankId,
            pageNum,
            InterfaceConstants.categoriesListPageSize
        )
    }
}