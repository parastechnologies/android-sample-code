package com.app.muselink.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getCureentDateTime():String{
        val currentDateTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return currentDateTime

    }


}