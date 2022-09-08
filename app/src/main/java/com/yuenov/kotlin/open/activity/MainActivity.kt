package com.yuenov.kotlin.open.activity

import android.os.Bundle
import android.view.KeyEvent
import com.yuenov.kotlin.open.base.BaseActivity
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.ActivityMainBinding

class MainActivity : BaseActivity<BaseFragmentViewModel, ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

}