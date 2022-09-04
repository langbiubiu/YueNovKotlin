package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.reflect.TypeToken
import com.yuenov.kotlin.open.application.gson
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentCategorychannelBinding
import com.yuenov.kotlin.open.databinding.ViewMenuTablayoutTitleBinding
import com.yuenov.kotlin.open.ext.setClickListener
import com.yuenov.kotlin.open.model.response.CategoryInfoItem
import com.yuenov.kotlin.open.model.response.ChannelInfoItem
import com.yuenov.kotlin.open.viewmodel.CategoryChannelViewModel
import me.hgj.jetpackmvvm.ext.nav

class CategoryChannelFragment: BaseFragment<CategoryChannelViewModel, FragmentCategorychannelBinding>() {

    private lateinit var customView: ViewMenuTablayoutTitleBinding
    private var channelList: ArrayList<ChannelInfoItem> = arrayListOf()

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

    override fun initData() {
        val data = mViewModel.getCacheContent(PreferenceConstants.TYPE_CATEGORY_CHANNEL)
        if (data.isNotEmpty()) {
            channelList =
                gson.fromJson(data, object : TypeToken<ArrayList<ChannelInfoItem>>() {}.type)
            setupData()
        }
    }

    override fun lazyLoadData() {
        mViewModel.getCategoryChannelList()
    }

    override fun createObserver() {
        mViewModel.apply {
            getCategoryChannelListState.observe(viewLifecycleOwner) {
                if (it.isSuccess && !it.isEmpty) {
                    channelList = it.listData as ArrayList<ChannelInfoItem>
                    setCacheContent(PreferenceConstants.TYPE_CATEGORY_CHANNEL, gson.toJson(channelList))
                    setupData()
                }
            }
        }
    }

    private fun setupData() {
        val fragments = mutableListOf<CategoryChannelItemFragment>()
        channelList.forEach {
            fragments.add(CategoryChannelItemFragment.getFragment(it.categories, it.channelId))
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