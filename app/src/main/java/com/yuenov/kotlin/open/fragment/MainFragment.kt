package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.databinding.FragmentMainBinding
import com.yuenov.kotlin.open.ext.init
import com.yuenov.kotlin.open.viewmodel.MainFragmentViewModel

class MainFragment : BaseFragment<MainFragmentViewModel, FragmentMainBinding>() {

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.add(BookShelfFragment())
        fragments.add(DiscoverFragment())
        fragments.add(BookStoreFragment())
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.mainViewpager.init(this, fragments, fragments.size, false)
        mViewBind.mainBottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_bookshelf -> mViewBind.mainViewpager.setCurrentItem(0, false)
                R.id.menu_discover -> mViewBind.mainViewpager.setCurrentItem(1, false)
                R.id.menu_bookstore -> mViewBind.mainViewpager.setCurrentItem(2, false)
            }
            true
        }
    }

    fun toBookShelf() { mViewBind.mainBottom.selectedItemId = R.id.menu_bookstore }

    fun toDiscover() { mViewBind.mainBottom.selectedItemId = R.id.menu_discover }

    fun toBookStore() { mViewBind.mainBottom.selectedItemId = R.id.menu_bookstore }
}