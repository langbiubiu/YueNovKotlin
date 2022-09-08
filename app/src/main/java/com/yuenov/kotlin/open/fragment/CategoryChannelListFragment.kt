package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuenov.kotlin.open.adapter.CategoryChannelItemAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.FragmentRecyclerViewBinding
import com.yuenov.kotlin.open.ext.toCategoryBookList
import com.yuenov.kotlin.open.model.response.CategoryInfoItem

class CategoryChannelListFragment :
    BaseFragment<BaseFragmentViewModel, FragmentRecyclerViewBinding>() {

    companion object {
        private const val EXTRA_LIST = "CategoryChannel"
        private const val EXTRA_ID = "channelId"

        fun getFragment(list: ArrayList<CategoryInfoItem>, id: Int): CategoryChannelListFragment {
            val fragment = CategoryChannelListFragment()
            fragment.arguments = Bundle().apply {
                if (list.isNotEmpty()) putParcelableArrayList(EXTRA_LIST, list)
                putInt(EXTRA_ID, id)
            }
            return fragment
        }
    }

    private var channelId = 0
    private var categoryList: ArrayList<CategoryInfoItem> = arrayListOf()
    private val adapter = CategoryChannelItemAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        adapter.listener = object : CategoryChannelItemAdapter.ICategoryChannelListAdapterListener {
            override fun onCategoryChannelListAdapterClick(item: CategoryInfoItem) {
                toCategoryBookList(item.categoryName, item.categoryId, channelId)
            }
        }
        mViewBind.rvFccList.adapter = adapter
        mViewBind.rvFccList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun initData() {
        channelId = requireArguments().getInt(EXTRA_ID, 0)
        categoryList = requireArguments().getParcelableArrayList(EXTRA_LIST) ?: arrayListOf()
        val inviteList = ArrayList<Pair<CategoryInfoItem, CategoryInfoItem?>>()
        for (i in 0..categoryList.size step 2) {
            val first = categoryList[i]
            val second = if (i + 1 < categoryList.size) categoryList[i + 1] else null
            inviteList.add(Pair(first, second))
        }
        adapter.setNewInstance(inviteList)
    }
}