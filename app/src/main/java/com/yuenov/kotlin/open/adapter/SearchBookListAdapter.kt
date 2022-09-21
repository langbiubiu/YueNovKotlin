package com.yuenov.kotlin.open.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.database.appDb
import com.yuenov.kotlin.open.databinding.ViewItemSearchListTagBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

class SearchBookListAdapter :
    BaseQuickAdapter<BookInfoItem, BaseViewHolder>(R.layout.view_adapter_searchlist_book),
    LoadMoreModule {

    interface ISearchBookListAdapterListener {
        fun onAddBookShelf(item: BookInfoItem)
        fun onReadBook(item: BookInfoItem)
    }

    var listener: ISearchBookListAdapterListener? = null
    var howWords = ""
    private val tagList = mutableListOf<String>()

    init {
        addChildClickViewIds(
            R.id.rlAdSlContent, R.id.rivAdSlCoverImg,
            R.id.llAdSlContent, R.id.tvAdSlTitle, R.id.tvAdSlAuthor,
            R.id.tvAdSlDesc, R.id.tflAdSlHistory
        )
    }

    override fun convert(holder: BaseViewHolder, item: BookInfoItem) {
        val adapterPosition = holder.bindingAdapterPosition
        holder.setVisible(R.id.viewAdSlLine, adapterPosition > 0)
        val rivAdSlCoverImg: RoundedImageView = holder.getView(R.id.rivAdSlCoverImg)
        rivAdSlCoverImg.loadImage(item.coverImg, R.mipmap.ic_book_list_default)
        // 标题热词标红
        holder.setText(R.id.tvAdSlTitle, item.title)
        if (howWords.isNotEmpty()) {
            // 将item.title中的howWords相关字标红，不做了
        }
        holder.setText(R.id.tvAdSlAuthor, item.author)
        holder.setText(R.id.tvAdSlDesc, item.desc)
        tagList.clear()
        tagList.add(
            context.getString(
                if (item.chapterStatus == InterfaceConstants.CHAPTER_STATUS_SERIALIZE)
                    R.string.AboutChapterStatus_serialize
                else
                    R.string.AboutChapterStatus_end
            )
        )
        item.word?.apply {
            if (this.isNotEmpty()) tagList.add(this)
        }
        item.word?.apply {
            if (this.isNotEmpty()) tagList.add(this)
        }
        val tagFlowLayout: TagFlowLayout = holder.getView(R.id.tflAdSlHistory)
        tagFlowLayout.adapter = object : TagAdapter<String>(tagList) {
            override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                val tagViewBinding =
                    ViewItemSearchListTagBinding.inflate(LayoutInflater.from(context))
                tagViewBinding.tvIsltlName.text = t
                return tagViewBinding.root
            }
        }
        resetVisibility(adapterPosition == 0, holder.getView(R.id.llAdSlOpreation))
        val tvAdSlAdd: TextView = holder.getView(R.id.tvAdSlAdd)
        if (adapterPosition == 0) {
            val bookShelfExists = appDb.bookShelfDao.exists(item.bookId)
            setAddBookShelfStyle(tvAdSlAdd, bookShelfExists)
        }
        val tvAdSlStartRead: TextView = holder.getView(R.id.tvAdSlStartRead)
        setClickListener(tvAdSlAdd, tvAdSlStartRead) {
            when (it) {
                tvAdSlAdd -> addBookShelf(tvAdSlAdd, item)
                tvAdSlStartRead -> listener?.onReadBook(item)
            }
        }
    }

    private fun setAddBookShelfStyle(tvAdSlAdd: TextView, isAdd: Boolean) {
        if (isAdd) {
            tvAdSlAdd.text = context.getString(R.string.SearchBookListAdapter_InBookShelf)
            tvAdSlAdd.setTextColor(context.getColor(R.color.gray_9a9a))
            tvAdSlAdd.isEnabled = false
        } else {
            tvAdSlAdd.text = context.getString(R.string.SearchBookListAdapter_addBookShelf)
            tvAdSlAdd.setTextColor(context.getColor(R.color._b383))
            tvAdSlAdd.isEnabled = true
        }
    }

    private fun addBookShelf(tvAdSlAdd: TextView, item: BookInfoItem) {
        setAddBookShelfStyle(tvAdSlAdd, true)
        listener?.onAddBookShelf(item)
    }
}