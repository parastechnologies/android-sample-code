package com.app.muselink.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD

abstract class BaseFragment : Fragment() {
     protected var mContext: Context? = null
    //private var svProgressHUD: SVProgressHUD? = null
    var hud:KProgressHUD?=null
    /**
     * [onAttach]
     * */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hud= KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.0f)

    }

    /**
     * Hide Progress
     * */
    fun hideLoader() {
        if (hud != null && hud!!.isShowing) {
            hud?.dismiss()
        }
    }
    /**
     * Show Progress
     * */
    fun showLoader() {
        hud?.show()
    }
}