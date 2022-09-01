package com.yuenov.kotlin.open.widget.page.animation

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import com.yuenov.kotlin.open.ext.CLASS_TAG
import com.yuenov.kotlin.open.ext.logD
import com.yuenov.kotlin.open.widget.page.PageView
import java.lang.Exception
import kotlin.math.*

class SimulationPageAnimation : HorizontalPageAnimation() {
    private var cornerX = 1 // 拖拽点对应的页脚
    private var cornerY = 1
    private val path0: Path = Path()
    private val path1: Path = Path()
    private val bezierStart1 = PointF() // 贝塞尔曲线起始点
    private val bezierControl1 = PointF() // 贝塞尔曲线控制点
    private val bezierVertex1 = PointF() // 贝塞尔曲线顶点
    private var bezierEnd1 = PointF() // 贝塞尔曲线结束点
    private val bezierStart2 = PointF() // 另一条贝塞尔曲线
    private val bezierControl2 = PointF()
    private val bezierVertex2 = PointF()
    private var bezierEnd2 = PointF()
    private var middleX = 0f
    private var middleY = 0f
    private var degrees = 0f
    private var touchToCornerDis = 0f
    private val colorMatrixFilter: ColorMatrixColorFilter
    private val matrix: Matrix = Matrix()
    private val matrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f)
    private var isRTAndLB = false // 是否属于右上左下
    private var maxLength: Float = 0f
    private var backShadowDrawableLR: GradientDrawable? = null // 有阴影的GradientDrawable
    private var backShadowDrawableRL: GradientDrawable? = null
    private var folderShadowDrawableLR: GradientDrawable? = null
    private var folderShadowDrawableRL: GradientDrawable? = null
    private var frontShadowDrawableHBT: GradientDrawable? = null
    private var frontShadowDrawableHTB: GradientDrawable? = null
    private var frontShadowDrawableVLR: GradientDrawable? = null
    private var frontShadowDrawableVRL: GradientDrawable? = null
    private val paint: Paint = Paint()

    // 适配 android 高版本无法使用 XOR 的问题
    private val xorPath: Path = Path()

    override fun drawMove(canvas: Canvas) {
        when (direction) {
            Direction.NEXT -> {
                calcPoints()
                drawCurrentPageArea(canvas, curBitmap, path0)
                drawNextPageAreaAndShadow(canvas, nextBitmap)
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, curBitmap)
            }
            else -> {
                calcPoints()
                drawCurrentPageArea(canvas, nextBitmap, path0)
                drawNextPageAreaAndShadow(canvas, curBitmap)
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, nextBitmap)
            }
        }
    }

    override fun drawStatic(canvas: Canvas) {
        canvas.drawBitmap(curBitmap, 0f, 0f, null)
    }

    override fun startAnim() {
        logD(CLASS_TAG, "startAnim")
        super.startAnim()
        var dx: Int
        val dy: Int
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (isCancel) {
            dx = if (cornerX > 0 && direction == Direction.NEXT) {
                (screenWidth - touchX).toInt()
            } else {
                (-touchX).toInt()
            }
            if (direction != Direction.NEXT) {
                dx = (-(screenWidth + touchX)).toInt()
            }
            dy = if (cornerY > 0) {
                (screenHeight - touchY).toInt()
            } else {
                (-touchY).toInt() // 防止touchY最终变为0
            }
        } else {
            dx = if (cornerX > 0 && direction == Direction.NEXT) {
                -(screenWidth + touchX).toInt()
            } else {
                (screenWidth - touchX + screenWidth).toInt()
            }
            dy = if (cornerY > 0) {
                (screenHeight - touchY).toInt()
            } else {
                (1 - touchY).toInt() // 防止touchY最终变为0
            }
        }
        scroller?.startScroll(touchX.toInt(), touchY.toInt(), dx, dy, 400)
    }

    override fun setView(view: PageView) {
        super.setView(view)
        maxLength = hypot(screenWidth.toDouble(), screenHeight.toDouble()).toFloat()
    }

    override var direction: Direction = Direction.NONE
        set(value) {
            field = value
            when (value) {
                Direction.PRE ->
                    //上一页滑动不出现对角
                    if (startX > screenWidth / 2) {
                        calcCornerXY(startX, screenHeight.toFloat())
                    } else {
                        calcCornerXY(screenWidth - startX, screenHeight.toFloat())
                    }
                Direction.NEXT ->
                    if (screenWidth / 2 > startX) {
                        calcCornerXY(screenWidth - startX, startY)
                    }
                else -> {}
            }
        }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        calcCornerXY(x, y)
    }

    override fun setTouchPoint(x: Float, y: Float) {
        super.setTouchPoint(x, y)
        //触摸y中间位置吧y变成屏幕高度
        if (startY > screenHeight / 3 && startY < screenHeight * 2 / 3
            || direction == Direction.PRE
        ) {
            touchY = screenHeight.toFloat()
        }
        if (startY > screenHeight / 3 && startY < screenHeight / 2
            && direction == Direction.NEXT
        ) {
            touchY = 1F
        }
    }

    /**
     * 创建阴影的GradientDrawable
     */
    private fun createDrawable() {
        val color = intArrayOf(0x333333, -0x4fcccccd)
        folderShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, color
        )
        folderShadowDrawableRL!!.gradientType = GradientDrawable.LINEAR_GRADIENT
        folderShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, color
        )
        folderShadowDrawableLR!!.gradientType = GradientDrawable.LINEAR_GRADIENT

        // 背面颜色组
        val mBackShadowColors = intArrayOf(-0xeeeeef, 0x111111)
        backShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors
        )
        backShadowDrawableRL!!.gradientType = GradientDrawable.LINEAR_GRADIENT
        backShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        backShadowDrawableLR!!.gradientType = GradientDrawable.LINEAR_GRADIENT

        // 前面颜色组
        val mFrontShadowColors = intArrayOf(-0x7feeeeef, 0x111111)
        frontShadowDrawableVLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors
        )
        frontShadowDrawableVLR!!.gradientType = GradientDrawable.LINEAR_GRADIENT
        frontShadowDrawableVRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors
        )
        frontShadowDrawableVRL!!.gradientType = GradientDrawable.LINEAR_GRADIENT
        frontShadowDrawableHTB = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors
        )
        frontShadowDrawableHTB!!.gradientType = GradientDrawable.LINEAR_GRADIENT
        frontShadowDrawableHBT = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors
        )
        frontShadowDrawableHBT!!.gradientType = GradientDrawable.LINEAR_GRADIENT
    }

    /**
     * 是否能够拖动过去
     *
     * @return
     */
    fun canDragOver(): Boolean = touchToCornerDis > screenWidth / 10

    fun right(): Boolean = cornerX <= -4

    /**
     * 绘制翻起页背面
     *
     * @param canvas
     * @param bitmap
     */
    private fun drawCurrentBackArea(canvas: Canvas, bitmap: Bitmap) {
        val i = (bezierStart1.x + bezierControl1.x).toInt() / 2
        val f1 = abs(i - bezierControl1.x)
        val i1 = (bezierStart2.y + bezierControl2.y).toInt() / 2
        val f2 = abs(i1 - bezierControl2.y)
        val f3 = f1.coerceAtMost(f2)
        path1.reset()
        path1.moveTo(bezierVertex2.x, bezierVertex2.y)
        path1.lineTo(bezierVertex1.x, bezierVertex1.y)
        path1.lineTo(bezierEnd1.x, bezierEnd1.y)
        path1.lineTo(touchX, touchY)
        path1.lineTo(bezierEnd2.x, bezierEnd2.y)
        path1.close()
        val mFolderShadowDrawable: GradientDrawable?
        val left: Int
        val right: Int
        if (isRTAndLB) {
            left = (bezierStart1.x - 1).toInt()
            right = (bezierStart1.x + f3 + 1).toInt()
            mFolderShadowDrawable = folderShadowDrawableLR
        } else {
            left = (bezierStart1.x - f3 - 1).toInt()
            right = (bezierStart1.x + 1).toInt()
            mFolderShadowDrawable = folderShadowDrawableRL
        }
        canvas.save()
        try {
            canvas.clipPath(path0)
            canvas.clipPath(path1)
        } catch (e: Exception) {
        }
        paint.colorFilter = colorMatrixFilter
        //对Bitmap进行取色
        val color = bitmap.getPixel(1, 1)
        //获取对应的三色
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        //转换成含有透明度的颜色
        val tempColor = Color.argb(200, red, green, blue)
        val dis = hypot(
            cornerX - bezierControl1.x,
            bezierControl2.y - cornerY
        )
        val f8 = (cornerX - bezierControl1.x) / dis
        val f9 = (bezierControl2.y - cornerY) / dis
        matrixArray[0] = 1 - 2 * f9 * f9
        matrixArray[1] = 2 * f8 * f9
        matrixArray[3] = matrixArray[1]
        matrixArray[4] = 1 - 2 * f8 * f8
        matrix.reset()
        matrix.setValues(matrixArray)
        matrix.preTranslate(-bezierControl1.x, -bezierControl1.y)
        matrix.postTranslate(bezierControl1.x, bezierControl1.y)
        canvas.drawBitmap(bitmap, matrix, paint)
        //背景叠加
        canvas.drawColor(tempColor)
        paint.colorFilter = null
        canvas.rotate(degrees, bezierStart1.x, bezierStart1.y)
        mFolderShadowDrawable!!.setBounds(
            left,
            bezierStart1.y.toInt(),
            right,
            (bezierStart1.y + maxLength).toInt()
        )
        mFolderShadowDrawable.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘制翻起页的阴影
     *
     * @param canvas
     */
    private fun drawCurrentPageShadow(canvas: Canvas) {
        val degree: Double = if (isRTAndLB) {
            (Math.PI / 4 - atan2(bezierControl1.y - touchY, touchX - bezierControl1.x))
        } else {
            (Math.PI / 4 - atan2(touchY - bezierControl1.y, touchX - bezierControl1.x))
        }
        // 翻起页阴影顶点与touch点的距离
        val d1 = 25f * 1.414 * cos(degree)
        val d2 = 25f * 1.414 * sin(degree)
        val x = (touchX + d1).toFloat()
        val y: Float = if (isRTAndLB) {
            (touchY + d2).toFloat()
        } else {
            (touchY - d2).toFloat()
        }
        path1.reset()
        path1.moveTo(x, y)
        path1.lineTo(touchX, touchY)
        path1.lineTo(bezierControl1.x, bezierControl1.y)
        path1.lineTo(bezierStart1.x, bezierStart1.y)
        path1.close()
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                xorPath.reset()
                xorPath.moveTo(0f, 0f)
                xorPath.lineTo(canvas.width.toFloat(), 0f)
                xorPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                xorPath.lineTo(0f, canvas.height.toFloat())
                xorPath.close()

                // 取 path 的补集，作为 canvas 的交集
                xorPath.op(path0, Path.Op.XOR)
                canvas.clipPath(xorPath)
            } else {
                canvas.clipPath(path0, Region.Op.XOR)
            }
            canvas.clipPath(path1)
        } catch (e: Exception) {
        }
        var leftX: Int
        var rightX: Int
        var mCurrentPageShadow: GradientDrawable?
        if (isRTAndLB) {
            leftX = bezierControl1.x.toInt()
            rightX = bezierControl1.x.toInt() + 25
            mCurrentPageShadow = frontShadowDrawableVLR
        } else {
            leftX = (bezierControl1.x - 25).toInt()
            rightX = bezierControl1.x.toInt() + 1
            mCurrentPageShadow = frontShadowDrawableVRL
        }
        var rotateDegrees: Float = Math.toDegrees(
            atan2(touchX - bezierControl1.x, bezierControl1.y - touchY).toDouble()
        ).toFloat()
        canvas.rotate(rotateDegrees, bezierControl1.x, bezierControl1.y)
        mCurrentPageShadow!!.setBounds(
            leftX, (bezierControl1.y - maxLength).toInt(),
            rightX, bezierControl1.y.toInt()
        )
        mCurrentPageShadow.draw(canvas)
        canvas.restore()

        path1.reset()
        path1.moveTo(x, y)
        path1.lineTo(touchX, touchY)
        path1.lineTo(bezierControl2.x, bezierControl2.y)
        path1.lineTo(bezierStart2.x, bezierStart2.y)
        path1.close()
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                xorPath.reset()
                xorPath.moveTo(0f, 0f)
                xorPath.lineTo(canvas.width.toFloat(), 0f)
                xorPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                xorPath.lineTo(0f, canvas.height.toFloat())
                xorPath.close()

                // 取 path 的补给，作为 canvas 的交集
                xorPath.op(path0, Path.Op.XOR)
                canvas.clipPath(xorPath)
            } else {
                canvas.clipPath(path0, Region.Op.XOR)
            }
            canvas.clipPath(path1)
        } catch (e: Exception) {
        }
        if (isRTAndLB) {
            leftX = bezierControl2.y.toInt()
            rightX = (bezierControl2.y + 25).toInt()
            mCurrentPageShadow = frontShadowDrawableHTB
        } else {
            leftX = (bezierControl2.y - 25).toInt()
            rightX = (bezierControl2.y + 1).toInt()
            mCurrentPageShadow = frontShadowDrawableHBT
        }
        rotateDegrees = Math.toDegrees(
            atan2(bezierControl2.y - touchY, bezierControl2.x - touchX).toDouble()
        ).toFloat()
        canvas.rotate(rotateDegrees, bezierControl2.x, bezierControl2.y)
        val temp: Float =
            if (bezierControl2.y < 0) bezierControl2.y - screenHeight else bezierControl2.y
        val hmg = hypot(bezierControl2.x, temp).toInt()
        if (hmg > maxLength)
            mCurrentPageShadow!!.setBounds(
                (bezierControl2.x - 25).toInt() - hmg, leftX,
                (bezierControl2.x + maxLength).toInt() - hmg, rightX
            )
        else
            mCurrentPageShadow!!.setBounds(
                (bezierControl2.x - maxLength).toInt(), leftX,
                bezierControl2.x.toInt(), rightX
            )
        mCurrentPageShadow.draw(canvas)
        canvas.restore()
    }

    private fun drawNextPageAreaAndShadow(canvas: Canvas, bitmap: Bitmap) {
        path1.reset()
        path1.moveTo(bezierStart1.x, bezierStart1.y)
        path1.lineTo(bezierVertex1.x, bezierVertex1.y)
        path1.lineTo(bezierVertex2.x, bezierVertex2.y)
        path1.lineTo(bezierStart2.x, bezierStart2.y)
        path1.lineTo(cornerX.toFloat(), cornerY.toFloat())
        path1.close()
        degrees = Math.toDegrees(
            atan2((bezierControl1.x - cornerX).toDouble(), (bezierControl2.y - cornerY).toDouble())
        ).toFloat()
        val leftX: Int
        val rightX: Int
        val mBackShadowDrawable: GradientDrawable?
        if (isRTAndLB) {  //左下及右上
            leftX = bezierStart1.x.toInt()
            rightX = (bezierStart1.x + touchToCornerDis / 4).toInt()
            mBackShadowDrawable = backShadowDrawableLR
        } else {
            leftX = (bezierStart1.x - touchToCornerDis / 4).toInt()
            rightX = bezierStart1.x.toInt()
            mBackShadowDrawable = backShadowDrawableRL
        }
        canvas.save()
        try {
            canvas.clipPath(path0)
            canvas.clipPath(path1)
        } catch (e: Exception) {
        }
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.rotate(degrees, bezierStart1.x, bezierStart1.y)
        mBackShadowDrawable!!.setBounds(
            leftX,
            bezierStart1.y.toInt(),
            rightX,
            (maxLength + bezierStart1.y).toInt()
        ) //左上及右下角的xy坐标值,构成一个矩形
        mBackShadowDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawCurrentPageArea(canvas: Canvas, bitmap: Bitmap, path: Path) {
        path0.reset()
        path0.moveTo(bezierStart1.x, bezierStart1.y)
        path0.quadTo(
            bezierControl1.x, bezierControl1.y, bezierEnd1.x,
            bezierEnd1.y
        )
        path0.lineTo(touchX, touchY)
        path0.lineTo(bezierEnd2.x, bezierEnd2.y)
        path0.quadTo(
            bezierControl2.x, bezierControl2.y, bezierStart2.x,
            bezierStart2.y
        )
        path0.lineTo(cornerX.toFloat(), cornerY.toFloat())
        path0.close()
        canvas.save()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            xorPath.reset()
            xorPath.moveTo(0f, 0f)
            xorPath.lineTo(canvas.width.toFloat(), 0f)
            xorPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
            xorPath.lineTo(0f, canvas.height.toFloat())
            xorPath.close()

            // 取 path 的补给，作为 canvas 的交集
            xorPath.op(path, Path.Op.XOR)
            canvas.clipPath(xorPath)
        } else {
            canvas.clipPath(path, Region.Op.XOR)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        try {
            canvas.restore()
        } catch (e: Exception) {
        }
    }

    /**
     * 计算拖拽点对应的拖拽脚
     *
     * @param x
     * @param y
     */
    private fun calcCornerXY(x: Float, y: Float) {
        cornerX = if (x <= screenWidth / 2) 0 else screenWidth
        cornerY = if (y <= screenHeight / 2) 0 else screenHeight
        isRTAndLB = (cornerX == 0 && cornerY == screenHeight
                || cornerX == screenWidth && cornerY == 0)
    }

    private fun calcPoints() {
        middleX = (touchX + cornerX) / 2
        middleY = (touchY + cornerY) / 2
        bezierControl1.x = middleX - (cornerY - middleY) * (cornerY - middleY) / (cornerX - middleX)
        bezierControl1.y = cornerY.toFloat()
        bezierControl2.x = cornerX.toFloat()
        val f4 = cornerY - middleY
        if (f4 == 0f) {
            bezierControl2.y = middleY - (cornerX - middleX) * (cornerX - middleX) / 0.1f
        } else {
            bezierControl2.y =
                middleY - (cornerX - middleX) * (cornerX - middleX) / (cornerY - middleY)
        }
        bezierStart1.x = bezierControl1.x - (cornerX - bezierControl1.x) / 2
        bezierStart1.y = cornerY.toFloat()

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (touchX > 0 && touchX < screenWidth) {
            if (bezierStart1.x < 0 || bezierStart1.x > screenWidth) {
                if (bezierStart1.x < 0) bezierStart1.x = screenWidth - bezierStart1.x

                val f1: Float = abs(cornerX - touchX)
                val f2: Float = screenWidth * f1 / bezierStart1.x
                touchX = abs(cornerX - f2)

                val f3: Float = abs(cornerX - touchX) * abs(cornerY - touchY) / f1
                touchY = abs(cornerY - f3)

                middleX = (touchX + cornerX) / 2
                middleY = (touchY + cornerY) / 2

                bezierControl1.x =
                    middleX - (cornerY - middleY) * (cornerY - middleY) / (cornerX - middleX)
                bezierControl1.y = cornerY.toFloat()

                bezierControl2.x = cornerX.toFloat()

                val f5 = cornerY - middleY
                if (f5 == 0f) {
                    bezierControl2.y = middleY - (cornerX - middleX) * (cornerX - middleX) / 0.1f
                } else {
                    bezierControl2.y =
                        middleY - (cornerX - middleX) * (cornerX - middleX) / (cornerY - middleY)
                }
                bezierStart1.x = bezierControl1.x - (cornerX - bezierControl1.x) / 2
            }
        }
        bezierStart2.x = cornerX.toFloat()
        bezierStart2.y = bezierControl2.y - (cornerY - bezierControl2.y) / 2
        touchToCornerDis = hypot(touchX - cornerX, touchY - cornerY)
        bezierEnd1 = getCross(
            PointF(touchX, touchY), bezierControl1, bezierStart1, bezierStart2
        )
        bezierEnd2 = getCross(
            PointF(touchX, touchY), bezierControl2, bezierStart1, bezierStart2
        )
        bezierVertex1.x = (bezierStart1.x + 2 * bezierControl1.x + bezierEnd1.x) / 4
        bezierVertex1.y = (2 * bezierControl1.y + bezierStart1.y + bezierEnd1.y) / 4
        bezierVertex2.x = (bezierStart2.x + 2 * bezierControl2.x + bezierEnd2.x) / 4
        bezierVertex2.y = (2 * bezierControl2.y + bezierStart2.y + bezierEnd2.y) / 4
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
     *
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @return
     */
    private fun getCross(P1: PointF, P2: PointF, P3: PointF, P4: PointF): PointF {
        val crossP = PointF()
        // 二元函数通式： y=ax+b
        val a1 = (P2.y - P1.y) / (P2.x - P1.x)
        val b1 = (P1.x * P2.y - P2.x * P1.y) / (P1.x - P2.x)

        val a2 = (P4.y - P3.y) / (P4.x - P3.x)
        val b2 = (P3.x * P4.y - P4.x * P3.y) / (P3.x - P4.x)
        crossP.x = (b2 - b1) / (a1 - a2)
        crossP.y = a1 * crossP.x + b1
        return crossP
    }

    init {
        paint.style = Paint.Style.FILL
        createDrawable()
        val cm = ColorMatrix() //设置颜色数组
        val array = floatArrayOf(
            1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f
        )
        cm.set(array)
        colorMatrixFilter = ColorMatrixColorFilter(cm)
        touchX = 0.01f // 不让x,y为0,否则在点计算时会有问题
        touchY = 0.01f
    }
}