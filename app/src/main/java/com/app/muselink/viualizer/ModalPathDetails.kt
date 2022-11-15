package com.app.muselink.viualizer

import android.graphics.Paint
import android.graphics.Path
import android.widget.RelativeLayout

class ModalPathDetails {
    var cx : Float?  =0.0f
    var cy : Float?  =0.0f
    var x : Float?  =0.0f
    var y : Float?  =0.0f
    var path : Path?  =null
    var paint : Paint? = null
    var pathView : RelativeLayout? = null
    var pathViewAll : PathView? = null
    var displysedAngle : Float? = 0.0f
}