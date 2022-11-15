package com.app.muselink.viualizer

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class CustomCircleVisualizerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var w = 0
    var h: Int = 0
    var usableWidth: Int = 0
    var usableHeight: Int = 0
    var radiusOriginal: Int = 0
    var radiusChange: Int = 0
    var cx: Int = 0
    var cy: Int = 0
    var lineLenght: Int = 0
    var startPointFromPos = 0

    private var mTimer1: Timer? = null
    private var mTt1: TimerTask? = null
    private val mTimerHandler: Handler = Handler()
    var canvas: Canvas? = null
    var IsFirstTime = true
    var IsChage = false
    var startAngle = 0
    var endAngle = 360
    var count = 0
    var length = 0.0f
    var listPaths = ArrayList<ModalPathDetails>()
    var radiusIncreseDecrese: Double? = 1.1

    fun GetRandomNumber(minimum: Double, maximum: Double): Double {
        val random = Random()
        return random.nextDouble() * (maximum - minimum) + minimum
    }

    fun startTimer() {
        mTimer1 = Timer()
        mTt1 = object : TimerTask() {
            override fun run() {
                mTimerHandler.post {
                    radiusIncreseDecrese = GetRandomNumber(1.1, 2.0)
//                    if(radiusIncreseDecrese!! >1.1){
//                        frequency = 300
//                    }else if(radiusIncreseDecrese!!> 1.2){
//
//                    }
//                    val data = String.format("%.1f", radiusIncreseDecrese)
//                    radiusIncreseDecrese = data.toDouble()
//                    radiusChange = (radiusOriginal / radiusIncreseDecrese!!).roundToInt()


//                    if(radiusIncreseDecrese!!<1.1){
//                        frequency = 200
//                    }else if(radiusIncreseDecrese!!<1.2){
//                        frequency = 500
//                    }else if(radiusIncreseDecrese!!<1.3){
//                        frequency = 1000
//                    }else if(radiusIncreseDecrese!!<1.4){
//                        frequency = 3500
//                    }else if(radiusIncreseDecrese!!<1.5){
//                        frequency = 6000
//                    }else if(radiusIncreseDecrese!!<1.6){
//                        frequency = 10000
//                    }else if(radiusIncreseDecrese!!<1.7){
//                        frequency = 20000
//                    }else{
//                        frequency = 200
//                    }

//                    invalidate()

                    count++
                    if(count == listPaths.size){
                        count = 0
                    }
                    var data = listPaths[count]
                }
            }
        }
        mTimer1?.schedule(mTt1, 200, 200)
    }

    fun initFrequency() {
        listFrequency.add(200)
        listFrequency.add(500)
        listFrequency.add(600)
        listFrequency.add(700)
        listFrequency.add(900)
        listFrequency.add(1000)
        listFrequency.add(200)
        listFrequency.add(300)
        listFrequency.add(400)
        listFrequency.add(500)
        listFrequency.add(6000)
        listFrequency.add(10000)
        listFrequency.add(400)
        listFrequency.add(600)
        listFrequency.add(900)
        listFrequency.add(1100)
        listFrequency.add(1200)
        listFrequency.add(1300)
    }

    fun callDrawLine() {
        frequency = listFrequency[count]
        count++
        if (count == listFrequency.size) {
            Log.e("Called", "Called")
            count = 0
        }
    }

    fun drawLineAccordingFrequency() {

//        callDrawLine()

//        if(frequency<200){
//            startAngle  = 180
//            endAngle = 270
//        }else if(frequency < 500){
//            startAngle  = 180
//            endAngle = 270
//        }else if(frequency < 1000){
//            startAngle  = 90
//            endAngle = 180
//        }else if(frequency < 3500){
//            startAngle  = 0
//            endAngle = 90
//        }else if(frequency < 6000){
//            startAngle  = 0
//            endAngle = 90
//        }else if(frequency < 10000){
//            startAngle  = 270
//            endAngle = 360
//        }else if(frequency < 20000){
//            startAngle  = 270
//            endAngle = 360
//        }
        startAngle = 0
        endAngle = 360
        invalidate()

//        startTimer()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (IsFirstTime) {
            IsFirstTime = false
            w = width;
            h = height;

            usableWidth = w
            usableHeight = h

            radiusOriginal = Math.min(usableWidth, usableHeight) / 2;
            cx = (usableWidth / 2)
            cy = (usableHeight / 2)
            startPointFromPos = (radiusOriginal.toFloat() / (1.5f)).toInt()
            this.canvas = canvas

            radiusChange = radiusOriginal

            for (i in startAngle until endAngle) {
                if (i % 3 == 0) {
                    Drawline(canvas!!, i.toFloat(), i, frequency)
                }
            }
        } else {

            w = width;
            h = height;

            usableWidth = w
            usableHeight = h

//            radiusOriginal = Math.min(usableWidth, usableHeight) / 2;
            cx = (usableWidth / 2)
            cy = (usableHeight / 2)
//            startPointFromPos = (radiusChange.toFloat() / (1.5f)).toInt()
            this.canvas = canvas

//            radiusChange = radiusOriginal

            for (i in startAngle until endAngle) {
                if (i % 3 == 0) {
                    Drawline(canvas!!, i.toFloat(), i, frequency)
                }
            }
        }

    }

    var listFrequency = ArrayList<Int>()

    var frequency = 180

    fun Drawline(canvas: Canvas, angle: Float, i: Int, frequency: Int) {

        var path: Path? = null
        path = Path()
        val paint = Paint()
        paint.strokeWidth = 5.0f
        paint.style = Paint.Style.STROKE
        val displacedAngle: Float = angle - 90.0f
        paint.strokeCap = Paint.Cap.ROUND

        if (i < 90) {
            val index = (i / 3).toFloat()
            val red = (248 - (5.7).toFloat() * index) / 255
            val green = (225 + (0.2 * index)) / 255
            val blue = (0 + (7.6 * index)) / 255
            paint.color = Color.argb(1.0f, red.toFloat(), green.toFloat(), blue.toFloat())
        } else if (i < 180) {
            val index = ((i - 90) / 3).toFloat()
            val red = (75 + (5).toDouble() * index) / 255
            val green = (231 + (7.5 * index)) / 255
            val blue = (228 + (0.67 * index)) / 255
            paint.color = Color.argb(1.0f, red.toFloat(), green.toFloat(), blue.toFloat())
        } else if (i < 270) {
            val index = ((i - 180) / 3).toFloat()
            val red = (233 + ((0.23).toFloat() * index)) / 255
            val green = (0 + (4.9 * index)) / 255
            val blue = (248 - (6.94 * index)) / 255
            paint.color = Color.argb(1.0f, red.toFloat(), green.toFloat(), blue.toFloat())
        } else {
            val index = ((i - 270) / 3).toDouble()
            val red = (240 + ((0.27).toDouble() * index)) / 255
            val green = (147 + (2.8 * index)) / 255
            val blue = (40 - (1.33 * index)) / 255
            paint.color = Color.argb(1.0f, red.toFloat(), green.toFloat(), blue.toFloat())
        }

//        paint.setPathEffect()

        val cx1 =
            (cx + (radiusOriginal - startPointFromPos) * Math.cos(displacedAngle * Math.PI / 180)).toInt()
        val cy1 =
            (cy + (radiusOriginal - startPointFromPos) * Math.sin(displacedAngle * Math.PI / 180)).toInt();

        val x =
            (cx) + Math.cos(degreesToRadians(displacedAngle)) * (radiusChange) //convert angle to radians for x and y coordinates
        val y = (cy) + Math.sin(degreesToRadians(displacedAngle)) * (radiusChange)
//
        path.moveTo(cx1.toFloat(), cy1.toFloat())
        path.lineTo((x.toFloat()), (y.toFloat()))
//

        val view = PathView(context)
        view.init(paint, cx1.toFloat(), cy1.toFloat(), x.toFloat(), y.toFloat())
        view.draw(canvas)

        var modalPathDetails = ModalPathDetails()
        modalPathDetails.cx = cx1.toFloat()
        modalPathDetails.cy = cy1.toFloat()
        modalPathDetails.x = x.toFloat()
        modalPathDetails.y = y.toFloat()
        modalPathDetails.paint = paint
        modalPathDetails.path = path
        modalPathDetails.pathView = view
        modalPathDetails.pathViewAll = view
        modalPathDetails.displysedAngle = displacedAngle


        listPaths.add(modalPathDetails)
//
//        canvas.drawPath(path, paint)

    }


    fun degreesToRadians(degrees: Float): Double {
        return degrees * Math.PI / 180
    }


}