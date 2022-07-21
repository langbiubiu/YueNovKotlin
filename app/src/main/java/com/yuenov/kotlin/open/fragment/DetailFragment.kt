package com.yuenov.kotlin.open.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.application.MyApplication
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.database.tb.TbBookShelf
import com.yuenov.kotlin.open.databinding.FragmentDetailBinding
import com.yuenov.kotlin.open.databinding.ViewAdapterItemBookshelfBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.utils.ConvertUtils
import com.yuenov.kotlin.open.view.recyclerview.GridDividerItemDecoration
import com.yuenov.kotlin.open.viewmodel.DetailViewModel
import me.hgj.jetpackmvvm.ext.util.layoutInflater

class DetailFragment : BaseFragment<DetailViewModel, FragmentDetailBinding>() {

    private val bookShelfAdapter by lazy { MyAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun lazyLoadData() {
        bookShelfAdapter.listData = mViewModel.getBookShelfData()
        mViewBind.rvBookshelf.apply {
            context?.let {
                layoutManager = GridLayoutManager(it, 3, RecyclerView.VERTICAL, false)
                setHasFixedSize(true)
                addItemDecoration(
                    GridDividerItemDecoration(
                        it,
                        ConvertUtils.dp2px(25f),
                        ConvertUtils.dp2px(20f),
                        true
                    ), Color.BLACK
                )
                adapter = bookShelfAdapter
            }
        }
    }
}

class MyAdapter(data: ArrayList<TbBookShelf>) : RecyclerView.Adapter<MyViewHolder>() {
    var listData = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ViewAdapterItemBookshelfBinding.inflate(
            MyApplication.appContext.layoutInflater!!,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = listData[position]
        holder.binding.rivBookshelfCover.loadImage(data.coverImg, R.mipmap.ic_book_list_default)
        holder.binding.ivBookshelfUpdate.apply {
            visibility = if (data.hasUpdate) View.VISIBLE else View.GONE
        }
        holder.binding.tvBookshelfTitle.text = data.title
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}

class MyViewHolder(val binding: ViewAdapterItemBookshelfBinding) :
    RecyclerView.ViewHolder(binding.root) {}
