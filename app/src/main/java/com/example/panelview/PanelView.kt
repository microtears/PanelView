package com.example.panelview

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.sin


@Suppress("MemberVisibilityCanBePrivate")
class PanelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var radius: Float = 100f
        set(value) {
            field = value
            invalidate()
        }

    var layer1Color: Int = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }
    var layer2Color: Int = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }
    var layer3Color: Int = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }
    var layer3StokeColor: Int = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }
    var layer3StokeWidth: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    var textSize: Float = 14.dp
        set(value) {
            field = value
            textPaint.textSize = value
            initTextBounds()
            invalidate()
        }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        this.color = this@PanelView.textColor
        this.textSize = this@PanelView.textSize

    }

    private val paint = Paint()

    var data: List<Item> = emptyList()
        set(value) {
            field = value
            initTextBounds()
            requestLayout()
        }
    private val layer1Path: Path = Path()
    private val layer2Path: Path = Path()
    private val layer3Path: Path = Path()
    private val layer3StrokePath: Path = Path()
    private var textBounds: List<Rect> = emptyList()

    private val dash = DashPathEffect(floatArrayOf(4.dp, 6.dp), 0f)


    private val centerHeight: Int
        get() = measuredHeight / 2
    private val centerWidth: Int
        get() = measuredHeight / 2


    private fun initTextBounds() {
        textBounds = List(data.size) {
            val rect = Rect()
            val text = data[it].text
            textPaint.getTextBounds(text, 0, text.length, rect)
            rect
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        computePaths()
    }

    private fun computePaths() {
        val num = data.size
        val rad0 = Math.PI * 2 / num

        // layer1
        layer1Path.reset()
        repeat(num) { i ->
            val realRadius = radius
            val rad = rad0 * (num - i) - Math.PI
            val x = centerWidth.toFloat() + (realRadius * sin(rad)).toFloat()
            val y = centerHeight.toFloat() + (realRadius * cos(rad)).toFloat()
            if (i == 0) {
                layer1Path.moveTo(x, y)
            } else {
                layer1Path.lineTo(x, y)
            }
        }
        layer1Path.close()

        // layer2
        layer2Path.reset()
        repeat(num) { i ->
            val realRadius = radius * 3 / 4
            val rad = rad0 * (num - i) - Math.PI
            val x = centerWidth.toFloat() + (realRadius * sin(rad)).toFloat()
            val y = centerHeight.toFloat() + (realRadius * cos(rad)).toFloat()
            if (i == 0) {
                layer2Path.moveTo(x, y)
            } else {
                layer2Path.lineTo(x, y)
            }
        }
        layer2Path.close()

        // layer3
        layer3Path.reset()
        layer3StrokePath.reset()
        repeat(num) { i ->
            val item = data[i]
            val realRadius = radius * item.ratio
            val strokeRadius = radius * item.ratio - layer3StokeWidth / 2
            val rad = rad0 * (num - i) - Math.PI
            val x = centerWidth.toFloat() + (realRadius * sin(rad)).toFloat()
            val y = centerHeight.toFloat() + (realRadius * cos(rad)).toFloat()
            val x1 = centerWidth.toFloat() + (strokeRadius * sin(rad)).toFloat()
            val y1 = centerHeight.toFloat() + (strokeRadius * cos(rad)).toFloat()
            if (i == 0) {
                layer3Path.moveTo(x, y)
                layer3StrokePath.moveTo(x1, y1)
            } else {
                layer3Path.lineTo(x, y)
                layer3StrokePath.lineTo(x1, y1)
            }
        }
        layer3Path.close()
        layer3StrokePath.close()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLayer12(canvas)
        drawRadius(canvas)
        drawLayer3(canvas)
        drawTexts(canvas)
    }

    private fun drawLayer12(canvas: Canvas) {
        paint.apply {
            reset()
            color = layer1Color
        }
        canvas.drawPath(layer1Path, paint)
        paint.apply {
            reset()
            color = layer2Color
        }
        canvas.drawPath(layer2Path, paint)
    }

    private fun drawLayer3(canvas: Canvas) {
        paint.apply {
            reset()
            color = layer3Color
        }
        canvas.drawPath(layer3Path, paint)
        paint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeWidth = layer3StokeWidth
            color = layer3StokeColor
        }
        canvas.drawPath(layer3StrokePath, paint)
    }

    private fun drawRadius(canvas: Canvas) {
        val num = data.size
        val rad0 = Math.PI * 2 / num
        repeat(num) { i ->
            val realRadius = radius
            val rad = rad0 * (num - i) - Math.PI
            val x = centerWidth.toFloat() + (realRadius * sin(rad)).toFloat()
            val y = centerHeight.toFloat() + (realRadius * cos(rad)).toFloat()
            paint.apply {
                reset()
                color = Color.WHITE
                strokeWidth = 1.dp
                pathEffect = dash
            }
            canvas.drawLine(
                centerWidth.toFloat(),
                centerHeight.toFloat(),
                x,
                y,
                paint
            )
        }
    }

    private fun drawTexts(canvas: Canvas) {
        val num = data.size
        val rad0 = Math.PI * 2 / num
        repeat(num) { i ->
            val text = data[i].text
            val textBound = textBounds[i]
            val realRadius = radius + textBound.height()
            val rad = rad0 * (num - i) - Math.PI
            val x = centerWidth.toFloat() + (realRadius * sin(rad)).toFloat()
            val y = centerHeight.toFloat() + (realRadius * cos(rad)).toFloat()
            val deg = (Math.toDegrees(rad - Math.PI / 2).toInt() + 360) % 360
            if ((deg in 0..45) || deg in 315..360) {
                textPaint.textAlign = Paint.Align.LEFT
            } else if (deg in 135..225) {
                textPaint.textAlign = Paint.Align.RIGHT
            } else {
                textPaint.textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                text,
                x,
                y + textBound.height() / 2 - textBound.bottom,
                textPaint
            )
        }
        if (DEBUG) {
            val x = centerWidth.toFloat()
            val y = centerHeight.toFloat()
            val center = "Center"
            val textBound = Rect()
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.getTextBounds(center, 0, center.length, textBound)
            canvas.drawText(
                center,
                x,
                y + textBound.height() / 2 - textBound.bottom,
                textPaint
            )
        }
    }

    data class Item(val text: String, val maxValue: Double, val value: Double) {
        val ratio: Double
            get() = value / maxValue
    }

    private fun View.dp2px(value: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        )
    }

    private val Int.dp: Float
        get() = dp2px(this)

    private val DEBUG = false

}