package com.highenergymind.view.sheet

import android.os.Bundle
import android.view.View
import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.LayoutPickerBinding


/**
 * Created by Developer on 11/15/23
 */
class PickerSheet(val callBack: (isCamera: Boolean) -> Unit) :
    BaseBottomSheet<LayoutPickerBinding>() {


    override fun getLayoutRes(): Int {
        return R.layout.layout_picker
    }

    override fun init() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicks()

    }

    private fun clicks() {
        mBinding.apply {
            tvCamera.setOnClickListener {
                callBack.invoke(true)
                dismiss()

            }
            tvPhoto.setOnClickListener {
                callBack.invoke(false)
                dismiss()
            }
            tvCancel.setOnClickListener {
                dismiss()
            }
        }

    }
}