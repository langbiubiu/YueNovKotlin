package com.yuenov.kotlin.open.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

class WrapHeightGridView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //因为组件高度设为wrap_content，高度被父控件限制，所以无法显示全部子控件
        //size设为Int.MAX_VALUE shr 2，相当于把控件的最大高度设到极限值，并使用AT_MOST模式，让子控件能完全显示
        //使用Int.MAX_VALUE shr 2是因为这是size支持的最大值
        val heightSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}