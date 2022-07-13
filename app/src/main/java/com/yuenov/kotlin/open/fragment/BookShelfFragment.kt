package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import android.view.ViewGroup
import com.yuenov.kotlin.open.adapter.BookShelfListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.databinding.FragmentBookshelfBinding
import com.yuenov.kotlin.open.databinding.ViewBookshelfEmptyBinding
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.ext.toRead
import com.yuenov.kotlin.open.viewmodel.BookShelfFragmentViewModel

class BookShelfFragment : BaseFragment<BookShelfFragmentViewModel, FragmentBookshelfBinding>() {

    private val bookShelfAdapter by lazy { BookShelfListAdapter(arrayListOf()) }

    private val emptyBinding: ViewBookshelfEmptyBinding by lazy {
        //EmptyView需要添加到同级View的Parent中才可以显示
        ViewBookshelfEmptyBinding.inflate(layoutInflater, mViewBind.gvBookshelf.parent as ViewGroup, true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        logd(CLASS_TAG, "initView")
        mViewBind.swipeRefresh.setOnRefreshListener { mViewModel.getBookShelfData() }
        mViewBind.gvBookshelf.setOnItemClickListener { parent, view, position, id ->
            toRead()
        }
        mViewBind.gvBookshelf.adapter = bookShelfAdapter
    }

    override fun initData() {
        logd(CLASS_TAG, "initData")
    }

    override fun lazyLoadData() {
        logd(CLASS_TAG, "lazyLoadData")
        mViewModel.getBookShelfData()
        bookShelfAdapter.listData = mViewModel.listBookShelf.value!!
        //setEmptyView需要放在数据请求完成后，如果放在initView中，会出现EmptyView闪现之后显示图书列表
        //UI显示应该写在createObserver中，等数据库和协程搞定之后再移过去
        mViewBind.gvBookshelf.emptyView = emptyBinding.root
        bookShelfAdapter.notifyDataSetChanged()
    }

    override fun createObserver() {
        logd(CLASS_TAG, "createObserver")
    }

    override fun onResume() {
        super.onResume()
    }



}