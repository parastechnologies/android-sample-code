package com.in2bliss.domain

import android.content.Context
import android.net.Uri
import com.in2bliss.data.mediaController.MediaControllerImpl
import kotlinx.coroutines.flow.StateFlow

interface BackgroundMusicPlayerInterface {

    var isPlayOrPause: ((isPlaying: Boolean) -> Unit)?
    var playerBuffering: ((isVisible: Boolean) -> Unit)?
    var playerMaxProgress: ((mediaProgress: MediaControllerImpl.MediaProgress) -> Unit)?
    var playerProgress: ((mediaProgress: MediaControllerImpl.MediaProgress) -> Unit)?

    /**
     * Initializing music player
     * @param context
     * */
    fun initializePlayer(
        context: Context
    )

    /**
     * Adding background media
     * */
    fun addBackgroundMedia(
        mediaUri: Uri,
        playWhenReady: Boolean
    )

    /**
     * Play or pause background player
     * @param isPlaying
     * */
    fun playOrPausePlayer(
        isPlaying: Boolean, isRepeat: Boolean = true
    )

    /**
     * Change repeat mode
     * @param isRepeat
     * */
    fun changeRepeatMode(
        isRepeat: Boolean
    )

    /**
     * Background player volume
     * @param volume
     * */
    fun changePlayerVolume(
        volume: Int
    )

    fun getPlayerVolume(): Float

    /**
     * Release player
     * */
    fun releasePlayer()

    /**
     * Change player progress
     * */
    fun changePlayerProgress(
        progress: Long
    )

    /**
     * Returns stateflow of type boolean is music playing
     * */
    fun musicPLayingState(): StateFlow<Boolean>

    /**
     * Returns stateflow of Long music progress state flow
     * */
    fun musicProgress(): StateFlow<MediaControllerImpl.MediaProgress>

    /**
     * Returns music end time state flow
     * */
    fun musicEndTime(): StateFlow<MediaControllerImpl.MediaProgress>

    /**
     * Returns buffering state flow
     * */
    fun musicBuffering(): StateFlow<Boolean>

    fun getPlayBackState(): StateFlow<Int>
}