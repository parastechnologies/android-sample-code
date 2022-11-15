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

class ShareAccountBottomsheet (context: Context) : BottomSheetDialogFragment(){

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val contentView = View.inflate(context, R.layout.share_account_bottom_sheet, null)

        var btnClose = contentView.findViewById<NeumorphButton>(R.id.btnClose)

        btnClose.setOnClickListener {
            this.dismiss()
        }

        dialog.setContentView(contentView)


    }

}