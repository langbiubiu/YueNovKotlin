package com.yuenov.kotlin.open.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.databinding.ViewAdapterItemDetailRecommendBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.model.response.BookInfoItem
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.ext.util.layoutInflater

/**
 * 预览页推荐书籍
 */
class BookDetailRecommendAdapter : BaseAdapter() {

    var list: List<BookInfoItem>? = null

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return Any()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //参考ViewHolder模式，其实ViewBinding本质就是一个ViewHolder
        val binding: ViewAdapterItemDetailRecommendBinding
        if (convertView == null) {
            binding = ViewAdapterItemDetailRecommendBinding.inflate(
                appContext.layoutInflater!!, parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ViewAdapterItemDetailRecommendBinding
        }
        binding.rivAdrCoverImg.loadImage(list!![position].coverImg, R.mipmap.ic_book_list_default)
        binding.tvAdrTitle.text = list!![position].title
        binding.tvAdrAuthor.text = list!![position].author
        return binding.root
    }

}