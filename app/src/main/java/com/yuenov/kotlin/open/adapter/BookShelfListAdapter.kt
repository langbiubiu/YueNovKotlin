package com.yuenov.kotlin.open.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.databinding.ViewAdapterItemBookshelfBinding
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import me.hgj.jetpackmvvm.ext.util.layoutInflater

/**
 * 首页书架
 */
class BookShelfListAdapter(data: ArrayList<TbBookShelf>) : BaseAdapter() {
    var listData = data

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        logd(CLASS_TAG, "getView position = $position, data = ${listData[position]}")
        val binding = ViewAdapterItemBookshelfBinding.inflate(MyApplication.appContext.layoutInflater!!, parent, false)
        binding.rivBookshelfCover.setImageResource(R.mipmap.dldl)
        binding.ivBookshelfUpdate.apply {
            visibility = if (listData[position].hasUpdate) View.VISIBLE else View.GONE
        }
        binding.tvBookshelfTitle.text = listData[position].title
        return binding.root
    }
}