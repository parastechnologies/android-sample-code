package com.masoudss.lib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.masoudss.lib.exception.SampleDataException
import com.masoudss.lib.utils.ThreadBlocking
import com.masoudss.lib.utils.Utils
import com.masoudss.lib.utils.WaveGravity
import com.masoudss.lib.utils.WaveformOptions
import java.io.File
import kotlin.math.abs

class WaveformSeekBar : View {

    private var mCanvasWidth = 0
    private var mCanvasHeight = 0

    private val mWavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mWaveRect = RectF()
    private val mWaveRectProgress = RectF()
    private val mWaveRectSelection = RectF()
    private val mWaveRectSelectedWaves = RectF()
    private val mProgressCanvas = Canvas()
    private var mMaxValue = Utils.dp(context, 2).toInt()
    private var mTouchDownX = 0F
    private var mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.WaveformSeekBar)

        waveWidth = ta.getDimension(R.styleable.WaveformSeekBar_wave_width, waveWidth)
        waveGap = ta.getDimension(R.styleable.WaveformSeekBar_wave_gap, waveGap)
        waveCornerRadius =
            ta.getDimension(R.styleable.WaveformSeekBar_wave_corner_radius, waveCornerRadius)
        waveMinHeight = ta.getDimension(R.styleable.WaveformSeekBar_wave_min_height, waveMinHeight)
        waveBackgroundColor =
            ta.getColor(R.styleable.WaveformSeekBar_wave_background_color, waveBackgroundColor)
        waveProgressColor =
            ta.getColor(R.styleable.WaveformSeekBar_wave_progress_color, waveProgressColor)
        progress = ta.getInteger(R.styleable.WaveformSeekBar_wave_progress, progress)
        maxProgress = ta.getFloat(R.styleable.WaveformSeekBar_wave_max_progress, maxProgress)
        val gravity = ta.getString(R.styleable.WaveformSeekBar_wave_gravity)
        waveGravity = when (gravity) {
            "1" -> WaveGravity.TOP
            "2" -> WaveGravity.CENTER
            else -> WaveGravity.BOTTOM
        }

        ta.recycle()
        WaveformOptions.init(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasWidth = w
        mCanvasHeight = h
    }

    private var mBorderLinePaint: Paint? = null
    private var mBorderLinePaintPink: Paint? = null

    private var mOffset = 0
    private var mSelectionStart = 0
    private var mSelectionEnd = 0

    fun setParameters(start: Int, end: Int, offset: Int) {
        mSelectionStart = start
        mSelectionEnd = end
        mOffset = offset
    }

    var startWave = 0F
    var endWave = 0F

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        mBorderLinePaint = Paint()
        mBorderLinePaint?.setAntiAlias(true)
        mBorderLinePaint?.setStrokeWidth(6f)
        mBorderLinePaint?.setPathEffect(
            DashPathEffect(
                floatArrayOf(3.0f, 2.0f),
                0.0f
            )
        )
        mBorderLinePaint?.setColor(Color.parseColor("#D1756D"))


        mBorderLinePaintPink = Paint()
        mBorderLinePaintPink?.setAntiAlias(true)
        mBorderLinePaintPink?.setStrokeWidth(6f)
        mBorderLinePaintPink?.setPathEffect(
            DashPathEffect(
                floatArrayOf(3.0f, 2.0f),
                0.0f
            )
        )
        mBorderLinePaintPink?.setColor(Color.parseColor("#F747D0"))


        super.onDraw(canvas)
        if (sample == null || sample!!.isEmpty())
            throw SampleDataException()

        mMaxValue = sample!!.maxOrNull()!!
        val step = (getAvailableWith() / (waveGap + waveWidth)) / sample!!.size

        var i = 0F
        var lastWaveRight = paddingLeft.toFloat()
        while (i < sample!!.size) {

            var waveHeight = getAvailableHeight() * (sample!![i.toInt()].toFloat() / mMaxValue)

            if (waveHeight < waveMinHeight)
                waveHeight = waveMinHeight

            val top: Float = when (waveGravity) {
                WaveGravity.TOP -> paddingTop.toFloat()
                WaveGravity.CENTER -> paddingTop + getAvailableHeight() / 2F - waveHeight / 2F
                WaveGravity.BOTTOM -> mCanvasHeight - paddingBottom - waveHeight
            }

            mWaveRect.set(lastWaveRight, top, lastWaveRight + waveWidth, top + waveHeight)
            mWaveRectProgress.set(
                lastWaveRight,
                top + waveHeight / 2,
                lastWaveRight + waveWidth,
                top + waveHeight
            )

//            when {
//                mWaveRect.contains(getAvailableWith()*progress/maxProgress, mWaveRect.centerY()) -> {
//                    var bitHeight = mWaveRect.height().toInt()
//                    if (bitHeight <= 0)
//                        bitHeight = waveWidth.toInt()
//
//                    val bitmap = Bitmap.createBitmap(getAvailableWith(),bitHeight , Bitmap.Config.ARGB_8888)
//                    mProgressCanvas.setBitmap(bitmap)
//
//                    val fillWidth = (getAvailableWith()*progress/maxProgress)
//
//                    mWavePaint.color = waveProgressColor
//                    mProgressCanvas.drawRect(0F,0F,fillWidth,mWaveRect.bottom,mWavePaint)
//
//                    mWavePaint.color = waveBackgroundColor
//                    mProgressCanvas.drawRect(fillWidth,0F,getAvailableWith().toFloat(),mWaveRect.bottom,mWavePaint)
//
//                    val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//                    mWavePaint.shader = shader
//                }
//                mWaveRect.right <= getAvailableWith()*progress/maxProgress -> {
//                    mWavePaint.color = waveProgressColor
//                    mWavePaint.shader = null
//                }
//                else -> {
//                    mWavePaint.color = waveBackgroundColor
//                    mWavePaint.shader = null
//                }
//            }


            mWavePaint.color = waveBackgroundColor
            canvas.drawRoundRect(mWaveRect, waveCornerRadius, waveCornerRadius, mWavePaint)
            mWavePaint.color = waveProgressColor
            canvas.drawRoundRect(mWaveRectProgress, waveCornerRadius, waveCornerRadius, mWavePaint)


            if (i >= 20 && i <= 30) {


                if (i >= 20.0F && i <= 21.0f) {
                    startWave = lastWaveRight
                }

                endWave = lastWaveRight

                mWavePaint.color = Color.parseColor("#D1756D")
                mWaveRectSelectedWaves.set(
                    lastWaveRight,
                    top,
                    lastWaveRight + waveWidth,
                    top + waveHeight
                )
                canvas.drawRoundRect(
                    mWaveRectSelectedWaves,
                    waveCornerRadius,
                    waveCornerRadius,
                    mWavePaint
                )

            }


            lastWaveRight = mWaveRect.right + waveGap

            if (lastWaveRight + waveWidth > getAvailableWith() + paddingLeft)
                break

            i += 1 / step
        }

        mWavePaint.color = Color.parseColor("#0D00D3FF")
        mWaveRectSelection.set(
            startWave,
            top.toFloat(),
            endWave,
            top + height.toFloat() / 2
        )
        canvas.drawRoundRect(
            mWaveRectSelection,
            waveCornerRadius,
            waveCornerRadius,
            mWavePaint
        )

        canvas.drawLine(
            startWave, 0f,
            startWave, measuredHeight.toFloat(),
            mBorderLinePaint!!
        )
        canvas.drawLine(
            endWave, 0f,
            endWave, measuredHeight.toFloat(),
            mBorderLinePaintPink!!
        )


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled)
            return false

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isParentScrolling())
                    mTouchDownX = event.x
                else
                    updateProgress(event)
            }
            MotionEvent.ACTION_MOVE -> {
                updateProgress(event)
            }
            MotionEvent.ACTION_UP -> {
                if (abs(event.x - mTouchDownX) > mScaledTouchSlop)
                    updateProgress(event)

                performClick()
            }
        }
        return true
    }

    private fun isParentScrolling(): Boolean {
        var parent = parent as View
        val root = rootView

        while (true) {
            when {
                parent.canScrollHorizontally(1) -> return true
                parent.canScrollHorizontally(-1) -> return true
                parent.canScrollVertically(1) -> return true
                parent.canScrollVertically(-1) -> return true
            }

            if (parent == root)
                return false

            parent = parent.parent as View

        }
    }

    private fun updateProgress(event: MotionEvent?) {

        progress = (maxProgress * event!!.x / getAvailableWith()).toInt()
        invalidate()

        if (onProgressChanged != null)
            onProgressChanged!!.onProgressChanged(this, progress, true)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun getAvailableWith() = mCanvasWidth - paddingLeft - paddingRight
    private fun getAvailableHeight() = mCanvasHeight - paddingTop - paddingBottom

    var onProgressChanged: SeekBarOnProgressChanged? = null

    var sample: IntArray? = null
        set(value) {
            field = value
            invalidate()
        }

    var progress: Int = 0
        set(value) {
            field = value
            invalidate()

            if (onProgressChanged != null)
                onProgressChanged!!.onProgressChanged(this, progress, false)
        }

    var maxProgress: Float = 100F
        set(value) {
            field = value
            invalidate()
        }

    var waveBackgroundColor: Int = Color.LTGRAY
        set(value) {
            field = value
            invalidate()
        }

    var waveProgressColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    var waveGap: Float = Utils.dp(context, 2)
        set(value) {
            field = value
            invalidate()
        }

    var waveWidth: Float = Utils.dp(context, 5)
        set(value) {
            field = value
            invalidate()
        }

    var waveMinHeight: Float = waveWidth
        set(value) {
            field = value
            invalidate()
        }

    var waveCornerRadius: Float = Utils.dp(context, 2)
        set(value) {
            field = value
            invalidate()
        }

    var waveGravity: WaveGravity = WaveGravity.CENTER
        set(value) {
            field = value
            invalidate()
        }

    @ThreadBlocking
    fun setSampleFrom(audio: File) = setSampleFrom(audio.path)

    @ThreadBlocking
    fun setSampleFrom(path: String) {
        WaveformOptions.getSampleFrom(path) {
            sample = it
        }
    }
}