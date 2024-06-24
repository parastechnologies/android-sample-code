package com.in2bliss.data.backgroundMusicPlayer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.in2bliss.data.mediaController.MediaControllerImpl
import com.in2bliss.domain.BackgroundMusicPlayerInterface
import com.in2bliss.utils.extension.convertMilliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackgroundMusicPlayerImpl : BackgroundMusicPlayerInterface {

    private var player: ExoPlayer? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var job: Job? = null
    private var playAndPauseJob: Job? = null
    private var bufferingJob: Job? = null

    override var isPlayOrPause: ((isPlaying: Boolean) -> Unit)? = null
    override var playerBuffering: ((isVisible: Boolean) -> Unit)? = null
    override var playerMaxProgress: ((mediaProgress: MediaControllerImpl.MediaProgress) -> Unit)? =
        null
    override var playerProgress: ((mediaProgress: MediaControllerImpl.MediaProgress) -> Unit)? =
        null

    private val mutableIsMusicPlaying by lazy {
        MutableStateFlow(false)
    }
    private val mutableMusicBuffering by lazy {
        MutableStateFlow(false)
    }
    private val mutableProgress by lazy {
        MutableStateFlow(
            value = MediaControllerImpl.MediaProgress(
                seekBarProgress = 0,
                musicProgress = "00:00"
            )
        )
    }
    private val mutableMusicEndTime by lazy {
        MutableStateFlow(
            value = MediaControllerImpl.MediaProgress(
                seekBarProgress = 0,
                musicProgress = "00:00"
            )
        )
    }

    /**
     * Initializing music player
     * @param context
     * */
    override fun initializePlayer(
        context: Context
    ) {
        synchronized(this) {
            player = ExoPlayer.Builder(context)
                .setAudioAttributes(AudioAttributes.DEFAULT, false)
                .build()

            playerListener()
        }
    }

    private fun playerListener() {
        player?.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                Log.e("prpk", "isPlaying $isPlaying")



                if (playAndPauseJob != null) playAndPauseJob?.cancel()
                playAndPauseJob = CoroutineScope(Dispatchers.Main).launch {
                    mutableIsMusicPlaying.emit(
                        value = isPlaying
                    )
                    isPlayOrPause?.invoke(isPlaying)
                }

                launchInMain {
                    playerProgress()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e("exoError", "onPlayerError: ${error.localizedMessage}")
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.e("prpk", "playback state change")

                val isBuffering = when (playbackState) {
                    Player.STATE_BUFFERING -> true
                    else -> false
                }

                if (bufferingJob != null) bufferingJob?.cancel()
                bufferingJob = CoroutineScope(Dispatchers.Main).launch {
                    mutableMusicBuffering.emit(
                        value = isBuffering
                    )
                    playerBuffering?.invoke(isBuffering)
                }

                launchInMain {
                    playerProgress()
                }
            }
        })
    }

    /**
     * Adding background media
     * @param mediaUri audio uri
     * @param playWhenReady play audio when prepared
     * */
    override fun addBackgroundMedia(mediaUri: Uri, playWhenReady: Boolean) {
        synchronized(this) {
            Log.e("prpk", "addBackgroundMedia: adding song")
            addMediaToPlayer(
                mediaUri = mediaUri,
                playWhenReady = playWhenReady
            )
        }
    }

    /**
     * Play or pause background player
     * @param isPlaying
     * */
    override fun playOrPausePlayer(isPlaying: Boolean, isRepeat: Boolean) {
        synchronized(this) {
            if (isPlaying) {
                Log.e("prpk", "playOrPausePlayer()")

                player?.play()
                if (player?.playbackState == Player.STATE_ENDED && isRepeat) {
                    Log.e("prpk", "STATE_ENDED repeat")
                    player?.seekTo(0)
                    player?.playWhenReady = true

                } else {
                }
            } else player?.pause()
        }
    }

    /**
     * Change repeat mode
     * @param isRepeat
     * */
    override fun changeRepeatMode(isRepeat: Boolean) {
        synchronized(this) {
            val repeat = if (isRepeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            player?.repeatMode = repeat
        }
    }

    private suspend fun playerProgress() {

        /** Player end progress */
        val maxProgress = player?.duration ?: 0L
        var endProgress = "00:00"
        convertMilliseconds(
            timeInMilli = maxProgress,
            convertedTime = { hour, minute, second ->
                endProgress = "${
                    if (hour > 0) "${String.format("%02d", hour)}:" else ""
                }${String.format("%02d", minute)}:${String.format("%02d", second)}"
            }
        )

        playerMaxProgress?.invoke(
            MediaControllerImpl.MediaProgress(
                seekBarProgress = maxProgress,
                musicProgress = if (endProgress == "-12:-55") "00:00" else endProgress
            )
        )

        mutableMusicEndTime.emit(
            value = MediaControllerImpl.MediaProgress(
                seekBarProgress = maxProgress,
                musicProgress = if (endProgress == "-12:-55") "00:00" else endProgress
            )
        )

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {

            /** Player end progress */
            CoroutineScope(Dispatchers.Main).launch {

                val progress = player?.currentPosition ?: 0L
                var currentProgress = "00:00"
                convertMilliseconds(
                    timeInMilli = progress,
                    convertedTime = { hour, minute, second ->
                        currentProgress = "${
                            if (hour > 0) "${String.format("%02d", hour)}:" else ""
                        }${String.format("%02d", minute)}:${String.format("%02d", second)}"
                    }
                )

                playerProgress?.invoke(
                    MediaControllerImpl.MediaProgress(
                        seekBarProgress = progress,
                        musicProgress = currentProgress
                    )
                )

                mutableProgress.emit(
                    value = MediaControllerImpl.MediaProgress(
                        seekBarProgress = progress,
                        musicProgress = currentProgress
                    )
                )
            }

            runnable?.let { run ->
                if (player?.isPlaying == true) {
                    handler?.postDelayed(run, 300)
                } else handler?.removeCallbacks(run)
            }
        }
        runnable?.run()
    }

    /**
     * Adding media to player
     * */
    private fun addMediaToPlayer(
        mediaUri: Uri,
        playWhenReady: Boolean
    ) {
        val imageUri = Uri.parse("")
        val mediaMetaData = MediaMetadata.Builder()
            .setTitle("Title")
            .setArtist("Artist")
            .setArtworkUri(imageUri)
            .build()
        val requestMetaData = MediaItem.RequestMetadata.Builder()
            .setMediaUri(mediaUri)
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(mediaUri)
            .setMediaMetadata(mediaMetaData)
            .setRequestMetadata(requestMetaData)
            .build()

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = playWhenReady
    }

    /**
     * Background player volume
     * @param volume
     * */
    override fun changePlayerVolume(volume: Int) {
        synchronized(this) {
            val playerVolume =
                if (volume > 0) (volume.toDouble() / 100.toDouble()).toFloat() else 0f
            player?.volume = playerVolume
            player?.prepare()
        }
    }

    @Synchronized
    override fun getPlayerVolume(): Float {
        return player?.volume ?: 0f

    }

    /**
     * Release player
     * */
    override fun releasePlayer() {
        synchronized(this) {
            player?.stop()
            player?.playWhenReady = false
            player?.release()
        }
    }

    /**
     * Change player progress
     * */
    override fun changePlayerProgress(progress: Long) {
        synchronized(this) {
            player?.seekTo(progress)
        }
    }

    /**
     * Returns stateflow of type boolean is music playing
     * */
    override fun musicPLayingState(): StateFlow<Boolean> {
        return mutableIsMusicPlaying.asStateFlow()
    }

    /**
     * Returns stateflow of Long music progress state flow
     * */
    override fun musicProgress(): StateFlow<MediaControllerImpl.MediaProgress> {
        return mutableProgress.asStateFlow()
    }

    /**
     * Returns music end time state flow
     * */
    override fun musicEndTime(): StateFlow<MediaControllerImpl.MediaProgress> {
        return mutableMusicEndTime.asStateFlow()
    }

    /**
     * Returns buffering state flow
     * */
    override fun musicBuffering(): StateFlow<Boolean> {
        return mutableMusicBuffering.asStateFlow()
    }

    override fun getPlayBackState(): StateFlow<Int> {
        return MutableStateFlow(player?.playbackState ?: 0)
    }

    /**
     * Launching coroutines in main thread
     * */
    private fun launchInMain(body: suspend () -> Unit) {
        if (job != null) job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            ensureActive()
            body.invoke()
        }
    }

}