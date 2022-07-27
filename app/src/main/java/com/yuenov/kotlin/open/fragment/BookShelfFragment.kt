package com.yuenov.kotlin.open.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.BookShelfListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.databinding.FragmentBookshelfBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.ConvertUtils
import com.yuenov.kotlin.open.view.DeleteBookShelfDialog
import com.yuenov.kotlin.open.view.recyclerview.GridDividerItemDecoration
import com.yuenov.kotlin.open.viewmodel.BookShelfFragmentViewModel

/**
 * 书架界面
 */
class BookShelfFragment : BaseFragment<BookShelfFragmentViewModel, FragmentBookshelfBinding>() {

    private val bookShelfAdapter by lazy { BookShelfListAdapter(arrayListOf()) }

    //是否第一次加载数据
    private var isFirstLoadData: Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        logd(CLASS_TAG, "initView")
        mViewBind.swipeRefresh.setOnRefreshListener { mViewModel.checkBookShelfUpdate() }
        mViewBind.swipeRefresh.isEnabled = false

        bookShelfAdapter.setOnItemClickListener(object: BookShelfListAdapter.OnItemClickListener{
            override fun onClick(view: View, position: Int, data: TbBookShelf) {
                toRead(BookBaseInfo(data.bookId, data.title, data.author, data.coverImg, null), 0L)
            }

            override fun onLongClick(view: View, position: Int, data: TbBookShelf): Boolean {
                val dialog =
                    DeleteBookShelfDialog(this@BookShelfFragment.requireContext(), position)
                dialog.setListener(object : DeleteBookShelfDialog.IDeleteBookShelfListener {
                    override fun toPreviewDetail(position: Int) {
                        toDetail(bookShelfAdapter.listData[position].bookId)
                    }

                    override fun toDelete(position: Int) {
                        val bookId = bookShelfAdapter.listData[position].bookId
                        mViewModel.deleteBookShelfData(bookId)
                        mViewModel.resetAddBookShelfStat(bookId, false)
                    }

                    override fun toCancel() {
                    }
                })
                dialog.show()
                return true
            }
        })
        mViewBind.rvBookshelf.apply {
            context?.let {
                layoutManager = GridLayoutManager(it, 3, RecyclerView.VERTICAL, false)
                setHasFixedSize(true)
                addItemDecoration(
                    GridDividerItemDecoration(
                        it,
                        ConvertUtils.dp2px(25f),
                        ConvertUtils.dp2px(20f),
                        true
                    ), Color.BLACK
                )
                adapter = bookShelfAdapter
            }
        }
        setClickListener(
            mViewBind.llSearch,
            mViewBind.ivSearch,
            mViewBind.tvSearch,
            mViewBind.includeEmpty.tvBseFind,
        )
        {
            when (it) {
                mViewBind.llSearch, mViewBind.ivSearch, mViewBind.tvSearch ->
                    toSearch()
                mViewBind.includeEmpty.tvBseFind ->
                    (parentFragment as MainFragment).toBookStore()
            }
        }
    }

    override fun initData() {
        logd(CLASS_TAG, "initData")
    }

    override fun lazyLoadData() {
        logd(CLASS_TAG, "lazyLoadData")
        isFirstLoadData = true
        mViewModel.getBookShelfData()
//        openLastReadBook()
    }

    override fun createObserver() {
        logd(CLASS_TAG, "createObserver")
        mViewModel.run {
            bookShelfDataState.observe(viewLifecycleOwner) {
                mViewBind.includeEmpty.root.visibility = if (it.listData.isEmpty()) View.VISIBLE else View.GONE
                if (it.isSuccess) {
                    bookShelfAdapter.listData = it.listData
                    mViewBind.swipeRefresh.isEnabled = bookShelfAdapter.listData.isNotEmpty()
                    bookShelfAdapter.notifyDataSetChanged()
                    //第一次获取书架数据后，获取书籍更新数据
                    //目前第一次更新数据一定会失败，可能是接口限制，增加延迟后也无效，暂时不进行更新
                    if (isFirstLoadData && it.listData.isNotEmpty()) {
//                        checkBookShelfUpdate()
                        isFirstLoadData = false
                    }
                } else {
                    Toast.makeText(
                        this@BookShelfFragment.context,
                        it.errMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            checkUpdateDataState.observe(viewLifecycleOwner) {
                Toast.makeText(
                    this@BookShelfFragment.context,
                    if(it.isSuccess) R.string.checkupdate_success else R.string.checkupdate_fail,
                    Toast.LENGTH_SHORT
                ).show()
                if (!it.isSuccess)
                    logd(CLASS_TAG, "checkUpdateDataState error message: ${it.errorMsg}")
                mViewBind.swipeRefresh.isRefreshing = false
            }
        }
    }

    /**
     * 打开非正常退出时，最后阅读的书
     */
    private fun openLastReadBook() {
        TODO()
    }

}