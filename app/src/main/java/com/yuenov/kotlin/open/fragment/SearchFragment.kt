package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import android.view.KeyEvent
import com.yuenov.kotlin.open.adapter.SearchBookDefaultListAdapter
import com.yuenov.kotlin.open.adapter.SearchBookListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.FragmentSearchBinding
import com.yuenov.kotlin.open.databinding.ViewSearchHistoryBinding
import com.yuenov.kotlin.open.ext.isFastDoubleClick
import com.yuenov.kotlin.open.ext.isLoadingShowing
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.ext.toFeedBack
import me.hgj.jetpackmvvm.ext.nav

class SearchFragment : BaseFragment<BaseFragmentViewModel, FragmentSearchBinding>() {

    private lateinit var historyBinding: ViewSearchHistoryBinding
    private var pageNum = 1
    private var isEnd = false
    private var searchHistoryList = mutableListOf<String>()
    private val defaultListAdapter = SearchBookDefaultListAdapter()
    private val searchAdapter = SearchBookListAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        historyBinding = ViewSearchHistoryBinding.inflate(layoutInflater)
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
        }
    }

    private fun resetDefaultStyle() {}

    private fun search(isLoadHeader: Boolean) {}

    private fun clearSearchHistory() {}
}