package com.yuenov.kotlin.open.adapter

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.databinding.ViewAdapterFindItemItemBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.model.response.BookInfoItem
import com.yuenov.kotlin.open.model.response.CategoryInfoItem
import com.yuenov.kotlin.open.widget.WrapHeightGridView
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.ext.util.layoutInflater

class CategoryListAdapter :
    BaseQuickAdapter<CategoryInfoItem, BaseViewHolder>(R.layout.view_adapter_find_item), LoadMoreModule {

    private var listener: IBookBlItemAdapter? = null

    override fun convert(holder: BaseViewHolder, item: CategoryInfoItem) {
        holder.setText(R.id.tvAblCategoryName, item.categoryName)
        val gridView: WrapHeightGridView = holder.getView(R.id.ocgAblItem)
        val gridAdapter = GridViewAdapter()
        gridAdapter.bookList = item.bookList ?: listOf()
        gridView.adapter = gridAdapter
        gridView.measure(0, 0)
        gridView.setOnItemClickListener { _, _, position, _ ->
            listener?.onBookBlItemClick(gridAdapter.bookList[position])
        }
        val tvAblShowAll: TextView = holder.getView(R.id.tvAblShowAll)
        val llAblReplace: LinearLayout = holder.getView(R.id.llAblReplace)
        val ivAblReplace: ImageView = holder.getView(R.id.ivAblReplace)
        val animation: Animation =
            AnimationUtils.loadAnimation(appContext, R.anim.anim_widget_rotate)
        setClickListener(tvAblShowAll, llAblReplace) {
            when (it) {
                tvAblShowAll -> listener?.onBookBlItemAdapterShowAll(item)
                llAblReplace -> {
                    animation.cancel()
                    ivAblReplace.startAnimation(animation)
                    listener?.onBookBlItemAdapterReplace(item)
                }
            }
        }
    }

    fun setListener(lis: IBookBlItemAdapter?) {
        listener = lis
    }

    interface IBookBlItemAdapter {
        fun onBookBlItemClick(bookInfo: BookInfoItem)
        fun onBookBlItemAdapterShowAll(categoryInfo: CategoryInfoItem)
        fun onBookBlItemAdapterReplace(categoryInfo: CategoryInfoItem)
    }

    class GridViewAdapter : BaseAdapter() {

        var bookList: List<BookInfoItem> = listOf()
        override fun getCount(): Int {
            return bookList.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val binding: ViewAdapterFindItemItemBinding
            if (convertView == null) {
                binding = ViewAdapterFindItemItemBinding.inflate(
                    appContext.layoutInflater!!,
                    parent,
                    false
                )
                binding.root.tag = binding
            } else {
                binding = convertView.tag as ViewAdapterFindItemItemBinding
            }
            val book = bookList[position]
            binding.rivAbliCoverImg.loadImage(book.coverImg, R.mipmap.ic_book_list_default)
            binding.tvAbliTitle.text = book.title
            binding.tvAbliAuthor.text = book.author
            return binding.root
        }

    }
}