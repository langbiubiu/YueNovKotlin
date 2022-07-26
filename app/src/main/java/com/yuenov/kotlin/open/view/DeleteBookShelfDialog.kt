package com.yuenov.kotlin.open.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.yuenov.kotlin.open.databinding.ViewPopwindowDeletebookshelfBinding
import com.yuenov.kotlin.open.ext.setClickListener

class DeleteBookShelfDialog(context: Context, position: Int) : Dialog(context) {

    init {
        val window = window
        // 拿到Dialog的Window, 修改Window的属性
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.decorView.setPadding(0, 0, 0, 0)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 获取Window的LayoutParams
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.gravity = Gravity.BOTTOM
        // 一定要重新设置, 才能生效
        window.attributes = attributes
    }

    private val binding: ViewPopwindowDeletebookshelfBinding by lazy {
        ViewPopwindowDeletebookshelfBinding.inflate(layoutInflater)
    }

    private val position = position

    interface IDeleteBookShelfListener {
        fun toPreviewDetail(position: Int)
        fun toDelete(position: Int)
        fun toCancel()
    }

    private var listener: IDeleteBookShelfListener? = null

    fun setListener(listener: IDeleteBookShelfListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setClickListener(
            binding.viewPopDbsClose,
            binding.tvPopDbsDelete,
            binding.tvPopDbsDetail,
            binding.tvPopDbsCancel
        ) {
            dismiss()
            when (it) {
                binding.viewPopDbsClose, binding.tvPopDbsCancel -> listener?.toCancel()
                binding.tvPopDbsDetail -> listener?.toPreviewDetail(position)
                binding.tvPopDbsDelete -> listener?.toDelete(position)
            }
        }
    }
}