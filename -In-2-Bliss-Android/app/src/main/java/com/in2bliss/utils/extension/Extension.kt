package com.in2bliss.utils.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.CalendarDate
import com.in2bliss.data.model.downloadResponse.DownloadStatus
import com.in2bliss.data.model.musicDetails.MusicCustomizeDetails
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.ui.activity.home.profileManagement.favourites.FavouritePopupAdapter
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.workManager.AudioConverterWorker
import com.in2bliss.workManager.DownloadWorker
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


fun <T> Activity.intent(
    destination: Class<T>,
    bundle: Bundle? = null
) {
    val intent = Intent(this, destination)
    if (bundle != null) intent.putExtras(bundle)
    this.startActivity(intent)
}

fun View.visible() {
    this.visibility = VISIBLE

}

fun View.gone() {
    this.visibility = GONE
}

fun View.inVisible() {
    this.visibility = INVISIBLE
}

fun View.visibility(isVisible: Boolean) {
    if (isVisible) {
        this.visible()
    } else this.gone()
}

fun CharSequence?.isValidPassword(): Boolean {
    this?.let {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        val passwordMatcher = Regex(passwordPattern)

        return passwordMatcher.find(this) != null
    } ?: return false
}

/**
 * Edittext password visibility
 * @param isPasswordVisible isPassword is visible
 * @param response callback to return drawable
 * */
fun EditText.passwordVisibility(isPasswordVisible: Boolean, response: (drawable: Int) -> Unit) {
    if (isPasswordVisible) {
        this.transformationMethod = PasswordTransformationMethod()
        response.invoke(R.drawable.ic_eye_open)
        this.setSelection(this.text.toString().length)
        return
    }
    this.transformationMethod = HideReturnsTransformationMethod()
    response.invoke(R.drawable.ic_eye_closed)
    this.setSelection(this.text.toString().length)
}

/**
 * Hide soft keyboard
 * */
fun Activity.hideKeyboard() {
    val input = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    input.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    return "${calendar.get(Calendar.YEAR)}-${
        String.format(
            "%02d",
            (calendar.get(Calendar.MONTH)) + 1
        )
    }-${
        String.format(
            "%02d",
            (calendar.get(Calendar.DAY_OF_MONTH))
        )
    }"
}

fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    return "${String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))}:${
        String.format("%02d", calendar.get(Calendar.MINUTE))
    }"
}


/**
 * Getting month total days list , month and year
 * @param incrementMonth increment month for to get next month days list
 * @param calendarData call back for getting calendar data list , month and year
 * */
fun getDatesForCalendar(
    incrementMonth: Int? = null,
    calendarData: (dateList: ArrayList<CalendarDate>, year: Int, month: Int) -> Unit
) {
    try {
        val calendar = Calendar.getInstance()
        if (incrementMonth != null) calendar.add(Calendar.MONTH, incrementMonth)
        val maxDaysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val calendarDateList: ArrayList<CalendarDate> = arrayListOf()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        for (date in 1..maxDaysOfMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, date)

            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "SUN"
                Calendar.MONDAY -> "MON"
                Calendar.TUESDAY -> "TUE"
                Calendar.WEDNESDAY -> "WED"
                Calendar.THURSDAY -> "THU"
                Calendar.FRIDAY -> "FRI"
                Calendar.SATURDAY -> "SAT"
                else -> ""
            }
            val calendarDate = CalendarDate(
                date = date,
                weekOfDay = dayOfWeek,
                year = year,
                month = month,
                isEmpty = false
            )
            calendarDateList.add(calendarDate)
        }

        calendarData.invoke(
            calendarDateList,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1
        )

    } catch (exception: Exception) {
        Log.d("CalendarDate", "getDatesForCalendar: ${exception.localizedMessage}")
    }
}

/**
 * Converting input date to giver date format
 * @param date input date
 * @param inputFormat input date format
 * @param outPutFormat desired date format
 * */
fun formatDate(
    date: String,
    inputFormat: String,
    outPutFormat: String,
    isUtc: Boolean = false,
    isOutPutUtc: Boolean = false
): String? {
    return try {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        if (isUtc) inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outPutDateFormat = SimpleDateFormat(outPutFormat, Locale.ENGLISH)
        if (isOutPutUtc) outPutDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val inputDate = inputDateFormat.parse(date) as Date
        return outPutDateFormat.format(inputDate)
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}


/**
 *  Time difference
 *  @param startHour
 *  @param startMinute
 *  @param endMinute
 *  @param endHour
 *  @param timeDifference
 *  */
fun timeDifference(
    startHour: Int?,
    startMinute: Int?,
    endHour: Int?,
    endMinute: Int?,
    timeDifference: (hour: Int, minute: Int) -> Unit
) {
    try {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, startHour ?: 0)
        calendar.set(Calendar.MINUTE, startMinute ?: 0)
        val startTime = calendar.timeInMillis

        calendar.add(Calendar.HOUR, endHour ?: 0)
        calendar.add(Calendar.MINUTE, endMinute ?: 0)
        val endTime = calendar.timeInMillis

        calendar.timeInMillis = endTime - startTime
        timeDifference.invoke(
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE)
        )
    } catch (exception: Exception) {
        exception.printStackTrace()
        timeDifference.invoke(0, 0)
    }
}

/**
 * Set image via glide
 * @param requestManager glide singleton instance
 * @param image
 * */
fun ImageView.glide(
    requestManager: RequestManager,
    image: Any,
    error: Int = R.color.cream_fcd9c5,
    placeholder: Int = R.color.cream_fcd9c5
) {
    requestManager
        .load(image)
        .placeholder(placeholder).addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                scaleType = ImageView.ScaleType.CENTER_CROP
                return false

            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                return false
            }
        })
        .error(error)
        .into(this)
}

/**
 * Showing the toast
 * @param message toast message
 * */
fun Activity.showToast(
    message: String
) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun Activity.showToastLong(
    message: String
) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun String.showSnackBar(parent: View) {
    Snackbar.make(parent, this, Snackbar.LENGTH_LONG).also {
        it.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 4
        it.show()
    }
}

fun checkEmail(email: String): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
}

val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

/**
 * Compress image
 * @param imageFile
 * @param activity
 * @return file
 * */
suspend fun compressImage(
    imageFile: File?,
    activity: Activity
): File? {
    return try {
        imageFile?.let { Compressor.compress(activity, it) }
    } catch (exception: Exception) {
        imageFile
    }
}

/**
 * Convert milliseconds to hour , minute and seconds
 * @param convertedTime
 * */
fun convertMilliseconds(
    timeInMilli: Long,
    convertedTime: (hour: Int, minute: Int, seconds: Int) -> Unit
) {
    try {
        val hour = ((timeInMilli / (1000 * 60 * 60)) % 24).toInt()
        val minute = ((timeInMilli / (1000 * 60)) % 60).toInt()
        val seconds = ((timeInMilli / 1000) % 60).toInt()
        convertedTime.invoke(
            hour, minute, seconds
        )

    } catch (exception: Exception) {
        exception.printStackTrace()
        convertedTime.invoke(
            0, 0, 0
        )
    }
}

@SuppressLint("InflateParams")
fun Context.popMenu(
    position: View,
    list: ArrayList<String>,
    onTypeClick: ((Int) -> Unit?)? = null
) {
    val inflater = LayoutInflater.from(this) as LayoutInflater
    val customView = inflater.inflate(R.layout.favourites_pop_menu, null)
    val popupWindow = PopupWindow(
        customView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    popupWindow.isOutsideTouchable = false
    popupWindow.isFocusable = true
    popupWindow.showAsDropDown(position)
    val recyclerView = customView.findViewById<RecyclerView>(R.id.rvRec)
    val adapter = FavouritePopupAdapter()
    recyclerView.adapter = adapter
    adapter.submitList(list)
    adapter.onClick = {
        onTypeClick?.invoke(it)
        popupWindow.dismiss()
    }
}

fun fromStringArrayLists(value: String?): java.util.ArrayList<String> {
    val listType: Type = object : TypeToken<java.util.ArrayList<String>>() {}.type
    return Gson().fromJson(value, listType)
}


/**
 * Convert time 24 hour format to 12 hour with AM and PM
 * @param time
 * @return converted time
 *
 */
fun convert24To12HourFormat(
    time: String,
): String? {
    return try {
        val dateFormat24Hour = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val dateFormat12Hour = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val formattedTime = dateFormat24Hour.parse(time) as Date
        dateFormat12Hour.format(formattedTime)
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}

/**
 * Code for return integer value from the given string
 * @param value
 * @return Int -> number
 */
fun getNumber(value: String): Int {
    return (value.replace("[^0-9]".toRegex(), "").toInt())
}

/**
 * Loading svg
 * @param imageLoader
 * @param imageRequest
 * @param url svg url
 * */
fun ImageView.loadSvg(
    imageLoader: ImageLoader,
    imageRequest: ImageRequest.Builder,
    url: String,
    error: Int = R.color.prime_blue_418FF6,
    placeholder: Int = R.color.prime_blue_418FF6
) {

    val request = imageRequest
        .data(url)
        .target(this)
        .error(error)
        .placeholder(placeholder)
        .build()

    imageLoader.enqueue(request)
}

/**
 * Time picker
 * @param context
 * @param selectedTime time callback
 * */
fun timePicker(
    context: Context,
    selectedTime: (hour: Int, minute: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val timePicker = TimePickerDialog(
        context, { _, hour, minute ->
            selectedTime.invoke(hour, minute)
        },
        calendar.get(Calendar.HOUR),
        calendar.get(Calendar.MINUTE), false
    )
    timePicker.show()
}

/**
 * Converting seconds to minute
 * */
fun secondsToMinute(
    seconds: Int?,
    converted: (minute: Int, second: Int) -> Unit
) {
    try {
        if (seconds == null) {
            converted.invoke(0, 0)
        } else converted.invoke(seconds / 60, seconds)
    } catch (exception: Exception) {
        converted.invoke(0, 0)
    }
}

fun convertTimeToSeconds(
    hour: Int?,
    minutes: Int?
): Int {
    val minuteToSeconds = if (minutes != null) minutes * 60 else 0
    val hourInSeconds = if (hour != null) hour * 60 * 60 else 0
    return minuteToSeconds + hourInSeconds
}

fun convertTimeToMilliseconds(
    hour: Int?,
    minutes: Int?,
    seconds:Int?=null
): Long {
    val minuteToSeconds = if (minutes != null) minutes * 60 * 1000 else 0
    val hourInSeconds = if (hour != null) hour * 60 * 60 * 1000 else 0
    val seconds= (seconds?:0)*1000
    return (minuteToSeconds + hourInSeconds + seconds).toLong()
}

fun isPastDate(
    selectedDate: String
): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = dateFormat.parse(selectedDate) as Date

        val calendar = Calendar.getInstance()
        val currentTime = calendar.time

        calendar.time = formattedDate
        val selectedDateTime = calendar.time

        if (selectedDate == getCurrentDate()) false else selectedDateTime.before(currentTime)
    } catch (exception: Exception) {
        exception.printStackTrace()
        true
    }
}

/**
 * CountDown
 * @param countDownTime callback of hour,minute and seconds
 * @param isFinished callback when timer is finished
 * @param countDownTimerInstance getting instance of countDownTimer for stop the countdown
 * */
fun countDown(
    countDownTimeInLong: Long,
    countDownTime: (countDown: Long?, hour: Int, minute: Int, seconds: Int) -> Unit,
    isFinished: () -> Unit,
    countDownTimerInstance: (countDownTimer: CountDownTimer) -> Unit
) {
    val countDown = object : CountDownTimer(countDownTimeInLong, 1000) {
        override fun onTick(p0: Long) {
            val hour = ((p0 / (1000 * 60 * 60)) % 24).toInt()
            val minute = ((p0 / (1000 * 60)) % 60).toInt()
            val seconds = ((p0 / 1000) % 60).toInt()
            countDownTime.invoke(p0, hour, minute, seconds)
        }

        override fun onFinish() {
            countDownTime.invoke(null, 0, 0, 0)
            isFinished.invoke()
        }
    }
    countDown.start()
    countDownTimerInstance.invoke(countDown)
}

/**
 * Downloading with WorkManager
 * @param category category which screen it going from or download in initiated from for base url
 * @param musicDetails used for getting info like music url and image url title and description
 * @param activity
 * */
fun downloadWithWorkManager(
    category: AppConstant.HomeCategory?,
    musicDetails: MusicDetails?,
    activity: Activity
) {
    try {
        val data = Data.Builder()
        data.putString(AppConstant.MUSIC_DETAILS, Gson().toJson(musicDetails))
        data.putString(AppConstant.MUSIC_DETAILS, Gson().toJson(musicDetails))
        data.putString(AppConstant.CATEGORY_NAME, category?.name)

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setExpedited(
                policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
            )
            .setInputData(
                inputData = data.build()
            )
            .build()
        WorkManager.getInstance(activity).enqueue(workRequest)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

/**
 * Audio conversion with work manager
 * @param activity
 * @param fileUri which going to be converted
 * */
fun audioConversionWithWorkManger(
    activity: Activity,
    fileUri: Uri?
) {
    try {
        val data = Data.Builder()
        data.putString(AppConstant.AUDIO, fileUri.toString())
        val workRequest = OneTimeWorkRequestBuilder<AudioConverterWorker>()
            .setExpedited(
                policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
            )
            .setInputData(
                inputData = data.build()
            )
            .build()
        WorkManager.getInstance(activity).enqueue(workRequest)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

/**
 * Getting downloading status according to music id and music url
 * @param downloadStatus current download status
 * @param downloadStarted is Downloading is in progress
 * @param music music detail of the current open screen
 * */
fun gettingDownloadStatus(
    downloadStatus: DownloadStatus,
    downloadStarted: Boolean,
    music: MusicDetails?
): AppConstant.DownloadStatus {
    return try {
        val musicDetails = downloadStatus.musicDetails
        when {
            (musicDetails?.musicId == music?.musicId &&
                    musicDetails?.musicUrl == music?.musicUrl) -> downloadStatus.downloadStatus

            else -> if (downloadStarted) AppConstant.DownloadStatus.DOWNLOADING else AppConstant.DownloadStatus.NOT_DOWNLOAD
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        AppConstant.DownloadStatus.NOT_DOWNLOAD
    }
}

fun getFormattedStopWatch(ms: Long): String {
    var milliseconds = ms
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    milliseconds -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

    return "${if (hours < 10) "0" else ""}$hours:" +
            "${if (minutes < 10) "0" else ""}$minutes:" +
            "${if (seconds < 10) "0" else ""}$seconds"
}

/**
 * Getting image base url
 * @param category which screen it is navigate from
 * @param image image target url
 * */
fun getImageUrl(
    category: AppConstant.HomeCategory?,
    type: Int? = null,
    image: String?,
    createdBy: Int = 0,
    background: String = ""
): String {

    Log.d("ascsac", "getImageUrl: $category")
    Log.d("ascsac", "getImageUrl: $type")
    Log.d("ascsac", "getImageUrl: $image")


    return when (category) {
        AppConstant.HomeCategory.TEXT_AFFIRMATION -> {
            if (createdBy == 1) {
                BuildConfig.AFFIRMATION_BASE_URL.plus(image ?: "")
            } else {
                BuildConfig.AFFIRMATION_BASE_URL.plus(background ?: "")
            }

        }

        AppConstant.HomeCategory.GUIDED_MEDITATION -> {
            BuildConfig.MEDITATION_BASE_URL.plus(image ?: "")
        }

        AppConstant.HomeCategory.GUIDED_AFFIRMATION, AppConstant.HomeCategory.CREATE_AFFIRMATION -> {
            BuildConfig.AFFIRMATION_BASE_URL.plus(image ?: "")
        }

        AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
            BuildConfig.WISDOM_BASE_URL.plus(image ?: "")
        }

        AppConstant.HomeCategory.MUSIC -> {
            BuildConfig.MUSIC_BASE_URL.plus(image ?: "")
        }

        AppConstant.HomeCategory.GUIDED_SLEEP -> {
            when (type) {
                ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> {
                    BuildConfig.AFFIRMATION_BASE_URL.plus(image ?: "")
                }

                ApiConstant.ExploreType.MEDITATION.value.toInt() -> {
                    BuildConfig.MEDITATION_BASE_URL.plus(image ?: "")
                }

                else -> {
                    BuildConfig.MUSIC_BASE_URL.plus(image ?: "")
                }
            }
        }

        else -> image ?: ""
    }
}

/**
 * Getting audio complete url
 * @param category which screen it is navigate from
 * @param audio target url
 * */
fun gettingAudioUrl(
    category: AppConstant.HomeCategory?,
    audio: String?
): String {
    return when (category) {
        AppConstant.HomeCategory.GUIDED_MEDITATION -> {
            (BuildConfig.MEDITATION_BASE_URL.plus(audio.orEmpty()))
        }

        AppConstant.HomeCategory.GUIDED_AFFIRMATION, AppConstant.HomeCategory.CREATE_AFFIRMATION -> {
            (BuildConfig.AFFIRMATION_BASE_URL.plus(audio.orEmpty()))
        }

        AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
            (BuildConfig.WISDOM_BASE_URL.plus(audio.orEmpty()))
        }

        AppConstant.HomeCategory.MUSIC -> {
            (BuildConfig.MUSIC_BASE_URL.plus(audio.orEmpty()))
        }

        else -> audio.orEmpty()
    }
}

/**
 * Getting favourite hashmap
 * @param category which screen it is coming from
 * @param isFavourite is favourite
 * @param favMusicId
 * */
fun getFavouriteHashMap(
    category: AppConstant.HomeCategory?,
    isFavourite: Boolean?,
    favMusicId: Int?,
    isSleep: Boolean = false
): HashMap<String, String> {

    val hashMap = HashMap<String, String>()
    hashMap[ApiConstant.STATUS] = if (isFavourite == true) {
        ApiConstant.FavouriteStatus.RemoveFavourite.value.toString()
    } else ApiConstant.FavouriteStatus.Favourite.value.toString()

    when (category) {
        AppConstant.HomeCategory.GUIDED_MEDITATION -> {
            hashMap[ApiConstant.MEDITATION_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Meditation.value.toString()
            val type = if (isSleep) {
                ApiConstant.MeditationOrMusicFavouriteType.Sleep.value.toString()
            } else ApiConstant.MeditationOrMusicFavouriteType.Normal.value.toString()
            hashMap[ApiConstant.TYPE] = type
        }

        AppConstant.HomeCategory.GUIDED_AFFIRMATION -> {
            hashMap[ApiConstant.AFFIRMATION_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Affirmation.value.toString()
            val type = if (isSleep) {
                ApiConstant.AffirmationFavouriteType.SleepAffirmation.value.toString()
            } else ApiConstant.AffirmationFavouriteType.Guided.value.toString()
            hashMap[ApiConstant.TYPE] = type
        }
        AppConstant.HomeCategory.SLEEP_AFFIRMATION->{
            hashMap[ApiConstant.AFFIRMATION_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] = ApiConstant.FavouriteType.Affirmation.value.toString()
            hashMap[ApiConstant.TYPE] = ApiConstant.AffirmationFavouriteType.SleepAffirmation.value.toString()



        }

        AppConstant.HomeCategory.CREATE_AFFIRMATION -> {
            hashMap[ApiConstant.AFFIRMATION_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Affirmation.value.toString()
            hashMap[ApiConstant.TYPE] =
                ApiConstant.AffirmationFavouriteType.MyAffirmation.value.toString()
        }

        AppConstant.HomeCategory.TEXT_AFFIRMATION -> {
            hashMap[ApiConstant.AFFIRMATION_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Affirmation.value.toString()
            hashMap[ApiConstant.TYPE] =
                ApiConstant.AffirmationFavouriteType.Text.value.toString()
        }

        AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
            hashMap[ApiConstant.WISDOM_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Wisdom.value.toString()
            hashMap[ApiConstant.TYPE] =
                ApiConstant.MeditationOrMusicFavouriteType.Normal.value.toString()
        }

        AppConstant.HomeCategory.MUSIC -> {
            hashMap[ApiConstant.MUSIC_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] =
                ApiConstant.FavouriteType.Music.value.toString()
            val type = if (isSleep) {
                ApiConstant.MeditationOrMusicFavouriteType.Sleep.value.toString()
            } else ApiConstant.MeditationOrMusicFavouriteType.Normal.value.toString()
            hashMap[ApiConstant.TYPE] = type
        }
        AppConstant.HomeCategory.SLEEP_MEDIATION->{
            hashMap[ApiConstant.MEDITATION_ID] = favMusicId.toString()
            hashMap[ApiConstant.FAVOURITE_TYPE] = ApiConstant.FavouriteType.Meditation.value.toString()
            hashMap[ApiConstant.TYPE] = ApiConstant.MeditationOrMusicFavouriteType.Sleep.value.toString()


        }



        else -> {}
    }
    return hashMap
}

/**
 * Getting the musicDetails for the affirmation detail screen and player screen
 * @param category which screen it navigate from
 * @param data music data
 * */
fun getDataForPlayer(
    category: AppConstant.HomeCategory?,
    data: MusicList.Data.Data,
    type: Int? = null
): MusicDetails {

    Log.d("sacaca", "getDataForPlayer: $category")
    Log.d("sacaca", "getDataForPlayer: $type")
    val affirmationAudio = when (category) {
        AppConstant.HomeCategory.GUIDED_AFFIRMATION -> data.affirmation
        AppConstant.HomeCategory.CREATE_AFFIRMATION -> data.audio
        AppConstant.HomeCategory.GUIDED_MEDITATION -> data.affirmation
        AppConstant.HomeCategory.WISDOM_INSPIRATION -> data.affirmation
        AppConstant.HomeCategory.MUSIC -> data.audio
        AppConstant.HomeCategory.GUIDED_SLEEP -> {
            when (type) {
                ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> data.affirmation
                ApiConstant.ExploreType.MEDITATION.value.toInt() -> data.affirmation
                else -> data.audio
            }
        }

        else -> null
    }

    val title = when (category) {
        AppConstant.HomeCategory.GUIDED_AFFIRMATION -> data.title
        AppConstant.HomeCategory.CREATE_AFFIRMATION -> data.title
        AppConstant.HomeCategory.GUIDED_MEDITATION -> data.title
        AppConstant.HomeCategory.WISDOM_INSPIRATION -> data.title
        AppConstant.HomeCategory.MUSIC -> data.audioName
        AppConstant.HomeCategory.GUIDED_SLEEP -> {
            when (type) {
                ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> data.title
                ApiConstant.ExploreType.MEDITATION.value.toInt() -> data.title
                else -> data.audioName
            }
        }

        else -> null
    }

    val backgroundAudio = when (category) {
        AppConstant.HomeCategory.GUIDED_AFFIRMATION -> data.audio
        AppConstant.HomeCategory.GUIDED_MEDITATION -> data.audio
        AppConstant.HomeCategory.CREATE_AFFIRMATION -> data.background
        AppConstant.HomeCategory.WISDOM_INSPIRATION -> data.audio
        AppConstant.HomeCategory.GUIDED_SLEEP -> {
            when (type) {
                ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> data.audio
                ApiConstant.ExploreType.MEDITATION.value.toInt() -> data.audio
                else -> null
            }
        }

        else -> null
    }


    val introAudio = when (category) {
        AppConstant.HomeCategory.GUIDED_AFFIRMATION -> data.introAffirmation
        AppConstant.HomeCategory.WISDOM_INSPIRATION -> data.introAffirmation
        AppConstant.HomeCategory.GUIDED_SLEEP -> {
            when (type) {
                ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> data.introAffirmation
                else -> null
            }
        }

        else -> null
    }


    val customiseAffirmation = MusicCustomizeDetails()

    data.customise?.duration?.let { duration ->
        convertMilliseconds(
            timeInMilli = (duration * 1000).toLong(),
            convertedTime = { hour, minute, _ ->
                customiseAffirmation.affirmationHour = hour
                customiseAffirmation.affirmationMinute = minute
            }
        )
    }
    data.customise?.backgroundDuration?.let { backgroundDuration ->
        convertMilliseconds(
            timeInMilli = (backgroundDuration * 1000).toLong(),
            convertedTime = { hour, minute, _ ->
                customiseAffirmation.backgroundMusicHour = hour
                customiseAffirmation.backgroundMusicMinute = minute
            }
        )
    }

    customiseAffirmation.isBackgroundMusicEnabled = if (data.customise != null) {
        data.customise.durationStatus == 1
    } else true

    customiseAffirmation.backgroundMusicUrl = data.customise?.music?.audio
    customiseAffirmation.backgroundMusicTitle = data.customise?.music?.audioName
    customiseAffirmation.backgroundMusicImage = data.customise?.music?.thumbnail
    customiseAffirmation.musicCategoryId = data.customise?.CID
    customiseAffirmation.isSleep = category == AppConstant.HomeCategory.GUIDED_SLEEP
    customiseAffirmation.backgroundMusicId = data.customise?.MID

    val ableToChangeBackgroundMusic = when (category) {
        AppConstant.HomeCategory.GUIDED_AFFIRMATION -> true
        AppConstant.HomeCategory.CREATE_AFFIRMATION -> true
        AppConstant.HomeCategory.GUIDED_SLEEP -> {
            when (type) {
                ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> true
                ApiConstant.ExploreType.MEDITATION.value.toInt() -> true
                else -> false
            }
        }

        else -> false
    }

    return MusicDetails(
        musicTitle = title,
        musicDescription = data.description,
        musicId = data.id,
        musicUrl = affirmationAudio,
        musicViews = data.views,
        musicFavouriteStatus = data.favouriteStatus,
        musicThumbnail = data.thumbnail,
        musicBackground = data.thumbnail,
        backgroundMusicUrl = backgroundAudio,
        affirmationIntroduction = introAudio,
        duration = data.duration,
        musicCustomizeDetail = customiseAffirmation,
        backgroundMusicTitle = data.audioName,
        ableToChangeBackgroundMusic = ableToChangeBackgroundMusic,
        isSleep = category == AppConstant.HomeCategory.GUIDED_SLEEP
    )
}

/**
 * Get fcm token for push notification
 * @param fcmToken fcm token callback
 * */
fun getFcmToken(
    fcmToken: ((token: String) -> Unit)
) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            fcmToken.invoke(task.result)
        }
    }
}

/**
 *  Category change according to the type when Guided sleep else categoryType
 * */
fun categoryType(
    categoryName: AppConstant.HomeCategory?,
    type: Int?
): AppConstant.HomeCategory? {
    return if (categoryName == AppConstant.HomeCategory.GUIDED_SLEEP) {
        when (type) {
            ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
            ApiConstant.ExploreType.MEDITATION.value.toInt() -> AppConstant.HomeCategory.GUIDED_MEDITATION
            else -> AppConstant.HomeCategory.MUSIC
        }
    } else categoryName
}

/**
 *  Category change according to the type when Guided sleep else categoryType
 * */
fun getRealCategoryType(
    categoryName: AppConstant.HomeCategory?,
    type: Int?
): AppConstant.HomeCategory? {
    return if (categoryName == AppConstant.HomeCategory.GUIDED_SLEEP) {
        when (type) {
            ApiConstant.ExploreType.AFFIRMATION.value.toInt() -> AppConstant.HomeCategory.SLEEP_AFFIRMATION
            ApiConstant.ExploreType.MEDITATION.value.toInt() -> AppConstant.HomeCategory.SLEEP_MEDIATION
            else -> AppConstant.HomeCategory.MUSIC
        }
    } else categoryName
}

/**
 * Converting music to mp3
 * @param filepath path of the audio file
 * @param activity
 * @return ConvertedFile containing music url and music name
 * */
suspend fun convertAudio(
    filepath: Uri,
    activity: Context,
): ConvertedFile {

    var isSuccess: Int? = null
    val tempFile =
        File(activity.cacheDir, "${System.currentTimeMillis().toString().takeLast(5)}.aac")

    activity.contentResolver.openInputStream(filepath).use { inputStream ->
        inputStream?.let {
            FileOutputStream(tempFile).use { outPutStream ->
                inputStream.copyTo(outPutStream)
            }
        }
    }

    val fileName = "${activity.resources.getString(R.string.app_name)}_${
        System.currentTimeMillis().toString().takeLast(5)
    }.mp3"

    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "In2Bliss"
    )
    if (dir.exists().not()) dir.mkdir()
    val cacheFile = File(dir, fileName)

    val command =
        "-i ${tempFile.path} -ab 320k -f mp3 ${cacheFile.path}"

    FFmpeg.executeAsync(command) { _, returnCode ->
        isSuccess = if (returnCode == RETURN_CODE_SUCCESS) {
            RETURN_CODE_SUCCESS
        } else -1
    }

    while (isSuccess == null) {
        delay(100)
    }

    withContext(Dispatchers.Main) {
        ProgressDialog.hideProgress()
    }

    return if (isSuccess == RETURN_CODE_SUCCESS) {
        ConvertedFile(
            fileName = cacheFile.name,
            fileUri = FileProvider.getUriForFile(
                activity,
                "com.in2bliss",
                cacheFile
            ),
            status = RETURN_CODE_SUCCESS
        )
    } else ConvertedFile(
        fileUri = null,
        fileName = null,
        status = -1
    )
}

data class ConvertedFile(
    val fileUri: Uri?,
    val fileName: String?,
    val status: Int?
)

fun Context.intentComponentShareTourCode() {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hey i am inviting you in In2bliss  App \n Please download the application from google Play store and you will get $5 on your Booking.\n https://play.google.com/store/apps"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(
        sendIntent,
        getString(R.string.invite_a_friend)
    )
    this.startActivity(shareIntent)
}

fun setTimeFormat(date: String, dateFormat: String): String {
    val dateFormats = SimpleDateFormat(dateFormat, Locale.ENGLISH)
    dateFormats.timeZone = TimeZone.getTimeZone("UTC")
    val countDate = dateFormats.parse(date)
    var returnTime = ""
    if (countDate != null) {
        val daysAgo = getDaysAgo(countDate)
        returnTime = if (daysAgo >= 30) {
            getMonthsAgo(countDate)
        } else if (daysAgo != 0) {
            daysAgo.toString().plus(" days ")
        } else {
            val time = getTimeAgo(countDate)
            time
        }
    }
    return returnTime
}


fun getDaysAgo(date: Date): Int {
    val calendar = Calendar.getInstance()
    calendar.time = date

    val currentDate = Calendar.getInstance()
    val days = (currentDate.timeInMillis - calendar.timeInMillis) / (24 * 60 * 60 * 1000)

    return days.toInt()
}

fun convertStringToList(name: String): java.util.ArrayList<String> {
    val result = name.split(",").map { it.trim() }
    return result as java.util.ArrayList<String>
}

fun getMonthsAgo(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date

    val currentDate = Calendar.getInstance()

    val yearDiff = currentDate.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
    val monthDiff = currentDate.get(Calendar.MONTH) - calendar.get(Calendar.MONTH)

    val monthsAgo = yearDiff * 12 + monthDiff

    return "$monthsAgo months ago"
}

fun getTimeAgo(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date

    val currentDate = Calendar.getInstance()

    val millisecondsDifference = currentDate.timeInMillis - calendar.timeInMillis

    val seconds = millisecondsDifference / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> "$minutes minutes ago"
        else -> "$hours hours ago"
    }
}


fun ImageView?.setImageRestaurantFromUrl(imageUrl: String?, progressBar: ProgressBar?) {
    progressBar?.visible()
    Glide
        .with(this?.context!!)
        .load(imageUrl)
        .dontAnimate()
        .placeholder(R.drawable.ic_user_heart)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                progressBar?.gone()
                return false
            }
        }).into(this)
}

fun formatSecondsToHhMm(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%d:%d:%d", hours, minutes,secs)
}

fun convertHhMmSsToSeconds(time: String): Int {
    val parts = time.split(":")
    return if (parts.size == 3) {
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        val seconds = parts[2].toIntOrNull() ?: 0
        hours * 3600 + minutes * 60 + seconds
    } else {
        val minutes = parts[0].toIntOrNull() ?: 0
        val seconds = parts[1].toIntOrNull() ?: 0
        minutes * 60 + seconds
    }
}

fun getMediaDuration(url: String): Long? {
    return try {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(url,java.util.HashMap())
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        durationStr?.toLong()
    } catch (e: Exception) {
        null
    }
}