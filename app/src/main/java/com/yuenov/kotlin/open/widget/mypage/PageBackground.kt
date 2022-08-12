package com.yuenov.kotlin.open.widget.mypage

import androidx.annotation.ColorRes
import com.yuenov.kotlin.open.R

//背景颜色和配对的字体颜色
enum class PageBackground(@ColorRes var bgColor: Int, @ColorRes var textColor: Int) {
    TYPE_1(R.color.bg1, R.color.gray_0000),
    TYPE_2(R.color.bg2, R.color.gray_2d2d),
    TYPE_3(R.color.bg3, R.color.gray_3f4c),
    TYPE_4(R.color.bg4, R.color.gray_442e),
    TYPE_5(R.color.bg5, R.color.gray_3333)
}