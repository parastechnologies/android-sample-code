package com.highenergymind.playerServices

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.highenergymind.data.AffirmationData
import com.highenergymind.data.CustomAffirmationModel
import com.highenergymind.di.ApplicationClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


/**
 * Created by developer on 01/05/24
 */
object AffirmPlayer {
    private var exoPlayer: ExoPlayer? = null
    private var duration: Long = 0L
    val durationFlow by lazy {
        MutableSharedFlow<Long>()
    }
    private var currentAffIndex = 0
    val affChangeFlow by lazy {
        MutableSharedFlow<Int>()
    }

    val isPlayingFlow by lazy {
        MutableSharedFlow<Boolean>()
    }
    var isCostMusicEnded = false
    var isCostAffEnded = false


    private var currentPos = 0L
    val currentPosFlow by lazy {
        MutableSharedFlow<Long>()
    }

    val bufferingFlow by lazy {
        MutableSharedFlow<Boolean>()
    }

    val errorFlow by lazy {
        MutableSharedFlow<String>()
    }

    fun getPlayer() = exoPlayer

    private lateinit var costModel: CustomAffirmationModel

    fun initializeAffirmations(
        context: Context,
        list: List<AffirmationData>,
        customAffirmationModel: CustomAffirmationModel
    ) {
        costModel = customAffirmationModel
        if (costModel.isCustomizedLength && (currentPos / 1000) <= duration && costModel.isSpeakerChange.not()) {
            duration =
                if (costModel.affirmationLength >= costModel.musicLength) costModel.affirmationLength.toLong() else costModel.musicLength.toLong()
            CoroutineScope(Dispatchers.Main).launch {
                durationFlow.emit(duration)
            }
            if (isVirtualPlaying) {
                affCostUntilEnd = duration - currentPos
                playPlayer()
            } else {

            }


        } else {

        }

        exoPlayer?.release()
        exoPlayer = ExoPlayer.Builder(context).build()
        resetDefault()
        duration += 2

        list.forEachIndexed { index, aff ->
            duration += if (ApplicationClass.isEnglishSelected) aff.audioFiles[customAffirmationModel.speakerIndex].affDurationEnglish.toLong() else aff.audioFiles[customAffirmationModel.speakerIndex].affDurationGerman.toLong() + if (index == 0) 0 else customAffirmationModel.delay
            exoPlayer?.addMediaItem(MediaItem.fromUri(if (ApplicationClass.isEnglishSelected) aff.audioFiles[customAffirmationModel.speakerIndex].affEnglish else aff.audioFiles[customAffirmationModel.speakerIndex].affGerman))
        }
        /** in case of customized length**/
        if (costModel.isRepeatUi) {
            exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
            BackPlayer.exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ONE

            if(costModel.isCustomizedLength) {
                duration =
                    if (costModel.affirmationLength >= costModel.musicLength) costModel.affirmationLength.toLong() else costModel.musicLength.toLong()
            }
        } else {
            exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
            BackPlayer.exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
        }
        CoroutineScope(Dispatchers.Main).launch {
            durationFlow.emit(duration)
        }
        addListeners(customAffirmationModel.delay)
        if (currentAffIndex != 0 && customAffirmationModel.isSpeakerChange) {
            currentPos += 2 * 1000
            for (i in 0 until currentAffIndex) {
                currentPos += (if (ApplicationClass.isEnglishSelected) list[i].audioFiles[customAffirmationModel.speakerIndex].affDurationEnglish.toLong() else list[i].audioFiles[customAffirmationModel.speakerIndex].affDurationGerman.toLong() + customAffirmationModel.delay) * 1000
                exoPlayer?.seekToNextMediaItem()
            }
            durationUntilEnd = (duration * 1000) - currentPos
            customAffirmationModel.isSpeakerChange = false
        } else {
            BackPlayer.exoPlayer?.seekTo(0)
        }
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }


    private fun addListeners(delayTime: Int) {

        BackPlayer.exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {

                    }

                    Player.STATE_READY -> {

                    }

                    Player.STATE_ENDED -> {

                    }
                }
            }
        })
        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

                if (isPlaying) {
                    if (currentPos == 0L) {
                        if (costModel.isCustomizedLength.not()) {
                            durationDownTimer(duration * 1000)
                        }
                        if (exoPlayer?.currentMediaItemIndex == 0) {
                            isDelayOn = true
                            exoPlayer?.pause()
                            countDownTimer(2000)
                        }
                    } else {
                        if (costModel.isCustomizedLength.not()) {
                            durationDownTimer(durationUntilEnd)
                        }
                    }
                    if (costModel.isCustomizedLength.not()) {
                        isVirtualPlaying = true
                        CoroutineScope(Dispatchers.IO).launch {
                            isPlayingFlow.emit(true)
                        }
                        BackPlayer.exoPlayer?.play()
                    }
                } else {
                    if (isDelayOn.not() && costModel.isCustomizedLength.not()) {
                        durationTimer?.cancel()
                    }
                }

            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                CoroutineScope(Dispatchers.IO).launch {
                    errorFlow.emit(error.localizedMessage)
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.e("stateChange", "objectClass")
                setBuffering(false)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isVirtualPlaying = false
                        setBuffering(true)
                    }

                    Player.STATE_READY -> {
                        if (affCostUntilEnd == 0L && musicCostUntilEnd == 0L && costModel.isCustomizedLength) {

                            affirmationCustomizationTimer(costModel.affirmationLength * 1000L)
                            musicCustomizationTimer(costModel.musicLength * 1000L)
                            if (costModel.musicLength != 0) {
                                isCostMusicEnded = false
                                BackPlayer.exoPlayer?.play()
                            } else {
                                isCostMusicEnded = true
                            }
                            if (costModel.affirmationLength != 0) {
                                isCostMusicEnded = false
                            } else {
                                exoPlayer?.pause()
                                isCostMusicEnded = true
                            }

                            CoroutineScope(Dispatchers.IO).launch {
                                isPlayingFlow.emit(true)
                            }
                        }
                        Log.e("STATE_READY", "onPlaybackStateChanged: ")
                    }

                    Player.STATE_ENDED -> {
                        if (costModel.isCustomizedLength.not()) {
                            BackPlayer.exoPlayer?.pause()
                            CoroutineScope(Dispatchers.IO).launch {
                                isPlayingFlow.emit(false)
                            }
                        }
                    }
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)

                val latestWindowIndex = exoPlayer?.currentMediaItemIndex
                if (latestWindowIndex != null && currentAffIndex != latestWindowIndex) {
                    currentAffIndex = latestWindowIndex

                    if (latestWindowIndex == 0 && costModel.isCustomizedLength.not()) {
                        durationTimer?.cancel()
                        musicCostTimer?.cancel()
                        countDownTimer?.cancel()
                        affCostTimer?.cancel()
                        currentPos = 0
                        isDelayOn = true
                        BackPlayer.exoPlayer?.seekTo(0)
                        BackPlayer.exoPlayer?.play()
                        exoPlayer?.pause()
                        durationDownTimer(duration * 1000)
                        countDownTimer(2000)
                    } else {
                        isDelayOn = true
                        exoPlayer?.pause()
                        countDownTimer(delayTime * 1000L)
                        if (costModel.isCustomizedLength && latestWindowIndex==0) {
                            affirmationCustomizationTimer(costModel.affirmationLength * 1000L)
                            musicCustomizationTimer(costModel.musicLength * 1000L)
                        }
                    }
                }
            }
        })
    }

    var isDelayOn = false
    private fun setBuffering(b: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            bufferingFlow.emit(b)
        }
    }

    var millisUntilEnd = 0L
    private var countDownTimer: CountDownTimer? = null
    private fun countDownTimer(sec: Long = 5000) {
        if (isCostAffEnded.not()) {
            countDownTimer = object : CountDownTimer(sec, 200) {
                override fun onTick(millisUntilFinished: Long) {
                    millisUntilEnd = millisUntilFinished
                }

                override fun onFinish() {
                    millisUntilEnd = 0L
                    isDelayOn = false
                    CoroutineScope(Dispatchers.Main).launch {
                        affChangeFlow.emit(currentAffIndex)
                    }
                    durationTimer?.cancel()
                    exoPlayer?.play()
                }
            }
            countDownTimer?.start()
        }
    }

    var durationUntilEnd = 0L
    private var durationTimer: CountDownTimer? = null
    private fun durationDownTimer(duration: Long) {
        durationTimer = object : CountDownTimer(duration, 200) {
            override fun onTick(millisUntilFinished: Long) {
                durationUntilEnd = millisUntilFinished
                CoroutineScope(Dispatchers.IO).launch {
                    currentPos += 200
                    currentPosFlow.emit(currentPos)
                    Log.e("Timer", "onTick: ${currentPos}")

                }
            }

            override fun onFinish() {

            }
        }
        durationTimer?.start()
    }

    private fun resetDefault() {
        exoPlayer?.clearMediaItems()
        duration = 0L
        currentPos = 0L
        affCostUntilEnd = 0L
        musicCostUntilEnd = 0L
        durationUntilEnd = 0L
        isVirtualPlaying = false

        isCostAffEnded = false
        isCostMusicEnded = false
        if (costModel.isSpeakerChange.not()) {
            currentAffIndex = 0
        }
        countDownTimer?.cancel()
        affCostTimer?.cancel()
        musicCostTimer?.cancel()
        durationTimer?.cancel()
    }

    var affCostTimer: CountDownTimer? = null
    var affCostUntilEnd = 0L

    private fun affirmationCustomizationTimer(duration: Long) {
        affCostTimer = object : CountDownTimer(duration, 200) {
            override fun onTick(millisUntilFinished: Long) {
                affCostUntilEnd = millisUntilFinished

                if (costModel.affirmationLength >= costModel.musicLength) {
                    CoroutineScope(Dispatchers.IO).launch {
                        currentPos += 200
                        currentPosFlow.emit(currentPos)
                    }
                }
            }

            override fun onFinish() {
                affCostUntilEnd = 0
                isCostAffEnded = true
                exoPlayer?.pause()
                if (isCostAffEnded && isCostMusicEnded) {
                    CoroutineScope(Dispatchers.IO).launch {
                        isPlayingFlow.emit(false)
                    }
                }
            }
        }
        affCostTimer?.start()

    }

    var musicCostTimer: CountDownTimer? = null
    var musicCostUntilEnd = 0L

    private fun musicCustomizationTimer(duration: Long) {

        musicCostTimer = object : CountDownTimer(duration, 200) {
            override fun onTick(millisUntilFinished: Long) {
                musicCostUntilEnd = millisUntilFinished
                if (costModel.affirmationLength < costModel.musicLength) {
                    CoroutineScope(Dispatchers.IO).launch {
                        currentPos += 200
                        currentPosFlow.emit(currentPos)
                    }
                }
            }

            override fun onFinish() {
                isCostMusicEnded = true
                musicCostUntilEnd = 0
                BackPlayer.exoPlayer?.pause()
                if (isCostAffEnded && isCostMusicEnded) {
                    CoroutineScope(Dispatchers.IO).launch {
                        isPlayingFlow.emit(false)
                    }
                }

            }
        }
        musicCostTimer?.start()

    }

    var isVirtualPlaying: Boolean = false

    fun pausePlayer() {
        isVirtualPlaying = false
        if (isDelayOn) {
            countDownTimer?.cancel()
        }
        durationTimer?.cancel()
        if (costModel.isCustomizedLength) {
            CoroutineScope(Dispatchers.IO).launch {
                isPlayingFlow.emit(false)
            }
            affCostTimer?.cancel()
            musicCostTimer?.cancel()

        }
        BackPlayer.exoPlayer?.pause()
        exoPlayer?.pause()
//        currentPos += 100
    }

    fun playPlayer() {
        isVirtualPlaying = true
        if (isCostMusicEnded.not()) {
            BackPlayer.exoPlayer?.play()
        }
        if (isDelayOn) {
            countDownTimer(millisUntilEnd)
            if (costModel.isCustomizedLength.not()) {
                durationDownTimer(durationUntilEnd)
            } else {
                affirmationCustomizationTimer(affCostUntilEnd)
                musicCustomizationTimer(musicCostUntilEnd)
            }
        } else {
            if (costModel.isCustomizedLength) {
                CoroutineScope(Dispatchers.IO).launch {
                    isPlayingFlow.emit(true)
                }
                if (isCostAffEnded.not()) {
                    exoPlayer?.play()
                }

                affirmationCustomizationTimer(affCostUntilEnd)
                musicCustomizationTimer(musicCostUntilEnd)
            } else {
                exoPlayer?.play()
            }
        }
    }
}