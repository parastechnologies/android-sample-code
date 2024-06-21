package com.highenergymind.utils
import android.content.Context
import android.content.Intent
import android.os.Bundle


fun <T> Context.intentComponent(destinationActivity: Class<T>, bundle: Bundle?=null) {
    val intent = Intent(this, destinationActivity)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    this.startActivity(intent)

}