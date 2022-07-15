package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.yuenov.kotlin.open.adapter.BookShelfListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.databinding.FragmentBookshelfBinding
import com.yuenov.kotlin.open.databinding.ViewBookshelfEmptyBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.request.BookCheckUpdateRequest
import com.yuenov.kotlin.open.model.response.CheckUpdateItemInfo
import com.yuenov.kotlin.open.view.DeleteBookShelfDialog
import com.yuenov.kotlin.open.viewmodel.BookShelfFragmentViewModel

class BookShelfFragment : BaseFragment<BookShelfFragmentViewModel, FragmentBookshelfBinding>() {

    private val bookShelfAdapter by lazy { BookShelfListAdapter(arrayListOf()) }

    private val emptyBinding: ViewBookshelfEmptyBinding by lazy {
        ViewBookshelfEmptyBinding.inflate(layoutInflater, null, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        logd(CLASS_TAG, "initView")
        mViewBind.swipeRefresh.setOnRefreshListener { checkBookShelfUpdate() }
        mViewBind.swipeRefresh.isEnabled = false

        mViewBind.gvBookshelf.run {
            setOnItemClickListener { parent, view, position, id ->
                toRead()
            }
            setOnItemLongClickListener { parent, view, position, id ->
                val dialog =
                    DeleteBookShelfDialog(this@BookShelfFragment.requireContext(), position)
                dialog.setListener(object : DeleteBookShelfDialog.IDeleteBookShelfListener {
                    override fun toPreviewDetail(position: Int) {
//                        toPreviewDetail(mViewModel.listBookShelf.value!![position].bookId)
                    }

                    override fun toDelete(position: Int) {
                        deleteBookShelf(position)
                    }

                    override fun toCancel() {
                    }
                })
                dialog.show()
                true
            }
        }
        mViewBind.gvBookshelf.adapter = bookShelfAdapter
        setClickListeners(
            mViewBind.llSearch,
            mViewBind.ivSearch,
            mViewBind.tvSearch,
            emptyBinding.tvBseFind
        )
        {
            when (it) {
                mViewBind.llSearch, mViewBind.ivSearch, mViewBind.tvSearch ->
                    toSearch()
                emptyBinding.tvBseFind ->
                    (parentFragment as MainFragment).toBookStore()
            }
        }
    }

    override fun initData() {
        logd(CLASS_TAG, "initData")
    }

    override fun lazyLoadData() {
        logd(CLASS_TAG, "lazyLoadData")
        mViewModel.getBookShelfData()
        checkBookShelfUpdate()
        openLastReadBook()
    }

    override fun createObserver() {
        logd(CLASS_TAG, "createObserver")
        mViewModel.run {
            listBookShelf.observe(viewLifecycleOwner, Observer {
                logd(CLASS_TAG, "listBookShelf observer")
                // EmptyView需要添加到同级View的Parent中才可以显示
                // 如果在EmptyViewBinding inflate时把 mViewBind.gvBookshelf.parent作为parent写入参数，
                // 且attachToParent设为true，则会直接显示EmptyView。在获取到数据后显示列表，就会有EmptyView闪现的现象。
                // 要解决闪现问题，需要在这里手动addView
                logd(CLASS_TAG, "set EmptyView")
                (mViewBind.gvBookshelf.parent as ViewGroup).addView(emptyBinding.root)
                //这一句setEmptyView不加也能实现相应效果，应该是GridView notify之后覆盖了EmptyView，如果不加可能会出现其他bug，还是加上比较好
                mViewBind.gvBookshelf.emptyView = emptyBinding.root

                bookShelfAdapter.listData = mViewModel.listBookShelf.value!!
                mViewBind.swipeRefresh.isEnabled = bookShelfAdapter.listData.isNotEmpty()
                bookShelfAdapter.notifyDataSetChanged()
            })
        }
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * 检查书架中书籍的更新情况
     *
     * 1.先获取书架
     */
    private fun checkBookShelfUpdate() {
        val request = BookCheckUpdateRequest(ArrayList<CheckUpdateItemInfo>())
    }

    /**
     * 打开非正常退出时，最后阅读的书
     */
    private fun openLastReadBook() {

    }

    /**
     * 删除书架中的图书
     */
    private fun deleteBookShelf(position: Int) {
        logd(CLASS_TAG, "deleteBookShelf")
        val bookId = bookShelfAdapter.listData[position].bookId
        mViewModel.deleteBookShelfData(bookId)
        mViewModel.resetAddBookShelfStat(bookId, false)
        bookShelfAdapter.listData.removeAt(position)
        bookShelfAdapter.notifyDataSetChanged()
        mViewBind.swipeRefresh.isEnabled = bookShelfAdapter.listData.isNotEmpty()
    }

}