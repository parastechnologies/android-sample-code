package com.app.muselink.ui.dialogfragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.util.SpringAnimationwithListenerNavigator
import com.app.muselink.util.springAnimationSingleXAxis
import com.app.muselink.util.springAnimationwithListener
import kotlinx.android.synthetic.main.fragment_upload_step2.*

class UploadStep2 : DialogFragment(){
    private var rootView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rootView = inflater.inflate(R.layout.fragment_upload_step2, container, false)
        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedPrefs.save(AppConstants.PREFS_MUSIC_TUTORIAL_2,true)
        imgShape3.springAnimationwithListener(.5f,animationListenerStep1)
        nmcOkStep4.setOnClickListener {
            this.dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = this.dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }
    val animationListenerStep2 = object : SpringAnimationwithListenerNavigator {
        override fun onEndAnimation() {
            tv3.visibility = View.VISIBLE
            tv4.visibility = View.VISIBLE
            nmcOkStep4.springAnimationSingleXAxis(.5f)
        }
        override fun onStartAnimation() {
        }
    }
    private val animationListenerStep1 = object : SpringAnimationwithListenerNavigator {
        override fun onEndAnimation() {
            tv1.visibility = View.VISIBLE
            tv2.visibility = View.VISIBLE
            imgStep4.springAnimationwithListener(.5f,animationListenerStep2)
        }
        override fun onStartAnimation() {
        }
    }
}