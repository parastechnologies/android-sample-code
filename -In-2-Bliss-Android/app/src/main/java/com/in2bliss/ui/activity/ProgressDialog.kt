package com.in2bliss.ui.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.in2bliss.databinding.ItemLoaderBinding

object ProgressDialog {

    private var progress: Dialog? = null

    fun showProgress(
        activity: Activity
    ) {
        if (progress?.isShowing == true) {
            return
        }
        progress = Dialog(activity)
        val view = ItemLoaderBinding.inflate(LayoutInflater.from(activity))
        progress?.setContentView(view.root)
        progress?.setCanceledOnTouchOutside(false)
        progress?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progress?.show()
    }

    fun hideProgress() {
        try {
            if (progress?.isShowing == true) {
                progress?.dismiss()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}