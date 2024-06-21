package com.highenergymind.view.sheet.trackaudiosettings

import android.view.View
import android.widget.SeekBar
import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.SheetTrackAudioSettingsLayoutBinding

class TrackAudioBottomSheet(
    private val isMusic: Boolean = false,
    private var affirmVolume: Float = 0f,
    var musicVolume: Float = 0f
) :
    BaseBottomSheet<SheetTrackAudioSettingsLayoutBinding>() {
    var musicVolumeCallBack: ((Float) -> Unit)? = null
    var affirmVolumeCallBack: ((Float) -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.sheet_track_audio_settings_layout
    }

    override fun init() {
        listeners()
        setVolumes()
        if (isMusic) {
            mBinding.volumeCL.visibility = View.GONE
        }

        mBinding.apply {
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
            getStartedBtn.setOnClickListener {
                dialog!!.dismiss()
            }
        }

    }

    private fun setVolumes() {
        mBinding.apply {
            seekBarAffirm.progress = (affirmVolume * 100).toInt()
            seekBarMusic.progress = (musicVolume * 100).toInt()
        }
    }

    private fun listeners() {
        mBinding.apply {
            seekBarAffirm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    affirmVolumeCallBack?.invoke((progress / 100.0).toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    musicVolumeCallBack?.invoke((progress / 100.0).toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }
}