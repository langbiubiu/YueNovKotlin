package com.yuenov.kotlin.open.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.yuenov.kotlin.open.application.appViewMode
import com.yuenov.kotlin.open.base.BaseActivity
import com.yuenov.kotlin.open.constant.PreferenceConstants
import com.yuenov.kotlin.open.databinding.ActivityFirstBinding
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import com.yuenov.kotlin.open.utils.DataStoreUtils
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class WelcomeActivity : BaseActivity<BaseViewModel, ActivityFirstBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.root.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 500)
    }

    override fun createObserver() {
        appViewMode.appConfigInfo.observeInActivity(this, Observer {
            logd(CLASS_TAG, "AppConfigInfo observe")
            DataStoreUtils.putJsonData(PreferenceConstants.KEY_CATEGORY_INFO, it)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewMode.updateAppConfigInfo()
    }

}