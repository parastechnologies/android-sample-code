package com.app.muselink.ui.dialogfragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.app.muselink.R
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.chatactivity.ChatActivity
import com.app.muselink.util.intentComponent
import kotlinx.android.synthetic.main.dialog_got_a_match.*

class MatchDialog : DialogFragment() {
    private var rootView: View? = null
    private var receiverId: String? = ""
    private var receiverName: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rootView = inflater.inflate(R.layout.dialog_got_a_match, container, false)
        receiverId = arguments?.getString(AppConstants.receiverId)
        receiverName = arguments?.getString(AppConstants.receiverName)
        rootView?.findViewById<TextView>(R.id.userName)?.text = receiverName
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
        btnStartChatting.setOnClickListener {
            val bundle = bundleOf(
                AppConstants.MATCH_SCREEN to "",
                AppConstants.receiverId to receiverId,
                AppConstants.receiverName to receiverName
            )
            requireActivity().intentComponent(ChatActivity::class.java, bundle)
        }
        btnSkip.setOnClickListener {
            dismiss()
        }
    }
}

















