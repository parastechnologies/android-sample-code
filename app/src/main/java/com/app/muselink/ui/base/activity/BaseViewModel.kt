package com.app.muselink.ui.base.activity

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.ViewModel
import com.kaopiz.kprogresshud.KProgressHUD
import javax.inject.Inject

abstract class BaseViewModel(activity: Activity) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    open var activity = activity

    var hud:KProgressHUD?=null
    init {
        hud= KProgressHUD.create(activity)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.0f)
    }

    /**
     * Hide Loader
     * */
    fun hideLoader() {
        if (hud != null && hud!!.isShowing) {
            hud?.dismiss()
        }
    }

    /**
     * Show Loader
     * */
    fun showLoader() {
        hud?.show()
    }

}