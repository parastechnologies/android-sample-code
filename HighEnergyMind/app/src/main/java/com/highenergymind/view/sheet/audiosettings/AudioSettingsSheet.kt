package com.highenergymind.view.sheet.audiosettings

import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.SheetsAudiosettingsBinding
import com.highenergymind.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioSettingsSheet : BaseBottomSheet<SheetsAudiosettingsBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.sheets_audiosettings
    }

    override fun init() {
        clicks()
        getChoice()
    }

    private fun getChoice() {
        mBinding.apply {
            val isSilent = sharedPrefs.getTrackChoice()
            if (isSilent) {
                rbSub.isChecked = true
            } else {
                rbSpoken.isChecked = true
            }
        }
    }

    private fun clicks() {
        mBinding.apply {
            rbSpoken.isChecked = true
            ctlSpoken.setOnClickListener {
                rbSpoken.isChecked = true
            }
            ctlSub.setOnClickListener {
                rbSub.isChecked = true
            }
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
            getStartedBtn.setOnClickListener {
                sharedPrefs.saveTrackChoice(rbSub.isChecked)
                dismiss()
            }
        }
    }
}