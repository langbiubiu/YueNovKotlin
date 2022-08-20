package com.yuenov.kotlin.open.widget.page

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import kotlin.math.max

object PageUtil {
    private var testWordRect: Rect = Rect()
    private var testTextSize: Float = 0f

    // 一行最多可以战士多少个“中”字
    private var pubLineShowWords = 0
    private const val testWord = "中"

    fun getLineSpacingExtra(textSize: Float): Int {
        return getTestWordHeight(textSize - 8)
    }

    fun getPageList(
        chapterName: String,
        content: String,
        tipTextSize: Float,
        textSize: Float,
        view: View
    ): List<Page> {
        val pageList = mutableListOf<Page>()
        // 每章第一行是章节名，字体大小为普通文字的1.3倍
        val titleTextSize = textSize * 1.3f
        val lineSpacing = getLineSpacingExtra(textSize)
        val textLineList = mutableListOf<TextLine>()
        // 临时的TextLine对象，用于数据缓存
        var tempTextLine = TextLine()
        var start = 0
        tempTextLine.textSize = textSize
        tempTextLine.height = getTestWordHeight(textSize)
        textLineList.add(tempTextLine)
        // 每章开头的章节名
        while (true) {
            tempTextLine = getShowLines(chapterName, titleTextSize, true, start, view)
            if (tempTextLine.textLength <= 0) break
            tempTextLine.isTitle = true
            tempTextLine.textSize = titleTextSize
            tempTextLine.fakeBoldText = true
            textLineList.add(tempTextLine)
            start += tempTextLine.textLength
        }

        // 按照段落分开
        val arrWrapContent = content.split("\n")
        for (paragraph in arrWrapContent) {
            start = 0
            var pos = 0
            while (true) {
                // 空内容显示为空白行
                if (paragraph.isEmpty()) {
                    tempTextLine = TextLine()
                    tempTextLine.textSize = textSize
                    tempTextLine.height = getTestWordHeight(textSize)
                    textLineList.add(tempTextLine)
                    break
                }
                // 正常数据
                tempTextLine = getShowLines(paragraph, textSize, false, start, view)
                if (tempTextLine.textLength <= 0) break
                tempTextLine.partFirstLine = (pos == 0)
                tempTextLine.isTitle = false
                tempTextLine.fakeBoldText = false
                textLineList.add(tempTextLine)
                start += tempTextLine.textLength
                pos++
            }
        }
        // 分页
        var page = Page()
        // 当前页高度
        var pageTextHeight = 0
        // 可用的高度
        val contentWordsHeight = view.measuredHeight - view.paddingTop - view.paddingBottom
        // 异常，无法获取控件高度
        if (contentWordsHeight <= 0) return pageList
        var i = 0
        // 填充PageList
        while (i < textLineList.size) {
            // 首页添加标题头
            if (page.textLines.isEmpty()) {
                // 空行
                tempTextLine = TextLine()
                tempTextLine.textSize = textSize
                tempTextLine.height = lineSpacing
                page.textLines.add(tempTextLine)
                pageTextHeight += tempTextLine.height
                // 页面顶部章节名
                tempTextLine = TextLine()
                tempTextLine.isChapter = true
                tempTextLine.textSize = tipTextSize
                tempTextLine.text = chapterName
                tempTextLine.height = getTestWordHeight(tipTextSize)
                page.textLines.add(tempTextLine)
                pageTextHeight += tempTextLine.height
                // 空行
                tempTextLine = TextLine()
                tempTextLine.textSize = textSize
                tempTextLine.height = (lineSpacing * 1.3f).toInt()
                page.textLines.add(tempTextLine)
                pageTextHeight += tempTextLine.height
            } else { // 第一行以后  添加上间距
                tempTextLine = TextLine()
                // 上一行是title：加一空行
                if (textLineList[i - 1].isTitle) {
                    tempTextLine.textSize = titleTextSize
                    tempTextLine.height = getTestWordHeight(tempTextLine.textSize)
                } else { // 上一行是普通文字：加上下间距
                    tempTextLine.textSize = textSize
                    // 段落换行间隔大2倍
                    tempTextLine.height =
                        if (textLineList[i].partFirstLine) (lineSpacing * 2) else lineSpacing
                }
                page.textLines.add(tempTextLine)
                pageTextHeight += tempTextLine.height
            }
            page.textLines.add(textLineList[i])
            // 累加行高
            pageTextHeight += textLineList[i].height
            // 一页放不下时 移除最后一行，并另启一页
            if (pageTextHeight > contentWordsHeight) {
                page.textLines.removeLast()
                page.pageNum = pageList.size
                page.chapterName = chapterName
                // Page内容不为空
                if (page.textLines.any { !it.isChapter && !it.isTitle && !it.text.isNullOrEmpty() })
                    pageList.add(page)
                page = Page()
                pageTextHeight = 0
                i--
            } else if (i == textLineList.size - 1) { // 最后一行
                if (page.textLines.any { !it.isChapter && !it.isTitle && !it.text.isNullOrEmpty() }) {
                    page.pageNum = pageList.size
                    page.chapterName = chapterName
                    pageList.add(page)
                }
            }
            i++
        }
        return pageList
    }

    private fun getTestWordHeight(textSize: Float): Int {
        if (textSize != testTextSize) {
            testTextSize = textSize
            val testWordPaint = Paint()
            testWordPaint.textSize = dp2px(textSize)
            testWordPaint.isAntiAlias = true
            testWordPaint.getTextBounds(testWord, 0, testWord.length, testWordRect)
        }
        return testWordRect.height()
    }

    // 获取通常情况下一行可以展示多少个"中"字，作为测量基准，减少计算量
    private fun initPubShowLineWords(textSize: Float, fakeBold: Boolean, view: View) {
        val showWidth = view.measuredWidth - view.paddingLeft - view.paddingRight
        val paint = Paint()
        paint.textSize = dp2px(textSize)
        paint.isFakeBoldText = fakeBold
        paint.isAntiAlias = true
        // 测量用的最大宽度
        val maxWidth = showWidth * 10
        // 测量结果
        val measuredWidth = FloatArray(1)
        val sbWords = StringBuffer(testWord)
        var i = 0
        while (true) {
            paint.breakText(sbWords.toString(), true, maxWidth.toFloat(), measuredWidth)
            if (measuredWidth[0] <= showWidth) {
                sbWords.append(testWord)
                i++
            } else {
                pubLineShowWords = i - 1
                break
            }
        }
    }

    private fun getShowLines(
        content: String,
        textSize: Float,
        fakeBold: Boolean,
        start: Int,
        view: View
    ): TextLine {
        val textLine = TextLine()
        if (content.length <= start)
            return textLine
        val showWidth = view.measuredWidth - view.paddingLeft - view.paddingRight
        val paint = Paint()
        paint.textSize = dp2px(textSize)
        paint.isFakeBoldText = fakeBold
        paint.isAntiAlias = true
        // 测量结果
        val measuredWidth = FloatArray(1)
        // 可测量的字符小于通用字符数: 测量剩余所有的字符
        // 否则测量通用字符数
        val sb = StringBuffer()
        if (pubLineShowWords == 0) {
            initPubShowLineWords(textSize, fakeBold, view)
            logd(CLASS_TAG, "getShowLines pubLineShowWords:$pubLineShowWords")
        }
        if ((content.length - start) <= pubLineShowWords) {
            sb.append(content.substring(start))
        } else {
            sb.append(content.substring(start, start + pubLineShowWords))
        }
        val maxWidth = showWidth * 10
        var beginIndex: Int
        // sb的宽度是否已经超出了可显示宽度
        var hasOutSize = false
        while (true) {
            paint.breakText(sb.toString(), true, maxWidth.toFloat(), measuredWidth)
            // 如果sb宽度没有超出可显示宽度
            if (measuredWidth[0] <= showWidth) {
                // 如果之前超出过可显示宽度，且现在宽度小于可显示宽度，表示填满了一行
                if (hasOutSize) break
                beginIndex = start + sb.length
                if (content.length <= beginIndex) {
                    // 已到达content结束位置
                    break
                } else {
                    sb.append(content.substring(beginIndex, beginIndex + 1))
                }
            } else { // 如果sb宽度超出可显示宽度，去除最后一个字符
                hasOutSize = true
                sb.deleteCharAt(sb.length - 1)
                break
            }
        }
        textLine.text = sb.toString()
        textLine.textLength = sb.length
        textLine.textSize = textSize
        val rect = Rect()
        paint.getTextBounds(textLine.text, 0, textLine.textLength, rect)
        textLine.height = max(rect.height(), getTestWordHeight(textSize))
        return textLine
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    fun dp2px(dpValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return dpValue * scale + 0.5f
    }
}