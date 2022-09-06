package com.yuenov.kotlin.open.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.yuenov.kotlin.open.databinding.ViewActivityTitlebarBinding

/**
 * My app title
 */
class MyAppTitle @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var binding: ViewActivityTitlebarBinding

    init {
        binding = ViewActivityTitlebarBinding.inflate(LayoutInflater.from(context))
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        this.addView(binding.root, layoutParams)
    }

    fun getCenterView(): TextView = binding.tvCenterTitle

    fun getRightView(): TextView = binding.tvRight

    fun getLeftView(): TextView = binding.tvLeft

    fun getLineView(): View = binding.viewLineAt

}