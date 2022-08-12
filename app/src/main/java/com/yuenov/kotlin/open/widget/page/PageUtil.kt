package com.yuenov.kotlin.open.widget.page

import android.graphics.Paint
import android.graphics.Rect
import com.yuenov.kotlin.open.utils.ConvertUtils

object PageUtil {
    private var testWordRec: Rect? = null
    // 一行最多可以战士多少个“中”字
    private var pubLineShowWords = 0
    private const val testWord = "中"

    fun getLineSpacingExtra(textSize: Float): Int {
        val textLine = TextLine()
        textLine.textSize = textSize - 8
        return getTestWordHeight(textLine)
    }



    private fun getTestWordHeight(textLine: TextLine): Int {
        val testWordPaint = Paint()
        val testWordRect = Rect()
        testWordPaint.apply {
            textSize = ConvertUtils.dp2px(textLine.textSize).toFloat()
            isAntiAlias = true
            getTextBounds(testWord, 0, testWord.length, testWordRect)
        }
        return testWordRect.height()
    }
}