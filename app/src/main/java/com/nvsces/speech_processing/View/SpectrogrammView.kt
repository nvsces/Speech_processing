package com.nvsces.speech_processing.View

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.nvsces.mynative.APP_ACTIVITY
import com.nvsces.speech_processing.R
import kotlin.math.abs

class SpectrogrammView : View {

    // Attributes
    private val paint = Paint()
    private lateinit var bitmap: Bitmap
    private lateinit var  canvas: Canvas
    private var pos = 0
    private var samplingRate = 0
    private var mwidth:Int = 0
    private var mheight:Int = 0


    private lateinit var magnitudes: FloatArray
    private lateinit var magnitudesMax: FloatArray
    private val colorRainbow =
            intArrayOf(-0x1, -0xff01, -0x10000, -0x100, -0xff0100, -0xff0001, -0xffff01, -0x1000000)
    private val colorFire = intArrayOf(-0x1, -0x100, -0x10000, -0x1000000)
    private val colorIce = intArrayOf(-0x1, -0xff0001, -0xffff01, -0x1000000)
    private val colorGrey = intArrayOf(-0x1, -0x1000000)


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mwidth = w
        mheight = h
        bitmap = Bitmap.createBitmap(mwidth, mheight, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var colors: IntArray? = null

        colors = colorRainbow

        samplingRate=16000

        val wColor = 10
        val wFrequency = 40
        val rWidth = width - wColor - wFrequency
        paint.strokeWidth = 1f

        // Get scale preferences

        // Get scale preferences
        //val defFrequency = APP_ACTIVITY.getString(R.string.preferences_frequency_scale_default_value)
        val logFrequency = false

        // Update buffer bitmap

        // Update buffer bitmap
        paint.color = Color.BLACK
        this.canvas.drawLine(pos % rWidth.toFloat(),
                0f,
                pos % rWidth.toFloat(),
                height.toFloat(),
                paint)

        // находим максимальный элемент в массиве
        // основной тон частотной характеристики

        magnitudesMax=FloatArray(magnitudes.size / 2 + 1)
        System.arraycopy(magnitudes, 0, magnitudesMax, 0, magnitudes.size / 8)
        //val maxIdx = magnitudesMax.indexOf(magnitudesMax.max()!!)

        for (i in 0 until height) {
            var posit=(height - i)
            var j = getValueFromRelativePosition((height - i).toFloat() / height,
                    1f,
                    samplingRate / 8.toFloat(),
                    false)
            j /= samplingRate / 8
            val mag = magnitudes[(j * magnitudes.size / 8).toInt()]

            val par=j * magnitudes.size / 8

            val maxIdx = magnitudes.indices.maxBy { magnitudes[it] } ?: -1

            val db = Math.max(0.0, -20 * Math.log10(mag.toDouble())).toFloat()
            val c = getInterpolatedColor(colors, db * 0.009f)
            //
            val x = (pos % rWidth).toFloat()
            val y = i.toFloat()
            this.canvas.drawPoint(x, y, paint)
            this.canvas.drawPoint(x, y, paint) // make color brighte
        }

        // Draw bitmap

        // Draw bitmap
        if (pos < rWidth) {
            canvas.drawBitmap(bitmap, wColor.toFloat(), 0f, paint)
        } else {
            canvas.drawBitmap(bitmap, wColor.toFloat() - pos % rWidth, 0f, paint)
            canvas.drawBitmap(bitmap, wColor.toFloat() + (rWidth - pos % rWidth), 0f, paint)
        }

        // Draw color scale

        // Draw color scale
        paint.color = Color.BLACK
        canvas.drawRect(0f, 0f, wColor.toFloat(), height.toFloat(), paint);
        // canvas.drawRect(0, 0, wColor, height, paint);
        for (i in 0 until height) {
            val c = getInterpolatedColor(colors, i.toFloat() / height)
            paint.color = c
            canvas.drawLine(0f, i.toFloat(), wColor - 5.toFloat(), i.toFloat(), paint)
        }

        // Draw frequency scale

        // Draw frequency scale
        val ratio = 0.7f * resources.displayMetrics.density
        paint.textSize = 12f * ratio
        paint.color = Color.BLACK
        canvas.drawRect(rWidth + wColor.toFloat(), 0f, width.toFloat(), height.toFloat(), paint)
        paint.color = Color.WHITE
        canvas.drawText("kHz", rWidth + wColor.toFloat(), 12 * ratio, paint)

            var i = 0
            while (i < (samplingRate - 500) / 8) {
                canvas.drawText(" " + i / 1000,
                        rWidth + wColor.toFloat(),
                        height * (1f - i.toFloat() / (samplingRate / 8)),
                        paint)
                i += 1000
            }

        pos = pos + 1
    }

    fun setFFTResolution(res: Int) { magnitudes = FloatArray(res) }

    fun setSamplingRate(sampling: Int) { samplingRate = sampling }

    fun setMagnitudes(m: FloatArray) { System.arraycopy(m, 0, magnitudes, 0, m.size) }

    /**
     * Converts relative position of a value within given boundaries
     * Log=true for logarithmic scale
     */
    private fun getRelativePosition(
            value: Float,
            minValue: Float,
            maxValue: Float,
            log: Boolean,
    ): Float {
        return if (log) Math.log10(1 + value - minValue.toDouble())
                .toFloat() / Math.log10(1 + maxValue - minValue.toDouble())
                .toFloat() else (value - minValue) / (maxValue - minValue)
    }

    /**
     * Returns a value from its relative position within given boundaries
     * Log=true for logarithmic scale
     */
    private fun getValueFromRelativePosition(
            position: Float,
            minValue: Float,
            maxValue: Float,
            log: Boolean,
    ): Float {
        return if (log) (Math.pow(10.0, position * Math.log10(1 + maxValue - minValue.toDouble())) + minValue - 1).toFloat()
        else minValue + position * (maxValue - minValue)
    }

    /**
     * Calculate rainbow colors
     */
    private fun ave(s: Int, d: Int, p: Float): Int {
        return s + Math.round(p * (d - s))
    }

    fun getInterpolatedColor(colors: IntArray, unit: Float): Int {
        if (unit <= 0) return colors[0]
        if (unit >= 1) return colors[colors.size - 1]
        var p = unit * (colors.size - 1)
        val i = p.toInt()
        p -= i.toFloat()

        // now p is just the fractional part [0...1) and i is the index
        val c0 = colors[i]
        val c1 = colors[i + 1]
        val a = ave(Color.alpha(c0), Color.alpha(c1), p)
        val r = ave(Color.red(c0), Color.red(c1), p)
        val g = ave(Color.green(c0), Color.green(c1), p)
        val b = ave(Color.blue(c0), Color.blue(c1), p)
        return Color.argb(a, r, g, b)
    }

}