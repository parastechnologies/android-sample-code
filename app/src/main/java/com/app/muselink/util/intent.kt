package com.app.muselink.util

import android.R.attr.label
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.app.muselink.R


fun <T> Activity.intentComponent(destinationActivity: Class<T>, bundle: Bundle?) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null)
        intent.putExtras(bundle)
    this.startActivity(intent)
    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Activity.copyToclipBoard(text:String){
    val clipboard: ClipboardManager? = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip: ClipData = ClipData.newPlainText("main", text)
    clipboard?.setPrimaryClip(clip)
}

fun Activity.finishActivity() {
    this.finish()
    this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}