package com.highenergymind.view.activity.playmusic

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.highenergymind.api.ApiService
import com.highenergymind.base.BaseViewModel
import com.highenergymind.data.BackAudios
import com.highenergymind.data.TrackOb
import com.highenergymind.playerServices.PlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by developer on 07/05/24
 */
@HiltViewModel
class PlayMusicViewModel @Inject constructor(@ApplicationContext context: Context,val apiService: ApiService):BaseViewModel(context){

    var mediaController: MediaController? = null

    var backAudios: BackAudios? = null
    val currentPos by lazy {
        MutableSharedFlow<Long>()
    }
    val duration by lazy {
        MutableSharedFlow<Long>()
    }
    val isPlayingPlayer by lazy {
        MutableSharedFlow<Boolean>()
    }
    val isBuffering by lazy {
        MutableSharedFlow<Boolean>()
    }
    val playerError by lazy {
        MutableSharedFlow<String>()
    }


    private val handler by lazy { Handler(Looper.getMainLooper()) }


    private fun checkPlaybackPosition(delayMs: Long) {
        handler.postDelayed(
            {
                viewModelScope.launch {
                    mediaController?.duration?.let { duration.emit(it) }
                    val currentPosition = mediaController?.currentPosition
                    currentPosition?.let { currentPos.emit(it) }
                    // Update UI based on currentPosition
                    checkPlaybackPosition(delayMs)
                }
            },
            delayMs
        )
    }

    fun initializePlayer() {
        val sessionToken =
            SessionToken(
                getViewContext(),
                ComponentName(getViewContext(), PlaybackService::class.java)
            )


        val controllerFuture =
            MediaController.Builder(getViewContext(), sessionToken).buildAsync()
        controllerFuture.addListener({

            // MediaController is available here with controllerFuture.get()
            mediaController = controllerFuture.get()
            mediaController?.repeatMode = Player.REPEAT_MODE_OFF

            mediaController?.clearMediaItems()
            addListener()


                val mediaItem =
                    MediaItem.Builder()
                        .setMediaId(backAudios?.id.toString())
                        .setUri(backAudios?.backAudio)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtist(backAudios?.backTrackDesc)
                                .setTitle(backAudios?.backgroundTitle)
                                .setArtworkUri(backAudios?.backTrackImg?.toUri())
                                .build()
                        )
                        .build()

                mediaController?.addMediaItem(mediaItem)


            mediaController?.prepare()
            mediaController?.play()
        }, MoreExecutors.directExecutor())
    }

    private fun addListener() {
        mediaController?.addListener(object : Player.Listener {

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)

            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                viewModelScope.launch {
                    isBuffering.emit(false)

                }
                when (playbackState) {
                    Player.STATE_READY -> {
                        viewModelScope.launch {

                            mediaController?.duration?.let { duration.emit(it) }
                        }
                    }

                    Player.STATE_BUFFERING -> {

                        viewModelScope.launch {
                            isBuffering.emit(true)
                        }
                    }
                }
            }



            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)

                viewModelScope.launch {

                    playerError.emit(error.localizedMessage)
                }
            }


            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                viewModelScope.launch {
                    isPlayingPlayer.emit(isPlaying)
                }
                if (isPlaying) {
                    checkPlaybackPosition(20)
                }
            }
        })
    }


    override fun onCleared() {
        super.onCleared()

        mediaController?.stop()
        mediaController?.release()
    }

    override fun retry(type: String) {

    }
}