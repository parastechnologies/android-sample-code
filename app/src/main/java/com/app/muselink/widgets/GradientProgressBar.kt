package com.app.muselink.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.app.muselink.R

@SuppressLint("Recycle")
class GradientProgressBar : View {
    var color: String = "#ffffff"
    var progress: Float = 0F
    private val COLOR_SKY = Color.parseColor("#4BE7E4")
    private val COLOR_BLUE = Color.parseColor("#2A43FF")
    private val COLOR_PINK = Color.parseColor("#E900F8")
    private val COLOR_RED = Color.parseColor("#F03527")
    private val COLOR_MUSTARD = Color.parseColor("#F09328")
    private val COLOR_YELLOW = Color.parseColor("#F8E100")
    private val COLOR_GREEN = Color.parseColor("#71E714")
    constructor(context: Context) : super(context) {
        initialize(context, null)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        initialize(context, attrs)
    }
    private fun initialize(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.GradientProgressBar)
            if (a.hasValue(R.styleable.GradientProgressBar_gpb_progress)) {
                color = a.getString(R.styleable.GradientProgressBar_gpb_progress)!!
            }
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLines(canvas)
    }
    //Todo draw line
    private fun drawLines(canvas: Canvas?): Canvas {
        //Todo Dark
        val paint = Paint()
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        val positions: FloatArray? = null
        val colors = intArrayOf(
            COLOR_SKY,
            COLOR_BLUE,
            COLOR_PINK,
            COLOR_RED,
            COLOR_MUSTARD,
            COLOR_YELLOW,
            COLOR_GREEN)
        paint.color = COLOR_BLUE
        val gradient: Shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), colors,positions, Shader.TileMode.REPEAT)
        paint.shader = gradient
        val ptLine = Path()
        val corners = floatArrayOf(0f, 0f,0f, 0f,0f, 0f,0f, 0f)
        val length = (width - 10).toFloat() * progress / 100
        ptLine.addRoundRect(RectF(0f, 0f, length, 10f), corners, Path.Direction.CW)
        canvas!!.drawPath(ptLine, paint)
        return canvas
    }
}