package com.yuenov.kotlin.open.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.model.response.BookInfoItem

class SearchBookDefaultListAdapter :
    BaseQuickAdapter<BookInfoItem, BaseViewHolder>(R.layout.view_adapter_searchdefault_book) {

    override fun convert(holder: BaseViewHolder, item: BookInfoItem) {
        val tvApSiPosition: TextView = holder.getView(R.id.tvApSiPosition)
        tvApSiPosition.text = holder.bindingAdapterPosition.toString()
        tvApSiPosition.setTextColor(
            context.getColor(if (holder.bindingAdapterPosition <= 3) R.color.red_3f42 else R.color.gray_9999))
        val rivApSiCoverImg: RoundedImageView = holder.getView(R.id.rivApSiCoverImg)
        rivApSiCoverImg.loadImage(item.coverImg, R.mipmap.ic_book_list_default)
        holder.setText(R.id.tvApSiTitle, item.title)
        holder.setText(R.id.tvApSiDesc, item.desc)
    }
}