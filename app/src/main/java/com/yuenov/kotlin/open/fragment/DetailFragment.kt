package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.BookDetailRecommendAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterFaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_BOOK_ID
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_STRING_BOOK_BASE_INFO
import com.yuenov.kotlin.open.databinding.FragmentDetailBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.response.BookDetailInfoResponse
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.TimeUtils
import com.yuenov.kotlin.open.viewmodel.CommonViewModel
import com.yuenov.kotlin.open.viewmodel.DetailViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 书籍详情界面
 */
class DetailFragment : BaseFragment<DetailViewModel, FragmentDetailBinding>() {

    private var bookId: Int = 0
    private var response: BookDetailInfoResponse? = null
    private var bookInfo: BookBaseInfo? = null
    private val commonViewModel: CommonViewModel by viewModels()
    private val recommendAdapter = BookDetailRecommendAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            setClickListener(
                llDpBack, llDpMenu, tvDpMenuTotal, llDpDownload, tvDpRead, llDpAddBookShelf,
                tvDpChapterName, tvDpRecommendMore, llDpReplace, tvDpReplace, ilDpReplace,
                ivDpAddBookShelf, tvDpAddBookShelf
            ) { view ->
                if (isFastDoubleClick() || isLoadingShowing()) return@setClickListener
                //TODO: 重新写各个点击事件的响应
                when (view) {
                    llDpBack -> nav().navigateUp()
                    tvDpChapterName -> toRead()
                    tvDpMenuTotal, llDpMenu -> {
                        toChapterMenuList()
                    }
                    tvDpRecommendMore -> toRead()
                    llDpReplace, tvDpReplace, ilDpReplace -> toRead()
                    llDpDownload -> toRead()
                    tvDpRead -> toRead()
                    llDpAddBookShelf, ivDpAddBookShelf, tvDpAddBookShelf -> toRead()
                }
            }
            svDpContent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val measuredHeight = llDpTop.measuredHeight
                if (v != svDpContent || measuredHeight < 1)
                    return@setOnScrollChangeListener
                //根据滚动高度来改变alpha值
                if (scrollY > measuredHeight) {
                    //不能设置计算上限，因为onScrollChangeListener的scrollY是UI刷新后的滚动高度，
                    //如果快速滑动，可能会直接掉过参与计算的高度区间，例如scrollY从0立刻变成大于(measureHeight * 2.5)
                    //这样就会导致llDpTop不显示
//                        if (scrollY < measuredHeight * 2.5) {
                        llDpTop.apply {
                            visibility = View.VISIBLE
                            val alphaF = (scrollY - measuredHeight) * 1.0f / (measuredHeight * 2)
                            alpha = if (alphaF > 1.0f) 1.0f else alphaF
                        }
//                        }
                } else {
                    llDpTop.visibility = View.INVISIBLE
                }
            }
            wgvDpRecommend.setOnItemClickListener { parent, view, position, id ->
                logd(CLASS_TAG, "onItemClick ${isLoadingShowing()}")
                if (isLoadingShowing()) return@setOnItemClickListener
                nav().navigateAction(R.id.action_detail_to_detail, Bundle().apply {
                    putInt(EXTRA_INT_BOOK_ID, recommendAdapter.list!![position].bookId)
                })
            }
        }
    }

    override fun initData() {
        bookId = arguments?.getInt(EXTRA_INT_BOOK_ID)!!
    }

    override fun lazyLoadData() {
        mViewModel.apply {
            requestBookDetail(bookId)
            hasReadRecord(bookId)
            hasBookShelf(bookId)
            hasChapter(bookId)
        }
    }

    override fun createObserver() {
        mViewModel.apply {
            bookDetailDataState.observe(viewLifecycleOwner) { resultState ->
                parseState(resultState,
                    { response ->
                        this@DetailFragment.response = response
                        response.apply {
                            bookInfo = BookBaseInfo(
                                bookId, title, author, coverImg, update?.chapterStatus
                            )
                        }
                        setupView(response)

                        addReadHistory(response)
                    },
                    {
                        showToast(it.errorMsg)
                        nav().navigateUp()
                    })
            }
            hasReadRecordState.observe(viewLifecycleOwner) {
                mViewBind.tvDpRead.text =
                    getString(if (it) R.string.DetailPreviewActivity_continueRead else R.string.DetailPreviewActivity_startRead)
            }
            hasBookShelfState.observe(viewLifecycleOwner) {
                refBookShelfState(it)
            }
            hasChapterState.observe(viewLifecycleOwner) {
                if (it) commonViewModel.updateChapterList(bookId, false)
            }
        }
    }

    //获取到书籍信息后更新UI
    private fun setupView(response: BookDetailInfoResponse) {
        mViewBind.ivDpBgBlur.blur(response.coverImg)
        mViewBind.rivDpCoverImg.loadImage(
            response.coverImg,
            R.mipmap.ic_book_list_default
        )
        mViewBind.tvDpTitle.text = response.title
        mViewBind.tvDpAuthor.text = response.author
        "${response.categoryName} ${response.word}".also { mViewBind.tvDpCategory.text = it }
        mViewBind.tvDpDesc.text = response.desc.deleteStartAndEndNewLine()
        response.update?.apply {
            mViewBind.tvDpChapterName.text = chapterName
            if (chapterStatus == InterFaceConstants.CHAPTER_STATUS_SERIALIZE) {
                val lastTime = TimeUtils.getDiffTimeText(time)
                mViewBind.tvDpIsEnd.text = lastTime
                mViewBind.tvDpIsEnd.visibility =
                    if (lastTime.isEmpty()) View.GONE else View.VISIBLE
            } else {
                mViewBind.tvDpIsEnd.text = getString(R.string.info_chapterStatus_end)
                mViewBind.tvDpIsEnd.visibility = View.VISIBLE
            }
        }
        mViewBind.tvDpMenuTotal.text =
            getString(R.string.DetailPreviewActivity_Chapter, response.chapterNum)

        recommendAdapter.list = response.recommend
        mViewBind.wgvDpRecommend.adapter = recommendAdapter

        mViewBind.rlDpContent.visibility = View.VISIBLE
    }

    private fun refBookShelfState(hasBookShelf: Boolean) {
        mViewBind.ivDpAddBookShelf.setImageResource(
            if (hasBookShelf) R.mipmap.ic_remove_bookshelf else R.mipmap.ic_add_bookshelf)
        mViewBind.tvDpAddBookShelf.text = getString(
            if (hasBookShelf) R.string.DetailPreviewActivity_removeBookShelf else R.string.DetailPreviewActivity_addBookShelf)
        mViewBind.tvDpAddBookShelf.setTextColor(resources.getColor(
            if (hasBookShelf) R.color.gary_c5c7 else R.color.blue_b383, null))
    }

    private fun toChapterMenuList() {
        commonViewModel.updateChapterListState.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                bookInfo?.apply {
                    nav().navigateAction(R.id.action_detail_to_chapter_list, Bundle().apply {
                        putParcelable(EXTRA_STRING_BOOK_BASE_INFO, bookInfo)
                    })
                }
            }
        }
        commonViewModel.updateChapterList(bookId, true)
    }
}