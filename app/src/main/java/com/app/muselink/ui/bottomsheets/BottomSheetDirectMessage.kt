package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import com.app.muselink.R
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.chatactivity.ChatActivity
import com.app.muselink.util.intentComponent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDirectMessage : BottomSheetDialogFragment() {
    private var receiverId: String? = ""
    private var receivername: String? = ""
    private var message: String? = ""
    private var rootView: View? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.bottomsheet_direct_message, container, false)
        receiverId = arguments?.getString(AppConstants.receiverId)
        receivername = arguments?.getString(AppConstants.receiverName)
        message = arguments?.getString(AppConstants.message)

        val imgClose: ImageView? =rootView?.findViewById(R.id.imgClose)

        imgClose?.setOnClickListener {
            dismiss()
        }

        val btnSubmit: soup.neumorphism.NeumorphButton? = rootView?.findViewById(R.id.btnSubmit)
        val edtMessage: EditText? = rootView?.findViewById(R.id.edtMessage)
        btnSubmit?.setOnClickListener {
            if (edtMessage?.text?.trim().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter message", Toast.LENGTH_LONG).show()
            } else {
                val bundle = bundleOf(
                    AppConstants.DM to "",
                    AppConstants.receiverId to receiverId,
                    AppConstants.receiverName to receivername,
                    AppConstants.message to edtMessage?.text?.trim().toString()
                )
                requireActivity().intentComponent(ChatActivity::class.java, bundle)
                dismiss()
            }

        }
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

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }
}