package com.yuenov.kotlin.open.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.yuenov.kotlin.open.adapter.SearchBookDefaultListAdapter
import com.yuenov.kotlin.open.adapter.SearchBookListAdapter
import com.yuenov.kotlin.open.application.appViewModel
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentSearchBinding
import com.yuenov.kotlin.open.databinding.ViewItemSearchListTagBinding
import com.yuenov.kotlin.open.databinding.ViewSearchHistoryBinding
import com.yuenov.kotlin.open.databinding.ViewSearchHotlistHeaderBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.DataStoreUtils
import com.yuenov.kotlin.open.viewmodel.SearchFragmentViewModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.view.isEmpty
import me.leefeng.promptlibrary.PromptButton
import me.leefeng.promptlibrary.PromptDialog

class SearchFragment : BaseFragment<SearchFragmentViewModel, FragmentSearchBinding>() {

    private lateinit var historyBinding: ViewSearchHistoryBinding
    private lateinit var hotListBinding: ViewSearchHotlistHeaderBinding
    private var pageNum = 1
    private var isEnd = false
    private var searchHistoryList = ArrayList<String>()
    private var searchResultList = ArrayList<BookInfoItem>()
    private val defaultListAdapter = SearchBookDefaultListAdapter()
    private val searchAdapter = SearchBookListAdapter()

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                closeKeyBoard()
            }
        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        historyBinding = ViewSearchHistoryBinding.inflate(layoutInflater)
        hotListBinding = ViewSearchHotlistHeaderBinding.inflate(layoutInflater)
        mViewBind.apply {
            setClickListener(
                etScContent,
                rlScBack,
                tvScSearch,
                tvScFeedBack,
                historyBinding.tvScClearSearchHistory
            ) {
                if (isFastDoubleClick() || isLoadingShowing()) return@setClickListener
                when (it) {
                    etScContent -> resetDefaultStyle()
                    rlScBack -> nav().navigateUp()
                    tvScSearch -> search(true)
                    historyBinding.tvScClearSearchHistory -> clearSearchHistory()
                    tvScFeedBack -> toFeedBack()
                }
            }
            etScContent.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    search(true)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            rvScHotList.addOnScrollListener(scrollListener)
            rvScSearchList.addOnScrollListener(scrollListener)
            defaultListAdapter.addHeaderView(historyBinding.root)
            defaultListAdapter.addHeaderView(hotListBinding.root)
            rvScHotList.adapter = defaultListAdapter
            defaultListAdapter.setOnItemChildClickListener { _, _, position ->
                val bookId = appViewModel.appConfigInfo.value!!.hotSearch?.get(position)?.bookId
                if (bookId != null) {
                    toDetail(bookId)
                }
            }
            rvScHotList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            resetVisibility(
                !appViewModel.appConfigInfo.value!!.hotSearch.isNullOrEmpty(),
                rvScHotList
            )
            if (etScContent.isEmpty()) {
                etScContent.setText(etScContent.hint)
                etScContent.setSelection(etScContent.selectionEnd)
            }
            searchAdapter.loadMoreModule.setOnLoadMoreListener {
                search(false)
            }
            rvScSearchList.adapter = searchAdapter
            searchAdapter.listener = object : SearchBookListAdapter.ISearchBookListAdapterListener {
                override fun onAddBookShelf(item: BookInfoItem) {
                    mViewModel.addBookShelf(
                        BookBaseInfo(
                            item.bookId,
                            item.title,
                            item.author,
                            item.coverImg,
                            item.chapterStatus
                        )
                    )
                }

                override fun onReadBook(item: BookInfoItem) {
                    if (isLoadingShowing()) return
                    toRead(
                        BookBaseInfo(
                            item.bookId,
                            item.title,
                            item.author,
                            item.coverImg,
                            item.chapterStatus
                        ), 0
                    )
                }
            }
            searchAdapter.setOnItemChildClickListener { adapter, view, position ->
                if (isLoadingShowing()) return@setOnItemChildClickListener
                toDetail((adapter.data[position] as BookInfoItem).bookId)
            }
            rvScSearchList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            resetVisibility(
                false,
                historyBinding.root,
                rvScHotList,
                rvScSearchList,
                viewScLine,
                tvScFeedBack
            )
            if (!appViewModel.appConfigInfo.value!!.hotSearch.isNullOrEmpty()) {
                etScContent.hint = appViewModel.appConfigInfo.value!!.hotSearch!![0].title
            }
        }
    }

    override fun initData() {
        val historyString = DataStoreUtils.getData(PreferenceConstants.KEY_SEARCH_HISTORY, "")
        searchHistoryList =
            gson.fromJson(historyString, object : TypeToken<ArrayList<String>>() {}.type)
        historyBinding.apply {
            tflScHistory.adapter = object : TagAdapter<String>(searchHistoryList) {
                override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                    val tagViewBinding =
                        ViewItemSearchListTagBinding.inflate(LayoutInflater.from(context))
                    tagViewBinding.tvIsltlName.text = t
                    return tagViewBinding.root
                }
            }
            tflScHistory.setOnTagClickListener { _, position, parent ->
                mViewBind.etScContent.setText(searchHistoryList[position])
                mViewBind.etScContent.setSelection(mViewBind.etScContent.selectionEnd)
                search(true)
                true
            }
            resetVisibility(searchHistoryList.isNotEmpty(), root)
        }
    }

    override fun createObserver() {
        mViewModel.searchBookState.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                if (!it.data!!.list.isNullOrEmpty()) {
                    val bookList = ArrayList(it.data!!.list!!)
                    val isLoadHeader = it.data!!.pageNum == 1
                    val keyWord = mViewBind.etScContent.text.toString()
                    if (bookList.isEmpty()) {
                        searchAdapter.loadMoreModule.loadMoreEnd(false)
                        resetVisibility(true, mViewBind.tvScFeedBack)
                        return@observe
                    } else {
                        if (!searchHistoryList.contains(keyWord)) {
                            searchHistoryList.add(0, keyWord)
                            historyBinding.tflScHistory.adapter.notifyDataChanged()
                            DataStoreUtils.putData(
                                PreferenceConstants.KEY_SEARCH_HISTORY,
                                gson.toJson(searchHistoryList)
                            )
                        }
                    }
                    searchAdapter.howWords = keyWord
                    if (isLoadHeader) {
                        searchAdapter.setNewInstance(bookList)
                    } else {
                        searchAdapter.addData(bookList)
                    }
                    val isEnd =
                        (searchAdapter.itemCount < (pageNum * InterfaceConstants.pageSize))
                    pageNum++
                    if (isEnd) {
                        searchAdapter.loadMoreModule.loadMoreEnd(false)
                    } else {
                        searchAdapter.loadMoreModule.loadMoreComplete()
                    }
                    mViewBind.apply {
                        resetVisibility(true, rvScSearchList, viewScLine, tvScFeedBack)
                    }
                }
            } else {
                showToast(it.errorMsg!!)
                searchAdapter.loadMoreModule.loadMoreFail()
            }
        }
    }

    private fun resetDefaultStyle() {
        mViewBind.apply {
            resetVisibility(false, rvScSearchList, viewScLine, tvScFeedBack)
            resetVisibility(
                !appViewModel.appConfigInfo.value!!.hotSearch.isNullOrEmpty(),
                rvScHotList
            )
            resetVisibility(searchHistoryList.isNotEmpty(), historyBinding.root)
            if (searchResultList.isNotEmpty()) {
                searchResultList.clear()
                searchAdapter.notifyDataSetChanged()
                rvScSearchList.smoothScrollToPosition(0)
            }
            Handler(Looper.myLooper()!!).postDelayed({ openKeyBoard(etScContent) }, 100)
        }
    }

    private fun search(isLoadHeader: Boolean) {
        if (isLoadingShowing()) return
        closeKeyBoard()
        if (isLoadHeader) pageNum = 1
        mViewModel.searchBook(mViewBind.etScContent.text.toString(), pageNum, InterfaceConstants.pageSize)
    }

    private fun clearSearchHistory() {
        if (isLoadingShowing()) return
        closeKeyBoard()
        Handler(Looper.myLooper()!!).postDelayed({ showDeletePop() }, 100)
    }

    private fun showDeletePop() {
        val confirm = PromptButton("确定") {
            if (searchHistoryList.isNotEmpty()) {
                searchHistoryList.clear()
            }
        }
        DataStoreUtils.putData(
            PreferenceConstants.KEY_SEARCH_HISTORY,
            gson.toJson(searchHistoryList)
        )
        confirm.focusBacColor = Color.parseColor("#FAFAD2")
        // Alert的调用
        val promptDialog = PromptDialog(requireActivity())
        promptDialog.showWarnAlert("你确定清空搜索记录？", PromptButton("取消") {}, confirm)
    }
}