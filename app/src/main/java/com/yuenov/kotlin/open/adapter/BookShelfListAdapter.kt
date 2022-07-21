package com.yuenov.kotlin.open.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.databinding.ViewAdapterItemBookshelfBinding
import com.yuenov.kotlin.open.ext.loadImage
import me.hgj.jetpackmvvm.ext.util.layoutInflater

class BookShelfListAdapter(data: ArrayList<TbBookShelf>): RecyclerView.Adapter<BookShelfListViewHolder>() {
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
        holder.binding.rivBookshelfCover.loadImage(data.coverImg, R.mipmap.ic_book_list_default)
        holder.binding.ivBookshelfUpdate.apply {
            visibility = if (data.hasUpdate) View.VISIBLE else View.GONE
        }
        holder.binding.tvBookshelfTitle.text = data.title
        holder.binding.root.run {
            setOnClickListener {
                onItemClickListener?.onClick(it, position, data)
            }
            setOnLongClickListener {
                onItemClickListener?.onLongClick(it, position, data) ?: false
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

}

class BookShelfListViewHolder(val binding: ViewAdapterItemBookshelfBinding) :
    RecyclerView.ViewHolder(binding.root) {}