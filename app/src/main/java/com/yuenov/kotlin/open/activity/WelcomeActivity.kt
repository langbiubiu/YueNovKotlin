package com.yuenov.kotlin.open.activity

import android.content.Intent
import android.os.Bundle
import com.yuenov.kotlin.open.base.BaseActivity
import com.yuenov.kotlin.open.base.BaseFragmentViewModel
import com.yuenov.kotlin.open.databinding.ActivityFirstBinding

class WelcomeActivity : BaseActivity<BaseFragmentViewModel, ActivityFirstBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.root.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 500)
    }
}