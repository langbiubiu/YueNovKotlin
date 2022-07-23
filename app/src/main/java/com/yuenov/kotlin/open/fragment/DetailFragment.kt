package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.adapter.BookDetailRecommendAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.InterFaceConstants
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_BOOK_ID
import com.yuenov.kotlin.open.databinding.FragmentDetailBinding
import com.yuenov.kotlin.open.ext.blur
import com.yuenov.kotlin.open.ext.deleteStartAndEndNewLine
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.showToast
import com.yuenov.kotlin.open.model.response.BookDetailInfoResponse
import com.yuenov.kotlin.open.utils.TimeUtils
import com.yuenov.kotlin.open.viewmodel.CommonViewModel
import com.yuenov.kotlin.open.viewmodel.DetailViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

class DetailFragment : BaseFragment<DetailViewModel, FragmentDetailBinding>() {

    private var bookId: Int = 0
    private val commonViewModel: CommonViewModel by viewModels()
    private val recommendAdapter = BookDetailRecommendAdapter()

    override fun initView(savedInstanceState: Bundle?) {

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
            bookDetailDataState.observe(viewLifecycleOwner) {
                parseState(it,
                    { response ->
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
        mViewBind.tvDpCategory.text = "${response.categoryName} ${response.word}"
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
}