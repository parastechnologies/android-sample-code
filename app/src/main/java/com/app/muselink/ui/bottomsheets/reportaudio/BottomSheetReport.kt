package com.app.muselink.ui.bottomsheets.reportaudio

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
import androidx.annotation.Nullable
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.databinding.BottomsheetReportBinding
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottomsheet_report.*
import kotlinx.android.synthetic.main.bottomsheet_report.imgClose
import kotlinx.android.synthetic.main.change_username_bottomsheet.*

@AndroidEntryPoint
class BottomSheetReport(val audioId :String) : BaseViewModelDialogFragment<BottomsheetReportBinding,ReportaudioViewModal>() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        binding?.audioId = audioId
        imgClose?.setOnClickListener {
            dismiss()
        }
        setupObservers()
    }


    private fun setupObservers() {
        viewModel?._reportAudioRes?.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.status.equals("200")) {
                            showToast(requireActivity(), it.data.message)
                            this.dismiss()
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    showToast(requireActivity(), it.message)
                }
                Resource.Status.LOADING -> {
                    binding?.showloader = true
                    this.dialog?.setCanceledOnTouchOutside(false)
                    this.dialog?.setCancelable(false)
                }
            }
        })
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

    override fun getViewModelClass(): Class<ReportaudioViewModal> {
        return ReportaudioViewModal::class.java
    }

    override fun getLayout(): Int {
        return R.layout.bottomsheet_report
    }
}