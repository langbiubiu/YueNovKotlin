package com.yuenov.kotlin.open.adapter

import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.model.response.CategoryInfoItem
import com.yuenov.kotlin.open.widget.CategoryItemImageView

class CategoryChannelItemAdapter :
    BaseQuickAdapter<Pair<CategoryInfoItem, CategoryInfoItem?>, BaseViewHolder>(R.layout.view_adapter_categorychannel_item) {

    interface ICategoryChannelListAdapterListener {
        fun onCategoryChannelListAdapterClick(item: CategoryInfoItem)
    }

    var listener: ICategoryChannelListAdapterListener? = null

    override fun convert(holder: BaseViewHolder, item: Pair<CategoryInfoItem, CategoryInfoItem?>) {
        holder.setText(R.id.tvAcciName1, item.first.categoryName)
        holder.getView<LinearLayout>(R.id.llAcci1).setOnClickListener {
            listener?.onCategoryChannelListAdapterClick(item.first)
        }
        val ciImageView1: CategoryItemImageView = holder.getView(R.id.ciivAcciImg1)
        ciImageView1.setData(item.first.coverImgs)
        ciImageView1.setListener {
            listener?.onCategoryChannelListAdapterClick(item.first)
        }
        item.second?.apply {
            holder.setText(R.id.tvAcciName2, categoryName)
            holder.getView<LinearLayout>(R.id.llAcci2).setOnClickListener {
                listener?.onCategoryChannelListAdapterClick(item.second!!)
            }
            val ciImageView2: CategoryItemImageView = holder.getView(R.id.ciivAcciImg2)
            ciImageView2.setData(item.first.coverImgs)
            ciImageView2.setListener {
                listener?.onCategoryChannelListAdapterClick(item.second!!)
            }
        }
        resetVisibility(
            if (item.second != null) View.VISIBLE else View.INVISIBLE,
            holder.getView(R.id.llAcci2)
        )
    }
}