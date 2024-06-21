package com.mindbyromanzanoni.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


@SuppressLint("SimpleDateFormat")
fun customDateFormat(_datetime: String, inputFormat: String, outFormat: String): String {
    return try {
        val inputForDate = SimpleDateFormat(inputFormat)
        val outputFormatDate = SimpleDateFormat(outFormat)
        val dateInput = inputForDate.parse(_datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun convertChatDateUtcToLocal(date: String?): String {
    val oldFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    oldFormatter.timeZone = TimeZone.getTimeZone("UTC")
    val value: Date?
    var dueDateAsNormal = ""
    try {
        value = oldFormatter.parse(date)
        val newFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        newFormatter.timeZone = TimeZone.getDefault()
        dueDateAsNormal = newFormatter.format(value)
    } catch (e: ParseException) {
        e.printStackTrace()
        Log.d("asdasdasasd", e.printStackTrace().toString())
    }
    return dueDateAsNormal
}

@SuppressLint("StaticFieldLeak", "SimpleDateFormat")
fun getTimeInAgo(date: String?): String {
    val updatedTime = convertDateTimeUtcToLocal(date)
    var time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getCurrentDateTime()).time.minus(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updatedTime).time
    )
    val days: Long = TimeUnit.MILLISECONDS.toDays(time)
    time -= TimeUnit.DAYS.toMillis(days)

    val hours: Long = TimeUnit.MILLISECONDS.toHours(time)
    time -= TimeUnit.HOURS.toMillis(hours)

    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(time)
    time -= TimeUnit.MINUTES.toMillis(minutes)

    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(time)
    time -= TimeUnit.SECONDS.toMillis(seconds)

    var str = ""
    when {
        days >= 1 -> {
            str = "$days days ago"
        }

        hours >= 1 -> {
            str = "$hours hours ago"
        }

        minutes >= 1 -> {
            str = "$minutes minutes ago"
        }

        seconds >= 1 -> {
            str = "$seconds seconds ago"
        }
    }
    return str
}


@SuppressLint("SimpleDateFormat")
fun convertDateTimeUtcToLocal(_datetime: String?): String {
    val value: Date?
    var dueDateAsNormal: String? = ""
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    outputFormat.timeZone = TimeZone.getDefault()
    value = inputFormat.parse(_datetime)
    dueDateAsNormal = outputFormat.format(value)
    return dueDateAsNormal
}


fun getCurrentDateTime(): String {
    val calendar = Calendar.getInstance().time
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return outputFormat.format(calendar)
}

@SuppressLint("SimpleDateFormat")
fun convertDateFormatMessage(datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormatDate = SimpleDateFormat("hh:mm a")
        val dateInput: Date = inputFormat.parse(datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}


@SuppressLint("SimpleDateFormat")
fun convertTimeLocalToUtc(datetime: String): String {
    try {
        val timeZone = TimeZone.getDefault()
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone(timeZone.id)
        val dateInput: Date? = inputFormat.parse(datetime)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        if (dateInput != null) {
            val calendar = Calendar.getInstance()
            if (calendar.getTimeZone().inDaylightTime(calendar.time)) {
                val calendarDTS = Calendar.getInstance()
                calendarDTS.time = dateInput
                calendarDTS.add(Calendar.HOUR_OF_DAY, -1)
                return inputFormat.format(calendarDTS.time)
            }
        }
        return inputFormat.format(dateInput)
    } catch (e: Exception) {
        return ""
    }
}

fun isInDst(tz: TimeZone, time: Date): Boolean {
    val calendar = Calendar.getInstance(tz)
    calendar.time = time
    // or supply a configured calendar with TZ as argument instead
    return calendar[Calendar.DST_OFFSET] != 0
}
@SuppressLint("SimpleDateFormat")
fun convertTimeLocalToUtcUnitedState(datetime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("America/Los_Angeles")
        val outputFormatDate = SimpleDateFormat("hh:mm a")
        outputFormatDate.timeZone = TimeZone.getTimeZone("UTC")
        val dateInput: Date? = inputFormat.parse(datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}

fun convertMilliseconds(timeInMilli: Long, convertedTime: (Long, Long, Long) -> Unit) {
    val totalSeconds = timeInMilli / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    convertedTime.invoke(hours, minutes, seconds)
}

fun String.convertDateFormat(inputFormat: String, outputFormat: String): String {
    val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
    if (this.isEmpty()) {
        return "-"
    }
    return try {
        outputDateFormat.format(inputDateFormat.parse(this))
    } catch (e: Exception) {
        "-"
    }
}

fun String.convertTimeFormat(inputFormat: String, outputFormat: String): String {
    val inputTimeFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val outputTimeFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

    val time: Date = inputTimeFormat.parse(this) ?: return ""

    return outputTimeFormat.format(time)
}

fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("dd/MM/yy")
    val dateInput: Date = inputFormat.parse(inputDate)
    return outputFormat.format(dateInput)
}

fun changeDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("yyyy-MM-dd")
    val dateInput: Date = inputFormat.parse(inputDate)
    return outputFormat.format(dateInput)
}

