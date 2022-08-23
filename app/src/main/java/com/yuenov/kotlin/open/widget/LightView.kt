package com.yuenov.kotlin.open.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.databinding.ViewWidgetLightBinding
import com.yuenov.kotlin.open.ext.resetVisibility
import com.yuenov.kotlin.open.ext.setClickListener

class LightView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs), View.OnClickListener {

    interface LightViewListener {
        fun onStateChange(view: View, select: Boolean)
    }

    private var binding: ViewWidgetLightBinding
    private var listener: LightViewListener? = null

    //属性名不能用isSelected，因为和View.isSelected()函数重复，无语
    private var select: Boolean = false

    init {
        binding = ViewWidgetLightBinding.inflate(LayoutInflater.from(context))
        binding.apply {
            addView(root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            setClickListener(riWgInside, riWgOutside, listener = this@LightView)

            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.LightView)
            val inSizeResource = a.getResourceId(R.styleable.LightView_inSideResource, 0)
            riWgInside.setImageResource(inSizeResource)

            val inSideCenterResource = a.getResourceId(R.styleable.LightView_centerResource, 0)
            if (inSizeResource > 0) ivWgCenter.setImageResource(inSideCenterResource)

            val outSizeResource = a.getResourceId(R.styleable.LightView_outSideResource, 0)
            riWgOutside.setImageResource(outSizeResource)
            a.recycle()
        }
    }

    fun setListener(listener: LightViewListener) {
        this.listener = listener
    }

    fun setSelect(value: Boolean) {
        select = value
        resetVisibility(select, binding.riWgOutside)
    }

    override fun onClick(v: View?) {
        if (select) return
        select = true
        resetVisibility(select, binding.riWgOutside)
        listener?.onStateChange(this, select)
    }
}