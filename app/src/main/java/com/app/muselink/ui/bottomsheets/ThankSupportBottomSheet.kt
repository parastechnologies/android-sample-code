package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import soup.neumorphism.NeumorphButton

class ThankSupportBottomSheet(context: Context) : BottomSheetDialogFragment() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val contentView = View.inflate(context, R.layout.thankyou_support_bottomsheet, null)
        val btnContinue = contentView.findViewById<NeumorphButton>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            dismiss()
            context?.startActivity(Intent(context,HomeActivity::class.java))
            (context as Activity).finishAffinity()
        }
        dialog.setContentView(contentView)
    }


}