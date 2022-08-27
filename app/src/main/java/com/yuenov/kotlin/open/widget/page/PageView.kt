package com.yuenov.kotlin.open.widget.page

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.ColorInt
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.widget.page.animation.PageAnimation
import com.yuenov.kotlin.open.widget.page.animation.VerticalPageAnimation
import me.hgj.jetpackmvvm.ext.util.dp2px
import kotlin.math.abs

class PageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var startX = 0f
    private var startY = 0f

    // 是否滑动了
    private var hasMoved = false

    // View是否已完成布局
    private var isPrepare = false

    // 当前显示的Page
    private var curPage: Page = Page()

    // 翻页后显示的Page
    private var nextPage: Page = Page()

    // 当前章节的所有页面列表
    private var curPageList = listOf<Page>()

    // 下一章节的所有页面列表
    private var nextPageList = listOf<Page>()

    private var isNextChapter = false

    // 当前显示的PageNumber
    var curPageNum = 0

    // 中间区域范围
    private lateinit var centerRect: Rect

    // 背景色
    private val bgColor: Int
        @ColorInt get() = context.getColor(pageLoader.getBgColor())

    // 字体颜色
    private val textColor: Int
        @ColorInt get() = context.getColor(pageLoader.getTextColor())

    // 字体大小
    private val textSize: Float
        get() = pageLoader.getTextSize()

    // 翻页动画
    private val pageAnimation: PageAnimation
        get() = pageLoader.getPageAnimation()

    // 右下角进度字体大小
    private var processTextSize: Float = 13f

    // 顶部标题字体大小
    private var titleTextSize: Float = 13f

    // 时间字体大小
    private var timeTextSize: Float = 13f

    private val paint: Paint = Paint()
    private var batteryPaint: Paint? = null
    private var timePaint: Paint? = null
    private var processPaint: Paint? = null

    // 电极范围
    private val polarRect = Rect(0, 0, 0, 0)

    // 电池外框范围
    private val outFrameRect = Rect(0, 0, 0, 0)

    // 电池内框范围
    private val inFrameRect = Rect(0, 0, 0, 0)

    // 外框的宽度
    private val outBorder = dp2px(1)

    // 外框和电极的间距
    private val polarSpace = dp2px(1)

    // 外框与内框的间距
    private val innerSpace = dp2px(1)

    // 一个默认的PageLoader
    private var pageLoader: IPagerLoader = DefaultPageLoader()

    var touchListener: TouchListener? = null

    var listener: PageViewListener? = null

    fun setPageLoader(loader: IPagerLoader) {
        logD(CLASS_TAG, "setPageLoader")
        pageLoader = loader
        pageAnimation.setView(this)
        updateContent()
    }

    fun updateContent() {
        curPageList = PageUtil.getPageList(
            pageLoader.getTitle(),
            pageLoader.getContent(),
            titleTextSize,
            textSize,
            this
        )
        if (curPageNum >= curPageList.size) {
            curPageNum = curPageList.size -1
        }
        curPage = curPageList[curPageNum]
        logD(
            CLASS_TAG,
            "initData page list:${curPageList.size}, cur page = ${curPageNum}, cur page num = ${curPage.pageNum}"
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerRect = Rect(w / 4, h / 3, w * 3 / 4, h * 2 / 3)
        pageAnimation.setView(this)
        drawPage(false)
        isPrepare = true
    }

    /**
     * 当动画执行时，把当前页的bitmap(curBitmap) copy给nextBitmap（其实应该叫prevBitmap了），
     * 因为已经把内容填充好了，所以不需要再执行一遍drawPage()，其实就是HorizontalPageAnimation里的changePage()
     * 然后调用swapPage()来交换数据，再执行drawPage()
     */
    override fun onDraw(canvas: Canvas) {
        if (isPrepare) {
            pageAnimation.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!pageLoader.canTouch()) return true
        super.onTouchEvent(event)
        val x = event.x
        val y = event.y
        val allowPageAnimation = pageLoader.allowPageAnimation()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = x
                startY = y
                hasMoved = false
            }
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(context).scaledTouchSlop
                if (!hasMoved)
                    hasMoved = abs(startX - x) > slop || abs(startY - y) > slop
            }
            MotionEvent.ACTION_UP -> {
                if (!hasMoved && centerRect.contains(x.toInt(), y.toInt())) {
                    touchListener?.onCenter()
                    return true
                } else {
                    touchListener?.onTouchUp()
                }
            }
        }
        if (allowPageAnimation) {
            pageAnimation.onTouchEvent(event)
        }
        return true
    }

    override fun computeScroll() {
        pageAnimation.scrollAnim()
        super.computeScroll()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pageAnimation.abortAnim()
    }

    fun getPageCount(): Int = curPageList.size

    /**
     * 只有这个方法会主动触发重新绘制
     */
    fun drawCurPage(isUpdate: Boolean) {
        logD(CLASS_TAG, "drawCurPage isUpdate:$isUpdate")
        if (!isUpdate) {
            if (pageAnimation is VerticalPageAnimation) {
                (pageAnimation as VerticalPageAnimation).resetBitmap()
            }
        } else {
            pageAnimation.setView(this)
        }
        drawPage(false)
        invalidate()
    }

    // 这里其实是修改PageAnimation中的bitmap，实际的绘制还是在invalidate触发的onDraw里面
    private fun drawPage(isNext: Boolean) {
        drawBackground(isNext)
        drawContent(isNext)
        drawBattery(isNext)
        drawTime(isNext)
        drawProcess(isNext)
    }

    private fun drawBackground(isNext: Boolean) {
        val bitmap = if (isNext) pageAnimation.nextBitmap else pageAnimation.curBitmap
        val canvas = Canvas(bitmap)
        canvas.drawColor(bgColor)
    }

    // 将内容填充到bitmap中，真正的绘制是在onDraw() -> PageAnimation.draw()中
    private fun drawContent(isNext: Boolean) {
        val bitmap = if (isNext) pageAnimation.nextBitmap else pageAnimation.curBitmap
        val canvas = Canvas(bitmap)
        if (pageAnimation is VerticalPageAnimation)
            canvas.drawColor(bgColor)
        if (curPageList.isEmpty()) return
        // 绘制内容
        var x = 0f
        var y = 0f
        val page = if (isNext) nextPage else curPage
        for (i in page.textLines.indices) {
            val textLine = page.textLines[i]
            paint.textSize = dp2px(textLine.textSize.toInt()).toFloat()
            paint.color = textColor
            paint.isFakeBoldText = textLine.fakeBoldText
            paint.isAntiAlias = true
            // 第一行：paddingTop + 文字高度
            if (i == 0) {
                x = paddingLeft.toFloat()
                y = paddingTop.toFloat() + textLine.height
            } else {
                y += textLine.height.toFloat()
            }
            if (!textLine.text.isNullOrEmpty())
                canvas.drawText(textLine.text!!, x, y, paint)
        }
    }

    private fun drawBattery(isNext: Boolean) {
        // 绘制电池
        val bitmap = if (isNext) pageAnimation.nextBitmap else pageAnimation.curBitmap
        val canvas = Canvas(bitmap)
        if (batteryPaint == null) {
            batteryPaint = Paint()
            batteryPaint!!.isAntiAlias = true
            batteryPaint!!.isDither = true
            // 电池的左下角坐标
            val baseLeft = paddingLeft + dp2px(6)
            val baseBottom = height - dp2px(12)
            // 外框宽高
            val outFrameWidth = dp2px(21)
            val outFrameHeight = dp2px(10)
            // 电极宽高
            val polarWidth = dp2px(3)
            val polarHeight = dp2px(6)
            // 外框范围
            outFrameRect.set(
                baseLeft,
                baseBottom - outFrameHeight,
                baseLeft + outFrameWidth,
                baseBottom
            )
            // 电极范围
            polarRect.set(
                baseLeft + outFrameWidth + polarSpace,
                baseBottom - (outFrameHeight + polarHeight) / 2,
                baseLeft + outFrameWidth + polarSpace + polarWidth,
                baseBottom - (outFrameHeight - polarHeight) / 2
            )
            inFrameRect.set(
                outFrameRect.left + outBorder + innerSpace,
                outFrameRect.top + outBorder + innerSpace,
                outFrameRect.right - outBorder - innerSpace,
                outFrameRect.bottom - outBorder - innerSpace
            )
        }
        batteryPaint?.apply {
            // 绘制电极
            style = Paint.Style.FILL
            canvas.drawRect(polarRect, this)
            // 绘制外框
            style = Paint.Style.STROKE
            strokeWidth = outBorder.toFloat()
            canvas.drawRect(outFrameRect, this)
            // 绘制内框
            val inWidth =
                (outFrameRect.width() - (innerSpace + outBorder) * 2) * pageLoader.getBattery() / 100
            inFrameRect.right = inFrameRect.left + inWidth
            style = Paint.Style.FILL
            canvas.drawRect(inFrameRect, this)
        }
    }

    private fun drawTime(isNext: Boolean) {
        // 绘制时间
        val bitmap = if (isNext) pageAnimation.nextBitmap else pageAnimation.curBitmap
        val canvas = Canvas(bitmap)
        if (timePaint == null) {
            timePaint = Paint()
            timePaint!!.apply {
                textSize = dp2px(timeTextSize.toInt()).toFloat()
                isAntiAlias = true
                isDither = true
            }
        }
        canvas.drawText(
            pageLoader.getTime(),
            (polarRect.right + dp2px(6)).toFloat(),
            (polarRect.top + dp2px(5)).toFloat(),
            timePaint!!
        )
    }

    private fun drawProcess(isNext: Boolean) {
        // 绘制进度
        val bitmap = if (isNext) pageAnimation.nextBitmap else pageAnimation.curBitmap
        val canvas = Canvas(bitmap)
        if (processPaint == null) {
            processPaint = Paint()
            processPaint!!.apply {
                textSize = dp2px(processTextSize.toInt()).toFloat()
                isAntiAlias = true
                isDither = true
            }
        }
        val progress =
            if (isNext) {
                if (isNextChapter) {
                    pageLoader.getProgress(nextPage.pageNum, nextPageList.size, true)
                } else {
                    pageLoader.getProgress(nextPage.pageNum, curPageList.size, false)
                }
            } else {
                pageLoader.getProgress(curPageNum, curPageList.size, false)
            }
        canvas.drawText(
            progress,
            (width - paddingRight - dp2px(27)).toFloat(),
            (polarRect.top + dp2px(4)).toFloat(), processPaint!!
        )
    }

    // 先判断当前章节内是否可以翻页
    fun hasNext(isNext: Boolean): Boolean {
        val nextPageNum = if (isNext) curPageNum + 1 else curPageNum - 1
        return if (nextPageNum in curPageList.indices) {
            isNextChapter = false
            nextPage = curPageList[nextPageNum]
            drawPage(true)
            logD(CLASS_TAG, "hasNext true cur page:${curPageNum}, next page:${nextPage.pageNum}")
            true
        } else {
            if (pageLoader.hasNextChapter(isNext) && pageLoader.getNextContent(isNext)
                    .isNotEmpty()
            ) {
                isNextChapter = true
                nextPageList = PageUtil.getPageList(
                    pageLoader.getNextTitle(isNext),
                    pageLoader.getNextContent(isNext),
                    titleTextSize,
                    textSize,
                    this
                )
                nextPage = if (isNext) nextPageList.first() else nextPageList.last()
                drawPage(true)
                logD(
                    CLASS_TAG,
                    "hasNext true cur page:${curPageNum}, next page:${nextPage.pageNum}"
                )
                true
            } else {
                isNextChapter = false
                logD(CLASS_TAG, "hasNext false")
                false
            }
        }
    }

    // 确认翻页时才会调用这个函数，交换当前页和下一页
    internal fun swapPage() {
        curPageNum = nextPage.pageNum
        if (isNextChapter) {
            nextPageList.apply {
                nextPageList = curPageList
                curPageList = this
            }
        }
        curPage.apply {
            curPage = nextPage
            nextPage = this
        }
    }

    internal fun turnPageStart() {
        logD(CLASS_TAG, "turnPageStart")
        listener?.onTurnPageStart()
    }

    internal fun turnPageCompleted() {
        logD(CLASS_TAG, "turnPageCompleted")
        listener?.onTurnPageCompleted()
        if (isNextChapter)
            listener?.onTurnChapterCompleted()
    }

    internal fun turnPageCanceled() {
        logD(CLASS_TAG, "turnPageCanceled")
        listener?.onTurnPageCanceled()
    }

    interface TouchListener {
        // 返回值表示是否拦截动画
        fun onTouchUp()
        fun onCenter()
    }

    interface PageViewListener {
        fun onTurnPageStart()
        fun onTurnPageCompleted()
        fun onTurnPageCanceled()
        fun onTurnChapterCompleted()
    }

}