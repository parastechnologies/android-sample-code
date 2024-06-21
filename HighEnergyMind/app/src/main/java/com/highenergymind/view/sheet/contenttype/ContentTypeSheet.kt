package com.highenergymind.view.sheet.contenttype

import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.SheetContenttypeLayoutBinding
import com.highenergymind.utils.AppConstant

class ContentTypeSheet(private var selectedType: String) :
    BaseBottomSheet<SheetContenttypeLayoutBinding>() {
    var callBack: ((String) -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.sheet_contenttype_layout
    }

    override fun init() {
        onClick()
    }


    private fun onClick() {
        mBinding.apply {
            if (selectedType == AppConstant.TYPE_TRACK) {
                rbTrack.isChecked = true
            } else {
                rbMusic.isChecked = true
            }
            ctlMusic.setOnClickListener {
                rbMusic.isChecked = true
            }
            ctlTrack.setOnClickListener {
                rbTrack.isChecked = true
            }
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
            getStartedBtn.setOnClickListener {
                callBack?.invoke(if (rbTrack.isChecked) AppConstant.TYPE_TRACK else AppConstant.TYPE_AFFIRMATION)
                dialog!!.dismiss()
            }
        }
    }
}