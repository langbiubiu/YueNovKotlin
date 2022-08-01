package com.yuenov.kotlin.open.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.base.BaseActivity
import com.yuenov.kotlin.open.databinding.ActivityMainBinding
import com.yuenov.kotlin.open.ext.showToast
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {}

}