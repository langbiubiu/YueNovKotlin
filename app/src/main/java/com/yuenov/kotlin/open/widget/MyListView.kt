package com.yuenov.kotlin.open.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import com.yuenov.kotlin.open.R

class MyListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ListView(context, attrs) {
    private var maxHeight = 100f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MyListView)
        for (i in 0..a.indexCount) {
            val type = a.getIndex(i)
            if (type == R.styleable.MyListView_maxHeight) {
                maxHeight = a.getDimension(type, -1f)
            }
        }
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specSize = MeasureSpec.getSize(heightMeasureSpec)
        var heightSpec = heightMeasureSpec
        if (maxHeight <= specSize && maxHeight > -1) {
            heightSpec = MeasureSpec.makeMeasureSpec(maxHeight.toInt(), MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    fun setMaxHeight(max: Int) {
        maxHeight = max.toFloat()
    }
}