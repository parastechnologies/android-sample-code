package com.highenergymind.view.sheet.settings

import com.highenergymind.R
import com.highenergymind.adapter.SpeakerAdapter
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.CustomAffirmationModel
import com.highenergymind.data.MusicModel
import com.highenergymind.databinding.SheetSettingsLayoutBinding
import com.highenergymind.utils.convertSecondsToMMSS

class SettingsSheet(private val customAffirmationModel: CustomAffirmationModel) :
    BaseBottomSheet<SheetSettingsLayoutBinding>() {
    var callBack: ((CustomAffirmationModel) -> Unit)? = null

    val dataList = ArrayList<MusicModel>()
    private lateinit var speakerAdapter: SpeakerAdapter

    override fun getLayoutRes(): Int {
        return R.layout.sheet_settings_layout
    }

    override fun init() {
        onClick()

        setUpData()
    }

    private fun setUpData() {

        setDelay()
        setAffLength()
        setMusicLength()
        speakerAdapter = SpeakerAdapter(
            isSelected = try {
                customAffirmationModel.speakerIndex
            } catch (_: Exception) {
                0
            },
        )
        speakerAdapter.callBack = {
            customAffirmationModel.isSpeakerChange = true
            customAffirmationModel.speakerIndex = it
        }
        mBinding.speakrRV.adapter = speakerAdapter
        val speakersList = customAffirmationModel.speakerAudio.toMutableList()
        speakersList.removeAt(speakersList.size - 1)
        speakerAdapter.submitList(speakersList.toMutableList())
        mBinding.apply {
            if (customAffirmationModel.isSilent) {
                rbSilent.isChecked = true
            } else {
                rbSpoken.isChecked = true
            }
        }

    }

    private fun setDelay() {
        mBinding.apply {
            tvDelayValue.text = "${customAffirmationModel.delay}s"
        }
    }

    private fun onClick() {

        mBinding.apply {
            ctlSilentAff.setOnClickListener {
                rbSilent.isChecked = true
            }
            ctlSpokenAffirm.setOnClickListener {
                rbSpoken.isChecked = true
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbSilent -> {
                        speakerCL.alpha = 0.5f
                        speakerAdapter.isClickAble = false
                        speakerAdapter.isSelected = -1
                        speakerAdapter.notifyDataSetChanged()
                        customAffirmationModel.isSilent = true
                    }

                    R.id.rbSpoken -> {
                        speakerCL.alpha = 1f
                        speakerAdapter.isClickAble = true
                        speakerAdapter.isSelected = try {
                            customAffirmationModel.speakerIndex
                        } catch (_: Exception) {
                            0
                        }
                        speakerAdapter.notifyDataSetChanged()
                        customAffirmationModel.isSilent = false
                    }
                }
            }
//            rbSpoken.isChecked = true
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
            btnAdd.setOnClickListener {
                customAffirmationModel.isCustomizedLength =
                    !(customAffirmationModel.musicLength == 0 && customAffirmationModel.affirmationLength == 0)
                if (customAffirmationModel.isSilent) {
                    customAffirmationModel.speakerIndex =
                        customAffirmationModel.speakerAudio.size - 1
                }
                callBack?.invoke(customAffirmationModel)
                dialog!!.dismiss()
            }

            ivDelayMin.setOnClickListener {
                val delay = customAffirmationModel.delay
                if (delay != 0) {
                    customAffirmationModel.delay -= 1
                    setDelay()
                }
            }
            ivDelayPlus.setOnClickListener {
                val delay = customAffirmationModel.delay
                if (delay != 15) {
                    customAffirmationModel.delay += 1
                    setDelay()
                }
            }

            ivPlusAff.setOnClickListener {
                customAffirmationModel.affirmationLength += 300 /* means 5 minute*/
                setAffLength()

            }
            ivMinAff.setOnClickListener {

                val length = customAffirmationModel.affirmationLength
                if (length != 0) {
                    customAffirmationModel.affirmationLength -= 300 /* means 5 minute*/
                    setAffLength()
                }
            }

            ivMinusMusic.setOnClickListener {

                val length = customAffirmationModel.musicLength
                if (length != 0) {
                    customAffirmationModel.musicLength -= 300 /* means 5 minute*/
                    setMusicLength()
                }
            }
            ivPlusMusic.setOnClickListener {
                customAffirmationModel.musicLength += 300 /* means 5 minute*/
                setMusicLength()
            }
        }
    }

    private fun setMusicLength() {
        mBinding.tvMusicValue.text = convertSecondsToMMSS(customAffirmationModel.musicLength)
    }

    private fun setAffLength() {
        mBinding.tvAffValue.text = convertSecondsToMMSS(customAffirmationModel.affirmationLength)
    }
}
