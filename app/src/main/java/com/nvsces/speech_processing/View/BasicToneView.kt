package com.nvsces.speech_processing.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.nvsces.mynative.fftResolution
import com.nvsces.mynative.frameSizeEdit
import com.nvsces.mynative.samplingRate
import com.nvsces.speech_processing.utils.maxValue
import com.nvsces.speech_processing.utils.searchMax
import com.nvsces.speech_processing.utils.searchMaxDefIndex
import kotlin.math.floor

class BasicToneView : View {

    private var paint = Paint()
    private var paintGrid=Paint()
    private var realTimeData = ArrayList<Float>()
    private var frequencyArray= ArrayList<Float>()
    private var DRAW=false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setWaveFrequency(wList: ArrayList<Float>) {
        DRAW=true
        frequencyArray=wList
    }

    fun setRealTimeWave(input: ArrayList<Float>) {
        realTimeData.clear()
        realTimeData.addAll(input)
    }

    private fun drawEtalon(canvas: Canvas, width: Int, height: Int, a: Float = 1f) {
        paint.color = Color.BLUE
        var x1: Float = 0f
        var y1: Float = (height * frequencyArray[0]) *a

        for (i in 1 until frequencyArray.size) {
            val y2 = (height * frequencyArray[i])*a
            val x2 = width * i / frequencyArray.size.toFloat()
            canvas.drawLine(x1, height - y1, x2, height - y2, paint)
            x1 = x2
            y1 = y2
        }
    }

    private fun drawRealTime(canvas: Canvas, width: Int, height: Int, a: Float = 1f) {

        paint.color = Color.RED
        var rtx1: Float = 0f
        var rty1: Float = (height * realTimeData[0]) * a
        for (p in 1 until realTimeData.size) {
            val rty2 = (height * realTimeData[p]) * a
            val rtx2 = width * p / frequencyArray.size.toFloat()
            canvas.drawLine(rtx1, height - rty1, rtx2, height - rty2, paint)
            rtx1 = rtx2
            rty1 = rty2
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height

        val wColor = 10
        val wFrequency = 70
        val rWidth = width - wColor - wFrequency
        val frameSize = floor(frameSizeEdit * samplingRate / fftResolution).toInt()

        paint.color = Color.BLUE
        paint.strokeWidth= 4F

        while (realTimeData.size>frequencyArray.size){
            realTimeData.removeAt(realTimeData.size-1)
        }

        val maxHrealtime = maxValue(realTimeData) +50
        val maxHetalon =  maxValue(frequencyArray) +50
        var hDraw=1f
        hDraw = if (maxHetalon>maxHrealtime) 1/maxHetalon
        else 1/maxHrealtime

        hDraw= (1/500f)//удалить эту строку если пределы меняются

        if (realTimeData.size > 0) {
            val maxRealTime = searchMax(realTimeData)
            if (realTimeData.size > frameSize) {
                val startIndex = realTimeData.size - frameSize
                val maxHrealtimeIn = searchMaxDefIndex(realTimeData, startIndex)+50
                val maxHetalonIn =  searchMaxDefIndex(frequencyArray, startIndex)+50

                var hScaleHeight=1f
                hScaleHeight = if (maxHrealtimeIn>maxHetalonIn) 1/maxHrealtimeIn
                else 1/maxHetalonIn
                drawGrid(canvas)
                drawText(canvas,height,hScaleHeight)

                hScaleHeight= (1/500f)//ищменить удалить эту строку если пределы меняются
                darwRealTimeMovement(canvas, width, height, hScaleHeight, startIndex)
                darwEtalonMovement(canvas, width, height, hScaleHeight, startIndex)
            } else {
                drawRealTime(canvas, width, height, hDraw)
                drawEtalon(canvas, width, height, hDraw)
                drawGrid(canvas)
                drawText(canvas,height,hDraw)
            }
        } else {
            if (DRAW) {
                drawText(canvas,height,hDraw)
                drawEtalon(canvas, width, height, hDraw)
                drawGrid(canvas)
            }
        }
    }

    private fun drawText(canvas: Canvas,height: Int,scaleHeight:Float) {
        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 30F
        //defuneLimits(scaleHeight)
        val step=height/10

        for (i in 0 until 10) {
            canvas.drawText(
                "" + 50*i + " Гц",
                (width - 90).toFloat(),
                height.toFloat() - i*step-10,
                textPaint
            )
        }
    }

    private fun defuneLimits(scaleHeight: Float) {
        val maxValue = 1/scaleHeight.toInt()
        val arrayValue= arrayOf(0,50,100,150,200,250,300,350,400,450,500)
    }

    private fun drawGrid(canvas: Canvas) {
        val paintStroke = Paint()
        paintStroke.color=Color.DKGRAY
        paintStroke.style = Paint.Style.STROKE
        val countXGrid=10
        for (i in 0 until countXGrid) {
            for (j in 0 until 10) {
                val myRect = Rect()
                val aLeft: Int = i * ((width / countXGrid).toInt())
                val aTop: Int = height - (10 - (j))*height*1/10
                val aRight: Int = ((i + 1) * (width / countXGrid).toInt())
                val aBottom: Int = height - (10 - (j + 1))*height*1/10
                myRect.set(aLeft, aTop, aRight, aBottom)
                canvas.drawRect(myRect, paintStroke)
            }
        }
    }

    private fun darwRealTimeMovement(
        canvas: Canvas,
        width: Int,
        height: Int,
        h: Float,
        startIndex: Int,
    ) {
        var rtx1: Float = 0f
        var rty1: Float = (height * realTimeData[startIndex]) * h
        paint.color = Color.RED
        val widthSize = realTimeData.size - startIndex


        for (p in startIndex until realTimeData.size) {
            val rty2 = (height * realTimeData[p]) * h
            val rtx2 = width * (p - startIndex) / widthSize.toFloat()
            canvas.drawLine(rtx1, height - rty1, rtx2, height - rty2, paint)
            rtx1 = rtx2
            rty1 = rty2
        }
    }

    private fun darwEtalonMovement(
        canvas: Canvas,
        width: Int,
        height: Int,
        hScaleHeight: Float,
        startIndex: Int,
    ) {
        paint.color = Color.BLUE
        var x1: Float = 0f
        val widthSize = realTimeData.size - startIndex

        var y1: Float = (height * frequencyArray[0]) * hScaleHeight


        for (i in startIndex until realTimeData.size) {
            val y2 = (height * frequencyArray[i]) * hScaleHeight
            val x2 = width * (i - startIndex) / widthSize.toFloat()
            canvas.drawLine(x1, height - y1, x2, height - y2, paint)
            x1 = x2
            y1 = y2
        }
    }

}