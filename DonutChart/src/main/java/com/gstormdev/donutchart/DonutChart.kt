package com.gstormdev.donutchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

// TODO need to add a text label to the center of the chart (to show the current value)
// TODO add text size/color options
// TODO add color options for rings
// TODO add animation for ring updates

class DonutChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var oval = RectF()
    private var percentage: Float

    private val backgroundArcPaint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val foregroundArcPaint = Paint().apply {
        isAntiAlias = true
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.DonutChart, 0, 0)
                .apply {
                    try {
                        percentage = getFloat(R.styleable.DonutChart_percentage, 0f)
                    } finally {
                        recycle()
                    }
                }
    }

    fun setPercentage(percentage: Float) {
        this.percentage = percentage
        invalidate()
        requestLayout()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Padding
        val xPadding: Float = (paddingStart + paddingEnd).toFloat()
        val yPadding: Float = (paddingTop + paddingBottom).toFloat()

        val donutWidth: Float = w - xPadding
        val donutHeight: Float = h - yPadding
        val diameter = min(donutWidth, donutHeight)
        val strokeWidth = diameter / 15

        oval.set(0f + strokeWidth, 0f + strokeWidth, diameter - strokeWidth, diameter - strokeWidth)

        backgroundArcPaint.strokeWidth = strokeWidth
        foregroundArcPaint.strokeWidth = strokeWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)

        // Whatever the width ends up being, ask for a height that would let the donut
        // get as big as it can
        val minh: Int = MeasureSpec.getSize(w) + paddingBottom + paddingTop
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)

        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(oval, 120f, 300f, false, backgroundArcPaint)
        val endpoint = min(300 * percentage, 300f)
        canvas.drawArc(oval, 120f, endpoint, false, foregroundArcPaint)
    }
}