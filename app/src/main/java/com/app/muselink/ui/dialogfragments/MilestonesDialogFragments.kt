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
import com.app.muselink.util.SpringAnimationwithListenerNavigator
import com.app.muselink.util.springAnimationwithListener
import kotlinx.android.synthetic.main.fragment_milestones.*
import kotlinx.android.synthetic.main.fragment_upload_step1.nmcOkStep1

class MilestonesDialogFragments : DialogFragment() {
    private var rootView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rootView = inflater.inflate(R.layout.fragment_milestones, container, false)
        return rootView
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvMessage.text=arguments?.getString("message","")
        tvMessage.springAnimationwithListener(.5f, animationListenerStep1)
        nmcOkStep1.setOnClickListener {
            this.dismiss()
        }
    }
    private val animationListenerStep2 = object : SpringAnimationwithListenerNavigator {
        override fun onEndAnimation() {}
        override fun onStartAnimation() {}
    }
    private val animationListenerStep1 = object : SpringAnimationwithListenerNavigator {
        override fun onEndAnimation() {
            if (nmcOkStep1 != null) {
                nmcOkStep1.visibility = View.VISIBLE
                nmcOkStep1.springAnimationwithListener(.5f, animationListenerStep2)
            }
        }

        override fun onStartAnimation() {}
    }
}