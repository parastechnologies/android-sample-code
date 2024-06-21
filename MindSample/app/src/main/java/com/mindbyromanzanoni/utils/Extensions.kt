package com.mindbyromanzanoni.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Geocoder
import android.location.Location
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.validators.Validator
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*



fun AlertDialog.hideKeyboardForAlertDialog(context: InputMethodManager?) {
    val view: View? = this.currentFocus
    if (view != null) {
        context!!.hideSoftInputFromWindow(view.windowToken, 0)
    }
}



fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


fun Activity.showToast(message: String?) {
    android.os.Handler(Looper.getMainLooper()).post {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


fun Activity.setStatusBarHideBoth(){
    with(window) {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.overrideImageStatusBar(context: Context){
    window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            )
    window.statusBarColor = Color.TRANSPARENT
}



@SuppressLint("Range")
fun ContentResolver.getFilename(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}



fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


fun Activity.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as
            InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


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


fun startZoomMeeting(context: Context, meetingId: String) {
    val zoomUrl = "zoomus://zoom.us/join?confno=$meetingId"
    val packageName = "us.zoom.videomeetings"

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(zoomUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Play Store app is not installed, open Play Store in browser
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        context.startActivity(intent)
    }
}

// Extension function for EditText to apply the text watcher and filter functionality
fun EditText.applyTextWatcherAndFilter(validator: Validator, image : ImageView) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            validateEmail(validator , image)
        }
    }
    this.addTextChangedListener(watcher)
}

// Function to filter text based on search query
private fun EditText.validateEmail(validator: Validator , image: ImageView) {
    val query = this.text.toString()
    val validColor = ContextCompat.getColor(context, R.color.theme_color_green)
    val invalidColor = ContextCompat.getColor(context, R.color.black)
    if (query.isEmpty()) {
        setTickColor(invalidColor, image)
    } else if (validator.isValidEmailId(query)) {
        setTickColor(validColor, image)
    } else {
        setTickColor(invalidColor, image)
    }
}

// Function to set tick color for validation
private fun setTickColor(color: Int, image: ImageView) {
    image.setColorFilter(color, PorterDuff.Mode.SRC_IN)
}



fun Activity.openChromeTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}


fun Activity.copyUrl(url:String) {
    if (url.isNotBlank()) {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(android.R.attr.label.toString(), url)
        clipboard.setPrimaryClip(clip)
     }
}
// Extension function for EditText to apply the text watcher and password validation
fun EditText.applyTextWatcherForPasswordValidate(validator: Validator, text : TextView) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            validatePassword(validator , text)
        }
    }
    this.addTextChangedListener(watcher)
}

// Function to hide and display password condition according to password validation
private fun EditText.validatePassword(validator: Validator , text: TextView) {
    val query = this.text.toString()
    if (query.isEmpty()) {
        text.visible()
    } else if (validator.isValidPasswordFormat(query)) {
        text.gone()
    } else {
        text.visible()
    }
}

fun getFirstWordOrInt(text:String) : Int{
    var intValue = 0
    val words = text.split("\\s+".toRegex())
    if (words.isNotEmpty()) {
        val firstWord = words[0]
        val numericValue = firstWord.takeWhile { it.isDigit() } // Extract numeric part
        intValue = numericValue.toIntOrNull() ?: 0 // Convert the extracted string to an integer
    }
     return intValue
}

// Extension function for EditText to set up text watcher
fun EditText.setSearchTextWatcher(callback: (String) -> Unit) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            callback(p0.toString())
        }
    }
    this.addTextChangedListener(watcher)
}

fun ShimmerFrameLayout.shimmerAnimationEffect(status:Boolean){
    if (status) {
        startShimmer()
        visible()
    } else {
        stopShimmer()
        gone()
    }
}


fun Int.isPositive(): Boolean = this > 0

fun Int.isNegative(): Boolean = this < 0



fun getFireBaseToken(data: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        } else {
            data(task.result)
        }
    })
}

// Extension function for EditText to set up text watcher
fun EditText.setAfterTextChangeWatcher(callback: (String) -> Unit) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            callback(p0.toString())
        }
    }
    this.addTextChangedListener(watcher)
}
/** Share  event details  other apps */
fun Activity.shareEvent(eventId: Int, eventType: Int) {
    val deepLinkUrl =
        Uri.parse("https://api.mindbyroman.com/share.html?EventId=$eventId&Type=$eventType")
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out this ${checkType(eventType)}: $deepLinkUrl")
        type = "text/plain"
    }
    startActivity(Intent.createChooser(sendIntent, "Share using"))
}
fun checkType(type:Int):String{
    return when(type){
        1-> "edification"
        2-> "meditation"
        3-> "resource"
        else -> "event"
    }
}
