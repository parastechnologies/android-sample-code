package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import soup.neumorphism.NeumorphButton

class SuccessBottomSheet(context: Context) : BottomSheetDialogFragment(){

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val contentView = View.inflate(context, R.layout.success_bottomsheet, null)

        val btnContinue = contentView.findViewById<NeumorphButton>(R.id.btnContinue)

        btnContinue.setOnClickListener {
            this.dismiss()
            ThankSupportBottomSheet(requireActivity()).show(requireFragmentManager(), "ThankYouSupport");
        }


        dialog.setContentView(contentView)
    }

}