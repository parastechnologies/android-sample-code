package com.highenergymind.view.activity.trackplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.CustomAffirmationModel
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.ActivityTrackPlayBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.playerServices.AffirmPlayer
import com.highenergymind.playerServices.BackPlayer
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.millisecondsToMMSS
import com.highenergymind.utils.shareUrl
import com.highenergymind.utils.showToast
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.trackDetail.TrackDetailViewModel
import com.highenergymind.view.sheet.settings.SettingsSheet
import com.highenergymind.view.sheet.trackaudiosettings.TrackAudioBottomSheet
import com.highenergymind.view.sheet.trackmusicsheet.TrackMusicSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayTrackActivity : BaseActivity<ActivityTrackPlayBinding>() {
    val viewModel by viewModels<TrackDetailViewModel>()
    private val playerViewModel by viewModels<PlayerViewModel>()
    private lateinit var settingsSheet: SettingsSheet
    private lateinit var trackAudioBottomSheet: TrackAudioBottomSheet
    private lateinit var trackMusicSheet: TrackMusicSheet
    private var isRepeat = false
    private val broadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.cvPlayPause.performClick()
        }
    }


    override fun getLayoutRes() = R.layout.activity_track_play


    override fun initView() {

        fullScreenStatusBar()
        setTool()
        setCollectors()
        getBundleData()
        onClick()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadCast, IntentFilter(getString(R.string.play_pause_intent_filter)))
    }

    private fun setCollectors() {
        binding.apply {
            AffirmPlayer.apply {
                lifecycleScope.launch {
                    durationFlow.collectLatest {
                        seekBar.max = it.toInt() * 1000
                        tvDuration.text = millisecondsToMMSS(it * 1000)
                    }
                }
                lifecycleScope.launch {
                    affChangeFlow.collectLatest {

                        tvAffirmation.startAnimation(
                            AnimationUtils.loadAnimation(
                                this@PlayTrackActivity,
                                R.anim.slide_out_left
                            )
                        )



                        playerViewModel.trackDetail?.affirmationList?.get(it)?.let { af ->
                            tvAffirmation.text =
                                if (ApplicationClass.isEnglishSelected) af.affirmationTextEnglish else af.affirmationTextGerman
                        }
                    }
                }
                lifecycleScope.launch {
                    bufferingFlow.collectLatest {
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
                    currentPosFlow.collectLatest {
                        seekBar.progress = it.toInt()
                        tvCurrentTime.text = millisecondsToMMSS(it)
                    }
                }
                lifecycleScope.launch {
                    isPlayingFlow.collectLatest {
                        lifecycleScope.launch(Dispatchers.IO) {
                            playerViewModel.createNotification(it.not())
                        }
                        if (it) {
                            ivPlayPauseImage.setImageResource(R.drawable.ic_play_on)
                        } else {
                            ivPlayPauseImage.setImageResource(R.drawable.ic_pause_on)
                        }
                    }
                }
            }
        }
    }

    private fun getBundleData() {
        if (intent.hasExtra(AppConstant.TRACK_DATA)) {

            playerViewModel.trackDetail =
                Gson().fromJson(intent.getStringExtra(AppConstant.TRACK_DATA), TrackOb::class.java)
            playerViewModel.customAffirmationModel = CustomAffirmationModel(
                speakerAudio = playerViewModel.trackDetail?.affirmationList?.get(0)?.audioFiles
                    ?: mutableListOf()
            )
            val isSilentDefault=viewModel.sharedPrefs.getTrackChoice()
            playerViewModel.customAffirmationModel.isSilent=isSilentDefault
            if (isSilentDefault) {
                playerViewModel.customAffirmationModel.speakerIndex =
                    playerViewModel.trackDetail?.affirmationList?.get(0)?.audioFiles?.lastIndex ?: 0
            }
            setData()
            viewModel.apply {
                map.clear()
                map[ApiConstant.TRACK_ID] = playerViewModel.trackDetail?.id.toString()
                addRecentApi()
            }
        }
    }

    private fun setData() {

        playerViewModel.apply {

            lifecycleScope.launch {
                AffirmPlayer.errorFlow.collectLatest {
                    showToast(it)
                }
            }
            binding.apply {
                ivBackground.glideImage(trackDetail?.trackThumbnail)
                tvTitle.text = trackDetail?.trackTitle
                customMusicTool.ivFav.setImageResource(if (trackDetail?.isFav == 1) R.drawable.fill_fav_ic else R.drawable.fav_ic)
                initializePlayer()
                lifecycleScope.launch {
                    delay(500)
                    tvAffirmation.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@PlayTrackActivity,
                            R.anim.slide_out_left
                        )
                    )
                    playerViewModel.trackDetail?.affirmationList?.get(0)?.let { af ->
                        tvAffirmation.text =
                            if (ApplicationClass.isEnglishSelected) af.affirmationTextEnglish else af.affirmationTextGerman
                    }
                }
            }
        }
    }


    private fun setTool() {
        binding.customMusicTool.titleTV.text = getString(R.string.track)
    }

    private fun onClick() {
        playerViewModel.apply {

            binding.apply {
                cvPlayPause.setOnClickListener {
                    AffirmPlayer.apply {
                        if (!isVirtualPlaying && isDelayOn) {
                            playPlayer()
                            lifecycleScope.launch(Dispatchers.IO) {
                                playerViewModel.createNotification(false)
                            }
                            ivPlayPauseImage.setImageResource(R.drawable.ic_play_on)
                        } else if (getPlayer()?.isPlaying == true || (getPlayer()?.isPlaying == false && isDelayOn)) {
                            pausePlayer()
                            lifecycleScope.launch(Dispatchers.IO) {
                                playerViewModel.createNotification(true)
                            }
                            ivPlayPauseImage.setImageResource(R.drawable.ic_pause_on)
                        } else {
                            if (getPlayer()?.playbackState == Player.STATE_ENDED) {
                                initializePlayer()
                            } else {
                                playPlayer()
                            }
                        }
                    }
                }
                seekBar.setOnTouchListener { _, _ ->
                    true
                }

                customMusicTool.ivFav.setOnClickListener {
                    if (trackDetail == null) return@setOnClickListener
                    trackDetail?.isFav = if (trackDetail?.isFav == 1) 0 else 1
                    if (trackDetail?.isFav == 1) {
                        customMusicTool.ivFav.setImageResource(R.drawable.fill_fav_ic)
                    } else {
                        customMusicTool.ivFav.setImageResource(R.drawable.fav_ic)
                    }
                    markFavApi(trackDetail!!)
                }
                customizeSessionmeIV.setOnClickListener {
                    settingsSheet = SettingsSheet(customAffirmationModel.copy())
                    settingsSheet.callBack = {
                        if (customAffirmationModel.delay != it.delay) {
                            it.isSpeakerChange = true
                        }
                        customAffirmationModel = it
                        setPlayerChanges()
                    }

                    supportFragmentManager.let {
                        settingsSheet.show(it, "SettingsSheet")
                    }
                }
                volumeSettingsIV.setOnClickListener {
                    trackAudioBottomSheet = TrackAudioBottomSheet(
                        affirmVolume = AffirmPlayer.getPlayer()?.volume ?: 0f,
                        musicVolume = BackPlayer.exoPlayer?.volume ?: 0f
                    )
                    trackAudioBottomSheet.musicVolumeCallBack = {
                        BackPlayer.exoPlayer?.volume = it
                    }
                    trackAudioBottomSheet.affirmVolumeCallBack = {
                        AffirmPlayer.getPlayer()?.volume = it

                    }
                    supportFragmentManager.let {
                        trackAudioBottomSheet.show(it, "TrackAudioBottomSheet")
                    }
                }
                customMusicTool.backIV.setOnClickListener {
                    finish()
                }
                customMusicTool.shareMusicIV.setOnClickListener {
                    val link = AppConstant.SHARE_AFFIRMATION_DEEP_LINK.plus(trackDetail?.id)
                    shareUrl(link)

                }
                ivMusic.setOnClickListener {
                    trackMusicSheet = TrackMusicSheet()
                    trackMusicSheet.callBack = { backAudios ->
                        BackPlayer.initializeBackground(
                            this@PlayTrackActivity,
                            backAudios.backAudio
                        )
                        BackPlayer.exoPlayer?.playWhenReady = true
                    }
                    supportFragmentManager.let {
                        trackMusicSheet.show(it, "")
                    }
                }
                ivRepeat.setOnClickListener {
                    if (isRepeat) {
                        isRepeat = false
                        ivRepeat.imageTintList = ContextCompat.getColorStateList(
                            this@PlayTrackActivity,
                            R.color.content_color
                        )
                        AffirmPlayer.getPlayer()?.repeatMode = Player.REPEAT_MODE_OFF
                        BackPlayer.exoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
                    } else {
                        ivRepeat.imageTintList = ContextCompat.getColorStateList(
                            this@PlayTrackActivity,
                            R.color.bg_color_2
                        )

                        isRepeat = true
                        AffirmPlayer.getPlayer()?.repeatMode = Player.REPEAT_MODE_ALL
                        BackPlayer.exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE
                    }
                    playerViewModel.customAffirmationModel.isRepeatUi = isRepeat
                }
            }
        }
    }

    private fun setPlayerChanges() {
        lifecycleScope.launch {
            playerViewModel.initializePlayer()

        }
    }

    private fun markFavApi(item: TrackOb) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.ID] = item.id
            map[ApiConstant.FAVOURITE] = item.isFav ?: 0
            map[ApiConstant.TYPE] = AppConstant.TYPE_TRACK
            markFav()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AffirmPlayer.pausePlayer()
        playerViewModel.closeNotification()
        unregisterReceiver(broadCast)
    }

}