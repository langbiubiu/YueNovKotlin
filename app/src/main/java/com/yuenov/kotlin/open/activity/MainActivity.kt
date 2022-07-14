package com.yuenov.kotlin.open.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.base.BaseActivity
import com.yuenov.kotlin.open.databinding.ActivityMainBinding
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {

    var exitTime = 0L
    override fun initView(savedInstanceState: Bundle?) {
        //进入首页检查更新
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainActivity, R.id.host_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.main_fragment) {
                    //如果当前界面不是主页，那么直接调用返回即可
                    nav.navigateUp()
                } else {
                    //是主页
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.news_exit_twice_string,
                            Toast.LENGTH_SHORT
                        ).show()
                        exitTime = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }
        })
    }

}