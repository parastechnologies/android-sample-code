package com.in2bliss.ui.activity.home.affirmation.recordAudio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.in2bliss.R

class AudioVisualizerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint: Paint? = null
    private val amplitudeList: ArrayList<Double> = arrayListOf()
    private var currentPosition = 0f
    private var maxAmplitude = 30741
    private val lineWidth = 8f
    private var viewMiddle: Int? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCanvas(canvas)
    }

    fun addAmplitude(
        amplitude: Long
    ) {
        val lineHeight = ((amplitude.toDouble() / maxAmplitude.toDouble()) * (height))
        amplitudeList.add(lineHeight)
        if ((amplitudeList.size * 2) * lineWidth >= width) {
            amplitudeList.removeAt(0)
        }
        invalidate()
    }

    private fun drawCanvas(canvas: Canvas?) {

        if (paint == null) {
            paint = Paint()
            paint?.color = ContextCompat.getColor(
                context,
                R.color.prime_purple_5F46F4
            )
            viewMiddle = height / 2
        }
        currentPosition = 0f

        amplitudeList.forEach { lineHeight ->

            paint?.let {
                canvas?.drawRect(
                    currentPosition.plus(lineWidth),
                    ((viewMiddle?.toDouble() ?: 0.0) - (lineHeight / 2)).toFloat(),
                    currentPosition.plus(lineWidth + lineWidth),
                    ((viewMiddle?.toDouble() ?: 0.0) + (lineHeight / 2)).toFloat(),
                    it
                )
            }
            currentPosition += (lineWidth + lineWidth)
        }
    }
}