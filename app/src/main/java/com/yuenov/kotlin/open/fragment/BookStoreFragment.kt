package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yuenov.kotlin.open.application.appViewModel
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.FragmentBookstoreBinding
import com.yuenov.kotlin.open.databinding.ViewMenuBookcityTablayoutTitleBinding
import com.yuenov.kotlin.open.ext.*

/**
 * 书城界面
 */
class BookStoreFragment : BaseFragment<BaseFragmentViewModel, FragmentBookstoreBinding>(), TabLayout.OnTabSelectedListener {

    private lateinit var customViewBind: ViewMenuBookcityTablayoutTitleBinding
    private val categoryNameList: MutableList<String> = mutableListOf()
    private val categoryIdList: MutableList<Int> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        customViewBind = ViewMenuBookcityTablayoutTitleBinding.inflate(layoutInflater)
        mViewBind.apply {
            setClickListener(rlBcBcSearch, rlBcBcSearch) {
                when (it) {
                    rlBcBcSearch -> toSearch()
                    rlBcCategoryChannel -> toCategoryChannel()
                }
            }
            tlBcMenu.addOnTabSelectedListener(this@BookStoreFragment)
        }
    }

    override fun initData() {
        if (appViewModel.appConfigInfo.value?.categories.isNullOrEmpty()) return
        val categories = appViewModel.appConfigInfo.value!!.categories
        val fragmentList = mutableListOf<BookListFragment>()
        if (categories != null) {
            categoryNameList.clear()
            categoryIdList.clear()
            categories.forEach {
                categoryNameList.add(it.categoryName!!)
                categoryIdList.add(it.categoryId!!)
                fragmentList.add(BookListFragment.getFragment(it.categoryName, it.categoryId))
            }
        }
        mViewBind.apply {
            vpBcContent.adapter = object : FragmentStateAdapter(requireActivity()) {
                override fun getItemCount(): Int {
                    return categoryNameList.size
                }

                override fun createFragment(position: Int): Fragment {
                    return fragmentList[position]
                }

            }
            TabLayoutMediator(tlBcMenu, vpBcContent) { tab, position ->
                tab.text = categoryNameList[position]
            }.attach()
        }
    }

    override fun createObserver() {
        appViewModel.appConfigInfo.observeInFragment(this) {
            initData()
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        customViewBind.tvMbtName.text = categoryNameList[tab.position]
        tab.customView = customViewBind.root
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        tab.customView = null
    }

    override fun onTabReselected(tab: TabLayout.Tab) {}
}