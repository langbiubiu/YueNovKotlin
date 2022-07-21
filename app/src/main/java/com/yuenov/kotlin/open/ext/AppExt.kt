package com.yuenov.kotlin.open.ext

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yuenov.kotlin.open.R
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction

/**
 * 跳转至阅读界面
 */
fun AppCompatActivity.toRead() {

}

/**
 * 跳转至阅读界面
 */
fun Fragment.toRead() {

}

/**
 * 跳转至详情界面
 */
fun Fragment.toDetail(bookId: Int) {
    nav().navigateAction(R.id.action_main_to_detail, Bundle().apply {

    })
}

/**
 * 跳转至搜索界面
 */
fun AppCompatActivity.toSearch() {

}

/**
 * 跳转至搜索界面
 */
fun Fragment.toSearch() {

}