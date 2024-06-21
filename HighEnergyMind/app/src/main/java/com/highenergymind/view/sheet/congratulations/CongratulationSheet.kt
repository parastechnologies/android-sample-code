package com.highenergymind.view.sheet.congratulations

import com.highenergymind.R
import com.highenergymind.base.BaseDialogFragment
import com.highenergymind.databinding.CongratulationsBinding

@Suppress("UNREACHABLE_CODE")
class CongratulationSheet : BaseDialogFragment<CongratulationsBinding>() {
    var callBack: (() -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.congratulations
    }

    override fun init() {
        onClick()
    }

    private fun onClick() {
        mBinding.crossIV.setOnClickListener {
            dismiss()
        }
        mBinding.getStartedBtn.setOnClickListener {
            callBack?.invoke()
            dismiss()
        }
    }

}