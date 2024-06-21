package com.highenergymind.view.sheet.redeemCode

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.base.BaseResponse
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.SheetRedeemCodeLayoutBinding
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.redemcode.RedeemCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Created by developer on 28/02/24
 */
@AndroidEntryPoint
class RedeemCodeSheet : BaseBottomSheet<SheetRedeemCodeLayoutBinding>() {
    val viewModel by viewModels<RedeemCodeViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.sheet_redeem_code_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet.parent.parent.requestLayout()
            }
        }
    }

    override fun init() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setCollectors()
        clicks()
    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                redeemCodeResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        sharedPrefs.saveUserData(response.data!!)
                        dismiss()
                    })
                }
            }
        }
    }

    private fun clicks() {
        mBinding.apply {
            redemCodeBtn.setOnClickListener {
                mBinding.apply {
                    if (codeEdt.text?.trim()?.toString().isNullOrEmpty()) {
                        requireContext().showToast(getString(R.string.please_enter_redeem_code))
                    } else {
                        viewModel.apply {
                            map.clear()
                            map[ApiConstant.CODE] = codeEdt.text?.trim()?.toString() ?: ""
                            redeemCodeApi()
                        }

                    }
                }
            }
            dontHaveCodeTV.setOnClickListener {
                dismiss()
            }
            crossIV.setOnClickListener {
                dismiss()
            }
        }
    }
}