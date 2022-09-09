package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.databinding.FragmentCategorychannelBinding
import com.yuenov.kotlin.open.databinding.ViewMenuTablayoutTitleBinding
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.model.response.RankListInfoItem
import com.yuenov.kotlin.open.viewmodel.RankFragmentViewModel
import me.hgj.jetpackmvvm.ext.nav

class RankFragment : BaseFragment<RankFragmentViewModel, FragmentCategorychannelBinding>() {

    private lateinit var customView: ViewMenuTablayoutTitleBinding
    private var channelList: ArrayList<RankListInfoItem> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        customView = ViewMenuTablayoutTitleBinding.inflate(layoutInflater)
        mViewBind.apply {
            setClickListener(rlCcBack) {
                nav().navigateUp()
            }
            tlCcMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    customView.tvMttName.text = channelList[tab.position].channelName
                    tab.customView = customView.root
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.customView = null
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}

            })
        }
    }

    override fun lazyLoadData() {
        mViewModel.getRankList()
    }

    override fun createObserver() {
        mViewModel.getRankListState.observe(viewLifecycleOwner) {
            if (it.isSuccess && !it.isEmpty) {
                val rankList = it.listData
                val fragments = mutableListOf<RankListFragment>()
                rankList.forEach {
                    fragments.add(RankListFragment.getFragment(it))
                }
                mViewBind.apply {
                    vpCcContent.adapter = object : FragmentStateAdapter(requireActivity()) {
                        override fun getItemCount(): Int {
                            return fragments.size
                        }

                        override fun createFragment(position: Int): Fragment {
                            return fragments[position]
                        }

                    }
                    TabLayoutMediator(tlCcMenu, vpCcContent) { tab, position ->
                        tab.text = channelList[position].channelName
                    }.attach()
                }
            }
        }
    }
}