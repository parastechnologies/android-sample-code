package com.app.muselink.ui.dialogfragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.ui.activities.home.HomeActivity
import kotlinx.android.synthetic.main.dialog_clear_cache.*

class ClearCacheDialog : DialogFragment() {
    private var rootView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rootView = inflater.inflate(R.layout.dialog_clear_cache, container, false)
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
        btnContinue.setOnClickListener {
            dismiss()
            SharedPrefs.clearPreference()
            val intent = Intent(requireActivity(),HomeActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
        tvCancel.setOnClickListener {
            dismiss()
        }
    }
}