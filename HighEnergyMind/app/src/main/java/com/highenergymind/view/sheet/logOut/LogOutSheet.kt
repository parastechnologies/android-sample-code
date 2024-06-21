package com.highenergymind.view.sheet.logOut

import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.LayoutLogOutSheetBinding


/**
 * Created by developer on 12/03/24
 */
class LogOutSheet : BaseBottomSheet<LayoutLogOutSheetBinding>() {
    var callBack: ((Boolean) -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.layout_log_out_sheet
    }

    override fun init() {
        clicks()
    }

    private fun clicks() {
        mBinding.apply {
            crossIV.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                callBack?.invoke(true)
                dismiss()
            }
            tvNo.setOnClickListener {
                callBack?.invoke(false)
                dismiss()
            }
        }
    }
}