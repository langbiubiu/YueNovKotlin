package com.yuenov.kotlin.open.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.model.response.RankInfoItem
import com.yuenov.kotlin.open.widget.RankItemImageView

class RankListAdapter: BaseQuickAdapter<RankInfoItem, BaseViewHolder>(R.layout.view_adapter_ranklist_item) {

    override fun convert(holder: BaseViewHolder, item: RankInfoItem) {
        resetVisibility(holder.bindingAdapterPosition > 0, holder.getView(R.id.viewAiRkLine))
        val riAiRkImg: RankItemImageView = holder.getView(R.id.riAiRkImg)
        riAiRkImg.setData(item.coverImgs ?: listOf())
        holder.setText(R.id.tvAiRkName, item.rankName)
    }

}