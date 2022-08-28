package com.yuenov.kotlin.open.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.database.tb.TbBookChapter
import com.yuenov.kotlin.open.databinding.ViewAdapterItemDetailMenuBinding
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.ext.util.layoutInflater

class DetailBottomMenuListAdapter(
    var data: List<TbBookChapter>,
    var orderByAes: Boolean = true,
    var currentChapterId: Long = 0L
) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return Any()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ViewAdapterItemDetailMenuBinding
        if (convertView == null) {
            binding = ViewAdapterItemDetailMenuBinding.inflate(
                appContext.layoutInflater!!, parent, false
            )
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ViewAdapterItemDetailMenuBinding
        }
        val pos = if (orderByAes) position else (data.size - 1 - position)
        binding.tvAidobName.text = data[pos].chapterName
        val textColor =
            if (data[pos].chapterId == currentChapterId)
                R.color._aae4
            else if (data[pos].content.isNullOrEmpty())
                R.color.gray_9999
            else
                R.color.gray_3333
        binding.tvAidobName.setTextColor(appContext.resources.getColor(textColor, null))
        return binding.root
    }
}