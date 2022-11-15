package com.app.muselink.ui.bottomsheets.forgotpassword

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.app.muselink.R
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.ForgotPasswordBottomsheetBinding
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordBottomSheet :
    BaseViewModelDialogFragment<ForgotPasswordBottomsheetBinding, ForgotPasswordViewModel>() {

    /**
     * ViewModel Class
     * */
    override fun getViewModelClass(): Class<ForgotPasswordViewModel> {
        return ForgotPasswordViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.forgot_password_bottomsheet
    }

    /**
     * onViewCreated
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        setupObservers()
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        imgClose.setOnClickListener {
            dismiss()
        }
    }

    /**
     * Observer Forget Password Response
     * */
    private fun setupObservers() {
        viewModel?.forgotPassResponse?.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            dismiss()
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


}