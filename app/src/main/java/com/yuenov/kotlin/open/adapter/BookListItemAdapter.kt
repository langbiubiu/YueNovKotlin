package com.yuenov.kotlin.open.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.constant.InterfaceConstants
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.model.response.BookInfoItem

class BookListItemAdapter :
    BaseQuickAdapter<BookInfoItem, BaseViewHolder>(R.layout.view_adapter_categorylist_item),
    LoadMoreModule {

    /**
     * 是否展示Order
     */
    var isShowOrder = false

    override fun convert(holder: BaseViewHolder, item: BookInfoItem) {
        val rivClCoverImg: RoundedImageView = holder.getView(R.id.rivClCoverImg)
        rivClCoverImg.loadImage(item.coverImg, R.mipmap.ic_book_list_default)
        holder.setText(R.id.tvClTitle, item.title)
        holder.setText(R.id.tvClDesc, item.desc)
        holder.setText(R.id.tvClAuthor, item.author)
        holder.setText(R.id.tvClCategoryName, item.categoryName)
        resetVisibility(
            item.chapterStatus == InterfaceConstants.CHAPTER_STATUS_SERIALIZE,
            holder.getView(R.id.tvClLz)
        )
        resetVisibility(
            item.chapterStatus == InterfaceConstants.CHAPTER_STATUS_END,
            holder.getView(R.id.tvClWj)
        )
        resetVisibility(holder.layoutPosition > 0, holder.getView(R.id.viewClLine))
        val rivClOrder: RoundedImageView = holder.getView(R.id.rivClOrder)
        val adapterPosition = holder.bindingAdapterPosition
        when (adapterPosition) {
            0 -> rivClOrder.setImageResource(R.mipmap.ic_img_num1)
            1 -> rivClOrder.setImageResource(R.mipmap.ic_img_num2)
            2 -> rivClOrder.setImageResource(R.mipmap.ic_img_num3)
        }
        resetVisibility(isShowOrder && adapterPosition <= 2, rivClOrder)
    }
}