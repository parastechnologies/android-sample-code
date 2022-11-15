package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import com.app.muselink.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import soup.neumorphism.NeumorphButton

class UploadAudioBottomSheet : BottomSheetDialogFragment() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int){
        super.setupDialog(dialog, style)
        getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val contentView = View.inflate(context, R.layout.upload_audio_bottomsheet, null)
        dialog.setContentView(contentView)
    }
}