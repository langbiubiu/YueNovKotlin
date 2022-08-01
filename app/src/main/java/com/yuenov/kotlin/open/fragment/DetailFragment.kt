package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.BookDetailRecommendAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterFaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_BOOK_ID
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_MODEL_BOOK_BASE_INFO
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_STRING_TITLE
import com.yuenov.kotlin.open.databinding.FragmentDetailBinding
import com.yuenov.kotlin.open.ext.*
import com.yuenov.kotlin.open.model.response.BookDetailInfoResponse
import com.yuenov.kotlin.open.model.standard.BookBaseInfo
import com.yuenov.kotlin.open.utils.TimeUtils
import com.yuenov.kotlin.open.viewmodel.CommonViewModel
import com.yuenov.kotlin.open.viewmodel.DetailViewModel
import me.hgj.jetpackmvvm.ext.navigateAction
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 书籍详情界面
 */
class DetailFragment : BaseFragment<DetailViewModel, FragmentDetailBinding>() {

    private var bookId: Int = 0
    private var response: BookDetailInfoResponse? = null
    private lateinit var bookInfo: BookBaseInfo
    private val commonViewModel: CommonViewModel by viewModels()
    private val recommendAdapter = BookDetailRecommendAdapter()
    private var hasBookShelf: Boolean = false
    private var recommendPageNum: Int = 1
    private val _defaultRecommendPageSize = 6
    private var recommendPageSize: Int = _defaultRecommendPageSize
    private var recommendTotal: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            setClickListener(
                llDpBack, llDpMenu, tvDpMenuTotal, llDpDownload, tvDpRead, llDpAddBookShelf,
                tvDpChapterName, tvDpRecommendMore, llDpReplace, tvDpReplace, ilDpReplace,
                ivDpAddBookShelf, tvDpAddBookShelf
            ) { view ->
                if (isFastDoubleClick() || isLoadingShowing()) return@setClickListener
                when (view) {
                    llDpBack -> nav?.navigateUp()
                    tvDpChapterName -> toRead(bookInfo, 0L, hasBookShelf)
                    tvDpMenuTotal, llDpMenu -> {
                        toChapterMenuList()
                    }
                    tvDpRecommendMore -> {
                        nav?.navigateAction(R.id.action_detail_to_detail, Bundle().apply {
                            putString(EXTRA_STRING_TITLE, "热门推荐")
                            putInt(EXTRA_INT_BOOK_ID, bookId)
                        })
                    }
                    llDpReplace, tvDpReplace, ilDpReplace -> {
                        // pageSize小于6，说明已经查询到列表尾部，则将pageNum设为1，从头获取
                        mViewModel.getRecommendList(
                            bookId,
                            if (recommendPageSize < _defaultRecommendPageSize) 1 else (recommendPageNum + 1),
                            _defaultRecommendPageSize
                        )
                    }
                    llDpDownload -> toRead(bookInfo, 0L, hasBookShelf)
                    tvDpRead -> toRead(bookInfo, 0L, hasBookShelf)
                    llDpAddBookShelf, ivDpAddBookShelf, tvDpAddBookShelf -> {
                        mViewModel.addOrRemoveBookShelf(hasBookShelf, bookInfo)
                    }
                }
            }
            svDpContent.setOnScrollChangeListener { v, _, scrollY, _, _ ->
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
                        resetVisibility(true, llDpTop)
                        val alphaF = (scrollY - measuredHeight) * 1.0f / (measuredHeight * 2)
                        alpha = if (alphaF > 1.0f) 1.0f else alphaF
                    }
//                        }
                } else {
                    resetVisibility(View.INVISIBLE, llDpTop)
                }
            }
            wgvDpRecommend.setOnItemClickListener { _, _, position, id ->
                logd(CLASS_TAG, "onItemClick ${isLoadingShowing()}")
                if (isLoadingShowing()) return@setOnItemClickListener
                nav?.navigateAction(R.id.action_detail_to_detail, Bundle().apply {
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
                        nav?.navigateUp()
                    })
            }
            hasReadRecordState.observe(viewLifecycleOwner) {
                mViewBind.tvDpRead.text =
                    getString(if (it) R.string.DetailPreviewActivity_continueRead else R.string.DetailPreviewActivity_startRead)
            }
            hasBookShelfState.observe(viewLifecycleOwner) {
                hasBookShelf = it
                refreshBookShelfState()
            }
            hasChapterState.observe(viewLifecycleOwner) {
                if (it) commonViewModel.updateChapterList(bookId, false)
            }
            addOrRemoveBookShelfState.observe(viewLifecycleOwner) {
                if (it) {
                    hasBookShelf = !hasBookShelf
                    refreshBookShelfState()
                } else {
                    showToast(if (hasBookShelf) R.string.remove_bookshelf_fail else R.string.add_bookshelf_fail)
                }
            }
            getRecommendListState.observe(viewLifecycleOwner) {
                if (it.isSuccess) {
                    logd(CLASS_TAG, "total = ${it.data!!.total}, pageNum = ${it.data!!.pageNum}, pageSize = ${it.data!!.pageSize}")
                    recommendPageNum = it.data!!.pageNum ?: 0
                    recommendPageSize = it.data!!.pageSize ?: 0
                    recommendTotal = it.data!!.total ?: 0
                    recommendAdapter.list = it.data!!.list
//                    mViewBind.wgvDpRecommend.adapter = recommendAdapter
                    recommendAdapter.notifyDataSetChanged()
                } else {
                    loge(CLASS_TAG, it.errorMsg!!)
                }
            }
        }
    }

    //获取到书籍信息后更新UI
    private fun setupView(response: BookDetailInfoResponse) {
        mViewBind.apply {
            ivDpBgBlur.blur(response.coverImg)
            rivDpCoverImg.loadImage(
                response.coverImg,
                R.mipmap.ic_book_list_default
            )
            tvDpTitle.text = response.title
            tvDpAuthor.text = response.author
            "${response.categoryName} ${response.word}".also { tvDpCategory.text = it }
            tvDpDesc.text = response.desc.deleteStartAndEndNewLine()
            response.update?.apply {
                tvDpChapterName.text = chapterName
                if (chapterStatus == InterFaceConstants.CHAPTER_STATUS_SERIALIZE) {
                    val lastTime = TimeUtils.getDiffTimeText(time)
                    tvDpIsEnd.text = lastTime
                    resetVisibility(lastTime.isNotEmpty(), tvDpIsEnd)
                } else {
                    tvDpIsEnd.text = getString(R.string.info_chapterStatus_end)
                    resetVisibility(true, tvDpIsEnd)
                }
            }
            tvDpMenuTotal.text =
                getString(R.string.DetailPreviewActivity_Chapter, response.chapterNum)

            recommendAdapter.list = response.recommend
            recommendPageSize = response.recommend?.size ?: _defaultRecommendPageSize
            wgvDpRecommend.adapter = recommendAdapter

            resetVisibility(true, rlDpContent)
        }
    }

    private fun refreshBookShelfState() {
        mViewBind.apply {
            ivDpAddBookShelf.setImageResource(
                if (hasBookShelf) R.mipmap.ic_remove_bookshelf else R.mipmap.ic_add_bookshelf
            )
            tvDpAddBookShelf.text = getString(
                if (hasBookShelf) R.string.DetailPreviewActivity_removeBookShelf else R.string.DetailPreviewActivity_addBookShelf
            )
            tvDpAddBookShelf.setTextColor(
                resources.getColor(
                    if (hasBookShelf) R.color.gary_c5c7 else R.color.blue_b383, null
                )
            )
        }
    }

    private fun toChapterMenuList() {
        commonViewModel.updateChapterListState.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                nav?.navigateAction(R.id.action_detail_to_chapter_list, Bundle().apply {
                    putParcelable(EXTRA_MODEL_BOOK_BASE_INFO, bookInfo)
                })
            }
        }
        commonViewModel.updateChapterList(bookId, true)
    }
}