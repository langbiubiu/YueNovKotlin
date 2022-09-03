package com.yuenov.kotlin.open.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.ext.deleteStartAndEndNewLine
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.model.response.BookInfoItem

class BookPreviewItemAdapter: BaseQuickAdapter<BookInfoItem, BaseViewHolder>(R.layout.view_adapter_item_category_list), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: BookInfoItem) {
        val rivBciCoverImg = holder.getView<RoundedImageView>(R.id.rivBciCoverImg)
        rivBciCoverImg.loadImage(item.coverImg, R.mipmap.ic_book_list_default)
        holder.setText(R.id.tvBciTitle, item.title)
        holder.setText(R.id.tvBciAuthor, item.author)
        holder.setText(R.id.tvBciDesc, item.desc.deleteStartAndEndNewLine())
        addChildClickViewIds(R.id.llBciBaseInfo,R.id.tvBciTitle,R.id.tvBciAuthor,R.id.tvBciDesc,R.id.llBciShowDetail)
    }
}