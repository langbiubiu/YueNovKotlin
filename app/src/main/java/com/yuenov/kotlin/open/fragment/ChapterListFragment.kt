package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.DetailBottomMenuListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.databinding.FragmentChapterListBinding
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.ext.showToast
import com.yuenov.kotlin.open.ext.toRead
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import me.hgj.jetpackmvvm.ext.nav

/**
 * 目录界面
 */
class ChapterListFragment : BaseFragment<BaseFragmentViewModel, FragmentChapterListBinding>() {

    private var chapterList = listOf<TbBookChapter>()
    private var orderDesc = true
    private lateinit var adapter: DetailBottomMenuListAdapter
    private var bookBaseInfo: BookBaseInfo? = null

    override fun initView(savedInstanceState: Bundle?) {
        adapter = DetailBottomMenuListAdapter(chapterList, orderDesc)
        mViewBind.apply {
            setClickListener(ivBmOrder, myAppTitle.getLeftView()) {
                when (it) {
                    ivBmOrder -> sortChapterList()
                    myAppTitle.getLeftView() -> nav().navigateUp()
                }
            }
            lvBmList.setOnItemClickListener { parent, view, position, id ->
                toRead(bookBaseInfo!!, chapterList[position].chapterId)
            }
        }
    }

    override fun initData() {
        bookBaseInfo =
            requireArguments().getParcelable(PreferenceConstants.EXTRA_MODEL_BOOK_BASE_INFO)
        if (bookBaseInfo == null || bookBaseInfo!!.bookId < 1) {
            showToast("未知数据")
            nav().navigateUp()
            return
        }
        mViewBind.myAppTitle.getCenterView().text = bookBaseInfo!!.title
    }

    override fun lazyLoadData() {
        chapterList = appDb.chapterDao.getChapterListByBookIdOrderByAsc(bookBaseInfo!!.bookId) ?: listOf()
        val status = getString(
            if (bookBaseInfo!!.chapterStatus == InterfaceConstants.CHAPTER_STATUS_END)
                R.string.info_chapterStatus_end
            else
                R.string.info_chapterStatus_serialize
        )
        mViewBind.apply {
            tvBmTitle.text = "$status 共${chapterList.size}章"
            resetVisibility(true, ivBmOrder)
            adapter.data = chapterList
            lvBmList.adapter = adapter
        }
    }

    private fun sortChapterList() {
        orderDesc = !orderDesc
        mViewBind.ivBmOrder.setImageResource(if (orderDesc) R.mipmap.ic_book_down else R.mipmap.ic_book_up)
        adapter.orderByAes = orderDesc
        adapter.notifyDataSetInvalidated()
    }
}