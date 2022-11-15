package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import soup.neumorphism.NeumorphButton

class InviteFriendBottomsheet (context: Context) : BottomSheetDialogFragment() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
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

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val contentView = View.inflate(context, R.layout.invite_friend_bottom_sheet, null)
        val imgClose = contentView.findViewById<ImageView>(R.id.imgClose)
        val btnContinue = contentView.findViewById<NeumorphButton>(R.id.btnContinue)

        imgClose.setOnClickListener {
            dismiss()
        }

        btnContinue.setOnClickListener {
            dismiss()
            val share =
                Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            share.putExtra(Intent.EXTRA_SUBJECT, requireActivity()?.getString(R.string.invt_frnd))
            share.putExtra(Intent.EXTRA_TEXT, "http://www.codeofaninja.com")
            requireActivity().startActivity(
                Intent.createChooser(
                    share,
                    requireActivity().getString(R.string.invt_frnd)
                )
            )
        }

        dialog.setContentView(contentView)

    }

}