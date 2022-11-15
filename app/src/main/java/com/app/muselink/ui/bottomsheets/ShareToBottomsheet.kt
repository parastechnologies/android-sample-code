package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import soup.neumorphism.NeumorphButton

class ShareToBottomsheet(context: Context) : BottomSheetDialogFragment(){

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val contentView = View.inflate(context, R.layout.share_to_bottom_sheet, null)
        val closeShare = contentView.findViewById<ImageView>(R.id.closeShare)
        val btnComment = contentView.findViewById<NeumorphButton>(R.id.btnComment)
        val btnReport = contentView.findViewById<NeumorphButton>(R.id.btnReport)

        btnReport?.setOnClickListener {

        }

        btnComment?.setOnClickListener {

        }

        closeShare.setOnClickListener {
            dismiss()
        }

        dialog.setContentView(contentView)

    }





}