package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuenov.kotlin.open.adapter.RankListAdapter
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.FragmentRecyclerViewBinding
import com.yuenov.kotlin.open.ext.toRankBookList
import com.yuenov.kotlin.open.model.response.RankListInfoItem

class RankListFragment : BaseFragment<BaseFragmentViewModel, FragmentRecyclerViewBinding>() {

    companion object {
        private const val EXTRA_RANK_LIST = "RankList"

        fun getFragment(list: RankListInfoItem): RankListFragment {
            val fragment = RankListFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(EXTRA_RANK_LIST, list)
            }
            return fragment
        }
    }

    private lateinit var list: RankListInfoItem
    private val adapter = RankListAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        adapter.setOnItemClickListener { adapter, view, position ->
            val rank = list.ranks?.get(position)!!
            toRankBookList(rank.rankName!!, list.channelId, rank.rankId)
        }
        mViewBind.rvFccList.adapter = adapter
        mViewBind.rvFccList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun initData() {
        list = requireArguments().getParcelable(EXTRA_RANK_LIST)!!
        adapter.setNewInstance(list.ranks?.toMutableList())
    }
}