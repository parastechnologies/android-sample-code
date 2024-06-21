package com.highenergymind.view.sheet.deleteaccount

import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.SheetDeleteAccountLayoutBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeleteAccountSheet : BaseBottomSheet<SheetDeleteAccountLayoutBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var callBack: ((Boolean) -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.sheet_delete_account_layout
    }

    override fun init() {
        onClick()
        mBinding.ivImage.glideImage(sharedPrefs.getUserData()?.userImg)
    }

    private fun onClick() {
        mBinding.apply {
            deleteButton.setOnClickListener {
                callBack?.invoke(true)
                dismiss()
            }
            cancelTV.setOnClickListener {
                dialog!!.dismiss()
            }
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
        }
    }

}