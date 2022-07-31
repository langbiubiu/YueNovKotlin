package com.yuenov.kotlin.open.fragment

import android.os.Bundle
import com.yuenov.kotlin.open.base.BaseFragment
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.FragmentReadBinding
import com.yuenov.kotlin.open.utils.DataStoreUtils
import com.yuenov.kotlin.open.viewmodel.BookShelfFragmentViewModel
import com.yuenov.kotlin.open.widget.mypage.ReadSettingInfo

/**
 * TODO:阅读界面
 */
class ReadFragment : BaseFragment<BookShelfFragmentViewModel, FragmentReadBinding>() {

    private val readSettingInfo: ReadSettingInfo = DataStoreUtils.getJsonData(
        PreferenceConstants.KEY_READ_SETTING_INFO,
        ReadSettingInfo::class.java
    )

    override fun initView(savedInstanceState: Bundle?) {

    }
}