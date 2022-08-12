package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.FragmentMainBinding
import com.yuenov.kotlin.open.ext.*

class MainFragment : BaseFragment<BaseFragmentViewModel, FragmentMainBinding>() {

    private var fragments: ArrayList<Fragment> = arrayListOf()
    private var exitTime = 0L

    init {
        fragments.add(BookShelfFragment())
        fragments.add(DiscoverFragment())
        fragments.add(BookStoreFragment())
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.apply {
            mainViewpager.init(this@MainFragment, fragments, fragments.size, false)
            mainBottom.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_bookshelf -> mainViewpager.setCurrentItem(0, false)
                    R.id.menu_discover -> mainViewpager.setCurrentItem(1, false)
                    R.id.menu_bookstore -> mainViewpager.setCurrentItem(2, false)
                }
                true
            }
            mainBottom.interceptLongClick()
        }
    }

    override fun needBackPressedCallback(): Boolean {
        return true
    }

    override fun onBackPressed() {
        logd(CLASS_TAG, "onBackPressed")
        if (System.currentTimeMillis() - exitTime > 2000) {
            showToast(R.string.news_exit_twice_string)
            exitTime = System.currentTimeMillis()
        } else {
            activity?.finish()
        }
    }

    fun toBookShelf() { mViewBind.mainBottom.selectedItemId = R.id.menu_bookstore }

    fun toDiscover() { mViewBind.mainBottom.selectedItemId = R.id.menu_discover }

    fun toBookStore() { mViewBind.mainBottom.selectedItemId = R.id.menu_bookstore }
}