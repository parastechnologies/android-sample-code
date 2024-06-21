package com.highenergymind.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.data.AffDay
import com.highenergymind.data.Affirmation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Suppress("DEPRECATION")
fun Activity.fullScreenStatusBar(isTextBlack: Boolean = true) {
    window.statusBarColor = Color.TRANSPARENT

    if (isTextBlack) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    } else {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}

fun String.firstUpper() = get(0).uppercase() +slice(1..<length)


fun Context.showToast(message: String?) {
    message?.let {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }
}

/**
 * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
 * @param context Context reference to get the TelephonyManager instance from
 * @return country code or null
 */
fun getUserCountry(context: Context): String? {
    try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        if (simCountry != null && simCountry.length == 2) { // SIM country code is available
            return simCountry.lowercase()
        } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
            val networkCountry = tm.networkCountryIso
            if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                return networkCountry.lowercase()
            }
        }
    } catch (e: java.lang.Exception) {
    }
    return null
}

/**
 * this function is use to get file path from uri
 * @param uri
 * @param activity
 * */
@SuppressLint("Recycle")
fun getFilePath(activity: Activity, uri: Uri): File {
    if (uri.scheme.equals("file", true)) {
        return File(uri.path)
    }
    val parcelFileDescriptor = activity.contentResolver.openFileDescriptor(uri, "r", null)
    val file = File(activity.cacheDir, activity.contentResolver.getFilename(uri))
    val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
    val outputStream = FileOutputStream(file)

    inputStream.copyTo(outputStream)
    return file
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


/**
 * Return image file path uri
 * @param context
 * @return Uri
 * */
@SuppressLint("SimpleDateFormat")
fun fileImageUri(context: Context): Uri {
    val filePath = File(
        context.cacheDir,
        context.resources.getString(R.string.app_name)
    )
    if (!filePath.exists()) filePath.mkdir()
    val timeStamp: String = SimpleDateFormat("dd_MMM_yyyy").format(Date())
    val file = File(filePath, "${"IMG_${timeStamp}_"}.png")
    if (!file.exists()) file.createNewFile()
    return FileProvider.getUriForFile(context, context.packageName, file)
}

/**
 * Convert image multiPart
 * @param file
 * @return MultiPartBody.part
 * */
fun toRequestBodyProfileImage(
    file: File?, apiParamName: String = ApiConstant.IMG,
    type: String = AppConstant.INTENT_IMAGE
): MultipartBody.Part? {
    val data = file?.asRequestBody(type.toMediaTypeOrNull())
    return data?.let {
        MultipartBody.Part.createFormData(
            apiParamName,
            file.name,
            it
        )
    }
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.showOrGone(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}


fun Context.checkCameraPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    } else {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

fun String.requestBody(): RequestBody {
    return toRequestBody("text/plain".toMediaTypeOrNull())
}

fun progressBar(context: Context): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f

    return circularProgressDrawable
}


/**
 * Set image to image view
 * @param image
 * */
fun ImageView.glideImage(image: Any?, placeholder: Int? = null) {
    val progressBar = progressBar(context)
    progressBar.setTintList(ContextCompat.getColorStateList(context, R.color.bg_color_1))
    progressBar.setTint(ContextCompat.getColor(context, R.color.bg_color_1))
    progressBar.start()
    Glide.with(context).load(image ?: "").placeholder(progressBar)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                Log.i("MY_LOG_TAG", "onLoadFailed: " + e.toString())
                progressBar.stop()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.stop()
                return false
            }
        }).error(placeholder ?: R.drawable.ic_placeholder).into(this)
}


fun String.showSnackBar(parent: View) {
    Snackbar.make(parent, this, Snackbar.LENGTH_LONG).also {
        it.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 4
        it.show()
    }
}

fun String.toDateFormat(
    inputFormat: String,
    outputFormat: String,
    isUtc: Boolean = false
): String? {
    val inputFor = SimpleDateFormat(inputFormat, Locale.getDefault())
    if (isUtc) inputFor.timeZone = TimeZone.getTimeZone("UTC")
    val outputFor = SimpleDateFormat(outputFormat, Locale.getDefault())
    if (isUtc) outputFor.timeZone = TimeZone.getDefault()
    val date = inputFor.parse(this)
    return try {
        outputFor.format(date)
    } catch (e: Exception) {
        null
    }
}


fun getFireBaseToken(onSuccess: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("my_log_tag", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }
        // Get new FCM registration token
        val token = task.result
        if (token != null) {
            onSuccess.invoke(token)
        }
    })
}

fun millisecondsToMMSS(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}


fun convertSecondsToMMSS(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun String.timeInAgo(inputFormat: String): String {
    return try {
        val input = SimpleDateFormat(inputFormat, Locale.getDefault())
        input.timeZone = TimeZone.getTimeZone("UTC")
        val timeMill = input.parse(this)

        val ago = DateUtils.getRelativeTimeSpanString(
            timeMill!!.time,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        if ("In 0 minutes" == ago.toString() || "0 minutes ago" == ago.toString()) {
            return "just now"
        }
        ago.toString()
    } catch (e: Exception) {
        "-"
    }
}

@SuppressLint("HardwareIds")
fun Activity.getAndroidID(): String? {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}


fun getFirebaseToken(onSuccess: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        onSuccess.invoke(token)
    })
}

fun makeTextViewResizable(
    tv: TextView,
    maxLine: Int,
    expandText: String,
    viewMore: Boolean,
    isJobColor: Boolean = false
) {
    if (tv.tag == null) {
        tv.tag = tv.text
    }
    val vto = tv.viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val text: String
            val lineEndIndex: Int
            val obs = tv.viewTreeObserver
            obs.removeOnGlobalLayoutListener(this)
            if (viewMore && tv.lineCount >= maxLine) {

                lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                    .toString() + " " + expandText
            } else {
                lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
            }
            tv.text = text
            tv.movementMethod = LinkMovementMethod.getInstance()
            tv.setText(
                addClickablePartTextViewResizable(
                    SpannableString(tv.text.toString()), tv, maxLine, expandText,
                    viewMore,
                    isJobColor
                ), TextView.BufferType.SPANNABLE
            )
        }
    })
}

private fun addClickablePartTextViewResizable(
    strSpanned: Spanned, tv: TextView,
    maxLine: Int, spanableText: String, viewMore: Boolean, isJobColor: Boolean,
): SpannableStringBuilder {
    val str = strSpanned.toString()
    val ssb = SpannableStringBuilder(strSpanned)
    if (str.contains(spanableText)) {

        ssb.setSpan(
            StyleSpan(Typeface.BOLD),
            str.indexOf(spanableText),
            str.indexOf(spanableText) + spanableText.length,
            0
        )
        val color = tv.context.getColor(R.color.content_color)



        ssb.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = color

            }

            override fun onClick(widget: View) {
                tv.layoutParams = tv.layoutParams
                tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                tv.invalidate()
                if (viewMore) {
                    makeTextViewResizable(tv, maxLine, "Read Less", false, isJobColor)
                } else {
                    makeTextViewResizable(tv, maxLine, "Read More", true, isJobColor)
                }

            }
        }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
    }
    return ssb
}

fun Context.shareUrl(link: String, subject: String? = null) {
    val intent = Intent(Intent.ACTION_SEND)

    /*This will be the actual content you wish you share.*/

    /*The type of the content is text, obviously.*/
    intent.setType("text/plain")

    /*Applying information Subject and Body.*/
    intent.putExtra(Intent.EXTRA_SUBJECT, subject ?: getString(R.string.share_subject))
    intent.putExtra(Intent.EXTRA_TEXT, link)

    /*Fire!*/
    startActivity(Intent.createChooser(intent, subject ?: getString(R.string.share_subject)))
}

fun String.copyToClipBoard(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(this, this)
    clipboard.setPrimaryClip(clip)
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        Toast.makeText(
            context,
            context.getString(R.string.invite_code_copied_to_clipboard), Toast.LENGTH_SHORT
        ).show()
    }
}

fun AffDay.toAffirmationModel(): Affirmation {
    return Affirmation(
        "", affirmationTextEnglish, affirmationTextGerman, "", createdAt, 0, 0,
        mutableListOf()
    )
}

fun Context.getCopyData():String{
    val clip=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val otp= clip.primaryClip?.description?.label?.toString()?:""
    return otp
}