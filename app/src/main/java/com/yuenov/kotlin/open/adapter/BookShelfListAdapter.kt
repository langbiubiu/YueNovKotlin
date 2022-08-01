package com.yuenov.kotlin.open.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.databinding.ViewAdapterItemBookshelfBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.resetVisibility
import me.hgj.jetpackmvvm.ext.util.layoutInflater

class BookShelfListAdapter(data: ArrayList<TbBookShelf>): RecyclerView.Adapter<BookShelfListAdapter.BookShelfListViewHolder>() {
    var listData = data
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfListViewHolder {
        val binding = ViewAdapterItemBookshelfBinding.inflate(
            MyApplication.appContext.layoutInflater!!,
            parent,
            false
        )
        return BookShelfListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookShelfListViewHolder, position: Int) {
        val data = listData[position]
        holder.binding.apply {
            rivBookshelfCover.loadImage(data.coverImg, R.mipmap.ic_book_list_default)
            resetVisibility(data.hasUpdate, ivBookshelfUpdate)
            tvBookshelfTitle.text = data.title
            root.apply {
                setOnClickListener {
                    onItemClickListener?.onClick(it, position, data)
                }
                setOnLongClickListener {
                    onItemClickListener?.onLongClick(it, position, data) ?: false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int, data: TbBookShelf)

        fun onLongClick(view: View, position: Int, data: TbBookShelf): Boolean
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class BookShelfListViewHolder(val binding: ViewAdapterItemBookshelfBinding) :
        RecyclerView.ViewHolder(binding.root)

}