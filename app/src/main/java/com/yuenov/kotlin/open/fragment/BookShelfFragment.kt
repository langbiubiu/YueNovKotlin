package com.yuenov.kotlin.open.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.BookShelfListAdapter
import com.yuenov.kotlin.open.application.appViewModel
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.databinding.FragmentBookshelfBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.ConvertUtils
import com.yuenov.kotlin.open.utils.DataStoreUtils
import com.yuenov.kotlin.open.viewmodel.BookShelfFragmentViewModel
import com.yuenov.kotlin.open.widget.DeleteBookShelfDialog
import com.yuenov.kotlin.open.widget.recyclerview.GridDividerItemDecoration

/**
 * 书架界面
 */
class BookShelfFragment : BaseFragment<BookShelfFragmentViewModel, FragmentBookshelfBinding>() {

    private val bookShelfAdapter by lazy { BookShelfListAdapter(arrayListOf()) }

    //是否第一次加载数据
    private var isFirstLoadData: Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            swipeRefresh.setOnRefreshListener { mViewModel.checkBookShelfUpdate() }
            swipeRefresh.isEnabled = false

            bookShelfAdapter.setOnItemClickListener(object :
                BookShelfListAdapter.OnItemClickListener {
                override fun onClick(view: View, position: Int, data: TbBookShelf) {
                    logD(CLASS_TAG, "onItemClick onCLick")
                    toRead(
                        BookBaseInfo(data.bookId, data.title, data.author, data.coverImg, null),
                        0L
                    )
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
                            mViewModel.deleteBookShelf(bookId)
                            mViewModel.resetAddBookShelfStat(bookId, false)
                        }

                        override fun toCancel() {
                        }
                    })
                    dialog.show()
                    return true
                }
            })
            rvBookshelf.apply {
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
            setClickListener(llSearch, ivSearch, tvSearch, includeEmpty.tvBseFind) {
                when (it) {
                    llSearch, ivSearch, tvSearch -> toSearch()
                    includeEmpty.tvBseFind -> (parentFragment as MainFragment).toBookStore()
                }
            }
        }
    }

    override fun initData() {
        logD(CLASS_TAG, "initData")
    }

    override fun lazyLoadData() {
        logD(CLASS_TAG, "lazyLoadData")
        isFirstLoadData = true
        mViewModel.getBookShelfList()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        logD(CLASS_TAG, "createObserver")
        mViewModel.run {
            getBookShelfListState.observe(viewLifecycleOwner) {
                resetVisibility(it.listData.isEmpty(), mViewBind.includeEmpty.root)
                if (it.isSuccess) {
                    bookShelfAdapter.listData = ArrayList(it.listData)
                    mViewBind.swipeRefresh.isEnabled = bookShelfAdapter.listData.isNotEmpty()
                    bookShelfAdapter.notifyDataSetChanged()
                    //第一次获取书架数据后，获取书籍更新数据
                    if (isFirstLoadData && it.listData.isNotEmpty()) {
                        checkBookShelfUpdate()
                        // 在更新完书架信息后再更新AppConfigInfo
                        appViewModel.updateAppConfigInfo()
                        isFirstLoadData = false
                    }
                } else {
                    it.errMessage?.let { it1 -> showToast(it1) }
                }
            }
            checkUpdateDataState.observe(viewLifecycleOwner) {
                showToast(if (it.isSuccess) R.string.checkupdate_success else R.string.checkupdate_fail)
                if (!it.isSuccess)
                    logE(CLASS_TAG, "checkUpdateDataState error message: ${it.errorMsg}")
                mViewBind.swipeRefresh.isRefreshing = false
            }
        }
        appViewModel.appConfigInfo.observeInFragment(this) {
            logD(CLASS_TAG, "AppConfigInfo observe")
            DataStoreUtils.putJsonData(PreferenceConstants.KEY_CATEGORY_INFO, it)
        }
    }

}