package com.gstormdev.donutchart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class DonutChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val minimumSize = 100
    private var oval = RectF()
    private var percentage: Float
    private var anglePercent: Float = 0f

    var text: String? = null
        set(value) {
            field = value
            invalidate()
        }

    var textSize: Float = 0f
        set(value) {
            field = value
            textPaint.textSize = value
            invalidate()
        }

    var textColor: Int = 0
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    var ringColor: Int = 0
        set(value) {
            field = value
            backgroundArcPaint.color = value
            invalidate()
        }

    var progressColor: Int = 0
        set(value) {
            field = value
            foregroundArcPaint.color = value
            invalidate()
        }

    private val backgroundArcPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val foregroundArcPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
    }

    init {
        val defaultTextSize = resources.displayMetrics.scaledDensity * 24  // 24sp
        val defaultTextColor = Color.BLACK
        val defaultRingColor = Color.GRAY
        val defaultProgressColor = Color.CYAN
        context.theme.obtainStyledAttributes(attrs, R.styleable.DonutChart, 0, 0)
                .apply {
                    try {
                        percentage = getFloat(R.styleable.DonutChart_percentage, 0f)
                        text = getString(R.styleable.DonutChart_text)
                        textSize = getDimension(R.styleable.DonutChart_textSize, defaultTextSize)
                        textColor = getColor(R.styleable.DonutChart_textColor, defaultTextColor)
                        ringColor = getColor(R.styleable.DonutChart_ringColor, defaultRingColor)
                        progressColor = getColor(R.styleable.DonutChart_progressColor, defaultProgressColor)
                    } finally {
                        recycle()
                    }
                }
    }

    /**
     * @param percentage The percentage of the donut to be filled in.  Range is from 0.0 to 1.0
     */
    fun setPercentage(percentage: Float) {
        // cap percentage between 0 and 100 in case of bad input
        val sanitizedPercentage = max(min(percentage, 1f), 0f)
        // only invalidate if the value has actually changed
        if (sanitizedPercentage != this.percentage) {
            val oldPercentage = this.percentage
            this.percentage = sanitizedPercentage
            animatePercentage(oldPercentage, this.percentage)
            invalidate()
        }
    }

    private fun animatePercentage(from: Float, to: Float) {
        val animator = ValueAnimator.ofFloat(from, to).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 250
            addUpdateListener {
                anglePercent = it.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }

    fun setTextSizeRes(@DimenRes size: Int) {
        this.textSize = resources.getDimension(size)
    }

    fun setTextColorRes(@ColorRes color: Int) {
        this.textColor = ContextCompat.getColor(context, color)
    }

    fun setRingColorRes(@ColorRes color: Int) {
        this.ringColor = ContextCompat.getColor(context, color)
    }

    fun setProgressColorRes(@ColorRes color: Int) {
        this.progressColor = ContextCompat.getColor(context, color)
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
        val minw: Int = paddingLeft + paddingRight + max(suggestedMinimumWidth, minimumSize)
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)

        // Whatever the width ends up being, ask for a height that would let the donut
        // get as big as it can
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)

        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(oval, 120f, 300f, false, backgroundArcPaint)
        canvas.drawArc(oval, 120f, 300 * anglePercent, false, foregroundArcPaint)

        if (!TextUtils.isEmpty(text)) {
            val textHeight = textPaint.descent() + textPaint.ascent()
            val textBaseline = (height - textHeight) / 2f
            canvas.drawText(text!!, (width - textPaint.measureText(text)) / 2f, textBaseline, textPaint)
        }
    }
}