package com.yuenov.kotlin.open.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.yuenov.kotlin.open.databinding.ViewPopwindowDeletebookshelfBinding
import com.yuenov.kotlin.open.ext.setClickListener

class DeleteBookShelfDialog(context: Context, private val position: Int) : Dialog(context) {

    private val binding: ViewPopwindowDeletebookshelfBinding

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
        binding = ViewPopwindowDeletebookshelfBinding.inflate(layoutInflater)
    }

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
        binding.apply {
            setContentView(root)
            setClickListener(
                viewPopDbsClose,
                tvPopDbsDelete,
                tvPopDbsDetail,
                tvPopDbsCancel
            ) {
                dismiss()
                when (it) {
                    viewPopDbsClose, tvPopDbsCancel -> listener?.toCancel()
                    tvPopDbsDetail -> listener?.toPreviewDetail(position)
                    tvPopDbsDelete -> listener?.toDelete(position)
                }
            }
        }
    }
}