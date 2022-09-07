package com.yuenov.kotlin.open.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.databinding.ViewWidgetRankitemImageBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.setClickListener

class RankItemImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    interface BdItemImageViewListener {
        fun categoryItemImageViewOnClick()
    }

    var listener: BdItemImageViewListener? = null
    private val binding : ViewWidgetRankitemImageBinding

    init {
        binding = ViewWidgetRankitemImageBinding.inflate(LayoutInflater.from(context))
        addView(binding.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        setClickListener(binding.ivWgBii1, binding.ivWgBii2, binding.ivWgBii3) {
            listener?.categoryItemImageViewOnClick()
        }
    }

    fun setData(images: List<String>) {
        if (images.isEmpty()) return
        binding.apply {
            ivWgBii1.loadImage(images[0], R.mipmap.ic_book_list_default)
            if (images.size > 1)
                ivWgBii2.loadImage(images[1], R.mipmap.ic_book_list_default)
            if (images.size > 2)
                ivWgBii3.loadImage(images[2], R.mipmap.ic_book_list_default)
        }
    }
}