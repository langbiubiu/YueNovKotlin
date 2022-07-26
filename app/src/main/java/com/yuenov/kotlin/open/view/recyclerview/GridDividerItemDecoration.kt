package com.yuenov.kotlin.open.view.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logd
import java.lang.Exception

/**
 * @Author:         hegaojian
 * @CreateDate:     2019/10/11 14:29
 * @param context 上下文
 * @param dividerWidth 您所需指定的间隔宽度，主要为第一列和最后一列与父控件的间隔；行间距，列间距将动态分配
 * @param dividerHeight 行间距
 * @param firstRowTopMargin 第一行顶部是否需要间隔
 * @param isNeedSpace 第一列和最后一列是否需要指定间隔(默认不指定)
 * @param isLastRowNeedSpace 最后一行是否需要间隔(默认不需要)
 *
 * 不要设置RecyclerView的paddingLeft和paddingRight，与父控件的左右间距需要通过dividerWidth来设置，且isNeedSpace设true
 */
class GridDividerItemDecoration(
    private val context: Context?,
    private val dividerWidth: Int,
    private val dividerHeight: Int,
    private val firstRowTopMargin: Int = 0,
    private val isNeedSpace: Boolean = false,
    private val isLastRowNeedSpace: Boolean = false,
    @ColorInt color: Int = Color.WHITE
) : ItemDecoration() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var spanCount = 0

    constructor(context: Context?, dividerWidth: Int, dividerHeight: Int, isNeedSpace: Boolean) : this(
        context,
        dividerWidth,
        dividerHeight,
        0,
        isNeedSpace,
        false
    ) {}

    init {
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        var top = 0
        var left = 0
        var right = 0
        var bottom = 0
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        spanCount = getSpanCount(parent)
        val childCount = parent.adapter!!.itemCount
        val maxAllDividerWidth = getMaxDividerWidth(view) //

        var spaceWidth = 0 //首尾两列与父布局之间的间隔
        if (isNeedSpace) spaceWidth = dividerWidth

        val eachItemWidth = maxAllDividerWidth / spanCount //每个Item left+right
        val dividerItemWidth =
            (maxAllDividerWidth - 2 * spaceWidth) / (spanCount - 1) //item与item之间的距离

        left = itemPosition % spanCount * (dividerItemWidth - eachItemWidth) + spaceWidth
        right = eachItemWidth - left
//        bottom = dividerWidth
        bottom = dividerHeight
        if (firstRowTopMargin > 0 && isFirstRow(parent, itemPosition, spanCount, childCount))
            //第一行顶部是否需要间隔
            top = firstRowTopMargin
        if (!isLastRowNeedSpace && isLastRow(parent, itemPosition, spanCount, childCount)) {
            //最后一行是否需要间隔
            bottom = 0
        }
        outRect[left, top, right] = bottom
//        logd(CLASS_TAG, "$itemPosition $outRect")
    }

    /**
     * 获取Item View的大小，若无则自动分配空间
     * 并根据 屏幕宽度-View的宽度*spanCount 得到屏幕剩余空间
     *
     * @param view
     * @return
     */
    private fun getMaxDividerWidth(view: View): Int {
        val itemWidth = view.layoutParams.width
        //没有指明Item高度时，指MATCH_PARENT和WRAP_CONTENT时，
//        val itemHeight = view.layoutParams.height
        val screenWidth =
            context!!.resources.displayMetrics.widthPixels.coerceAtMost(context.resources.displayMetrics.heightPixels)
        var maxDividerWidth = screenWidth - itemWidth * spanCount
        if (itemWidth < 0 || isNeedSpace && maxDividerWidth <= (spanCount - 1) * dividerWidth) {
            view.layoutParams.width = getAttachColumnWidth()
//            view.layoutParams.height = getAttachColumnWidth()
            maxDividerWidth = screenWidth - view.layoutParams.width * spanCount
        }
        return maxDividerWidth
    }

    /**
     * 根据屏幕宽度和item数量分配 item View的width和height
     *
     * @return
     */
    private fun getAttachColumnWidth(): Int {
        var itemWidth = 0
        var spaceWidth = 0
        try {
            val width =
                context!!.resources.displayMetrics.widthPixels.coerceAtMost(context.resources.displayMetrics.heightPixels)
            if (isNeedSpace) spaceWidth = 2 * dividerWidth
            itemWidth = (width - spaceWidth) / spanCount - 40
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return itemWidth
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        draw(c, parent)
    }

    //绘制item分割线
    private fun draw(canvas: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            //画水平分隔线
            var left = child.left
            var right = child.right
            var top = child.bottom + layoutParams.bottomMargin
            var bottom = top + dividerWidth
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                mPaint
            )
            //画垂直分割线
            top = child.top
            bottom = child.bottom + dividerWidth
            left = child.right + layoutParams.rightMargin
            right = left + dividerWidth
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                mPaint
            )
        }
    }

    /**
     * 判读是否是第一列
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isFirstColumn(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            if (pos % spanCount == 0) {
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager
                .orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if (pos % spanCount == 0) { // 第一列
                    return true
                }
            } else {
            }
        }
        return false
    }

    /**
     * 判断是否是最后一列
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isLastColumn(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) { // 如果是最后一列，则不需要绘制右边
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager
                .orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) { // 最后一列
                    return true
                }
            } else {
            }
        }
        return false
    }

    /**
     * 判读是否是最后一行
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isLastRow(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val lines =
                if (childCount % spanCount == 0) childCount / spanCount else childCount / spanCount + 1
            return lines == pos / spanCount + 1
        } else if (layoutManager is StaggeredGridLayoutManager) {
        }
        return false
    }

    /**
     * 判断是否是第一行
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isFirstRow(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            return pos / spanCount == 0
        } else if (layoutManager is StaggeredGridLayoutManager) {
        }
        return false
    }

    /**
     * 获取列数
     *
     * @param parent
     * @return
     */
    private fun getSpanCount(parent: RecyclerView): Int {
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }
}