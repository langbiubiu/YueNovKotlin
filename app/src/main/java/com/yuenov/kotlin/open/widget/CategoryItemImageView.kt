package com.yuenov.kotlin.open.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yuenov.kotlin.open.R
import com.yuenov.kotlin.open.databinding.ViewWidgetCategoryitemImageBinding
import com.yuenov.kotlin.open.ext.loadImage
import com.yuenov.kotlin.open.ext.setClickListener

class CategoryItemImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    interface CategoryItemImageViewListener {
        fun categoryItemImageViewOnClick()
    }

    private val binding: ViewWidgetCategoryitemImageBinding
    private var listener : CategoryItemImageViewListener? = object : CategoryItemImageViewListener {
        override fun categoryItemImageViewOnClick() {
            clickAction()
        }
    }
    private var clickAction: () -> Unit = {}

    init {
        binding = ViewWidgetCategoryitemImageBinding.inflate(LayoutInflater.from(context))
        binding.apply {
            addView(root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            setClickListener(ivWgcgi1, ivWgcgi2, ivWgcgi3) {
                listener?.categoryItemImageViewOnClick()
            }
        }
    }

    fun setListener(lis: CategoryItemImageViewListener) {
        listener = lis
    }

    fun setListener(action: () -> Unit) {
        clickAction = action
    }

    fun setData(images: List<String>?) {
        if (images.isNullOrEmpty()) return
        binding.apply {
            ivWgcgi1.loadImage(images[0], R.mipmap.ic_book_list_default)
            if (images.size > 1) ivWgcgi2.loadImage(images[1], R.mipmap.ic_book_list_default)
            if (images.size > 2) ivWgcgi3.loadImage(images[2], R.mipmap.ic_book_list_default)
        }
    }

}