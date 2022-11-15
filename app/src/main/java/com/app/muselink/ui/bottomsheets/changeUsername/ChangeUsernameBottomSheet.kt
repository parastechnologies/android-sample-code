package com.app.muselink.ui.bottomsheets.changeUsername

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.ChangeUsernameBottomsheetBinding
import com.app.muselink.listener.OnUpdate
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.change_username_bottomsheet.*

@AndroidEntryPoint
class ChangeUsernameBottomSheet(var listener: OnUpdate) :
    BaseViewModelDialogFragment<ChangeUsernameBottomsheetBinding, ChangeUserNameViewModel>() {

    /**
     * ChangeUserName ViewModel Class
     * */
    override fun getViewModelClass(): Class<ChangeUserNameViewModel> {
        return ChangeUserNameViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.change_username_bottomsheet
    }

    /**
     * [onViewCreated]
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
     * Observer Change Email Response
     * */
    private fun setupObservers() {
        viewModel?.changeEmailResponse?.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.status.equals("200")) {
                            SharedPrefs.save(
                                AppConstants.PREFS_USER_NAME,
                                edtUserName.text.toString()
                            )
                            listener.onUsernameUpdate()
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