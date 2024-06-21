package com.highenergymind.view.activity.playmusic

import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.BackAudios
import com.highenergymind.databinding.ActivityPlayMusicBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.millisecondsToMMSS
import com.highenergymind.utils.shareUrl
import com.highenergymind.utils.showToast
import com.highenergymind.utils.visible
import com.highenergymind.view.sheet.trackaudiosettings.TrackAudioBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayMusicActivity : BaseActivity<ActivityPlayMusicBinding>() {
    val viewModel by viewModels<PlayMusicViewModel>()
    private lateinit var trackAudioBottomSheet: TrackAudioBottomSheet
    private var isRepeat = false

    override fun getLayoutRes(): Int {
        return R.layout.activity_play_music
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        getIntentData()
        onClick()
    }

    private fun getIntentData() {
        binding.customMusicTool.ivFav.gone()
        if (intent.hasExtra(AppConstant.MUSIC_DATA)) {
            viewModel.backAudios = Gson().fromJson(
                intent.getStringExtra(AppConstant.MUSIC_DATA),
                BackAudios::class.java
            )
            setDataOnScreen()
        }
    }

    private fun setDataOnScreen() {
        binding.apply {
            ivBackground.glideImage(viewModel.backAudios?.backTrackImg)
            tvTitle.text = viewModel.backAudios?.backgroundTitle

            viewModel.initializePlayer()
        }
    }

    private fun onClick() {

        viewModel.apply {
            binding.apply {
                volumeIV.setOnClickListener {
                    trackAudioBottomSheet = TrackAudioBottomSheet(true, musicVolume = mediaController?.volume?:0f)
                    trackAudioBottomSheet.musicVolumeCallBack={
                        mediaController?.volume=it
                    }
                    supportFragmentManager.let {
                        trackAudioBottomSheet.show(it, "")
                    }
                }
                customMusicTool.backIV.setOnClickListener {
                    finish()
                }
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) {
                            mediaController?.seekTo(progress.toLong())

                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }
                })

                customMusicTool.shareMusicIV.setOnClickListener {
                    val url = AppConstant.SHARE_MUSIC_DEEP_LINK.plus(backAudios?.id)
                    shareUrl(url,getString(R.string.share_music))
                }
                cvPlayPause.setOnClickListener {
                    if (mediaController?.isPlaying == true) {
                        mediaController?.pause()
                    } else {
                        mediaController?.play()

                    }
                }
                repeatMusicIV.setOnClickListener {
                    if (isRepeat) {
                        isRepeat = false
                        repeatMusicIV.imageTintList = ContextCompat.getColorStateList(
                            this@PlayMusicActivity,
                            R.color.content_color
                        )
                        mediaController?.repeatMode = Player.REPEAT_MODE_OFF
                    } else {
                        repeatMusicIV.imageTintList = ContextCompat.getColorStateList(
                            this@PlayMusicActivity,
                            R.color.bg_color_2
                        )

                        isRepeat = true
                        mediaController?.repeatMode = Player.REPEAT_MODE_ONE
                    }
                }
            }

        }
    }

    private fun setCollectors() {
        binding.apply {


            viewModel.apply {
                lifecycleScope.launch {
                    playerError.collectLatest {
                        showToast(it)
                    }
                }
                lifecycleScope.launch {
                    isBuffering.collectLatest {
                        if (it) {
                            progress.visible()
                            ivPlayPauseImage.gone()
                        } else {
                            progress.gone()
                            ivPlayPauseImage.visible()
                        }
                    }
                }
                lifecycleScope.launch {
                    isPlayingPlayer.collectLatest {
                        ivPlayPauseImage.setImageResource(if (it) R.drawable.ic_play_on else R.drawable.ic_pause_on)
                    }
                }
                lifecycleScope.launch {
                    duration.collectLatest {
                        seekBar.max = it.toInt()
                        tvDuration.text = millisecondsToMMSS(it)
                    }
                }
                lifecycleScope.launch {
                    currentPos.collectLatest {
                        seekBar.progress = it.toInt()
                        tvCurrentTime.text = millisecondsToMMSS(it)

                    }
                }
            }

        }
    }

}