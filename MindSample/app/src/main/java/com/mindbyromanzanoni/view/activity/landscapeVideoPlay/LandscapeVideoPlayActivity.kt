package com.mindbyromanzanoni.view.activity.landscapeVideoPlay

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityLandscapeVideoPlayBinding
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.videoOrAudioControls.MediaCache
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandscapeVideoPlayActivity : BaseActivity<ActivityLandscapeVideoPlayBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var videoPosition: Long = 0
    override fun getLayoutRes(): Int {
        return R.layout.activity_landscape_video_play
    }

    override fun initView() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        getIntentData()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.apply {
            ivPlayOrPause.setOnClickListener {
                if (player?.isPlaying == false) {
                    handlingPlayIcon(true)
                } else if (player?.isPlaying == false) {
                    handlingPlayIcon(true)
                }
            }
            videoView.setOnClickListener {
                if (player?.isPlaying == false) {
                    handlingPlayIcon(true)
                }
            }
            ivBack.setOnClickListener {
                if (player?.isPlaying == true) {
                    minimize()
                } else {
                    finishActivity()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        viewModal.mediaController?.releaseMediaController()
    }

    override fun onRestart() {
        super.onRestart()
        if (!isInPictureInPictureMode) {
            binding.ivBack.visible()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.ivBack.gone()
        } else {
            binding.ivBack.visible()
        }
    }

    override fun viewModel() {}
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            viewModal.videoUrl.set(intent.getString(AppConstants.VIDEO_URL).toString())
            videoPosition = intent.getLong(AppConstants.POSITION)
            initPlayer(viewModal.videoUrl.get() ?: "")
        }
    }

    private fun initPlayer(mediaPath: String) {
        player = ExoPlayer.Builder(applicationContext)
            .build()
            .apply { addListener(playerListener) }
        setUri(mediaPath)
        //player?.seekTo(videoPosition)
        binding.videoView.player = player
        binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_prev).visibility =
            View.GONE
        binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_next).visibility =
            View.GONE
        // binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_settings).visibility = View.GONE
    }

    @OptIn(UnstableApi::class)
    private fun setUri(mediaPath: String) {
        val metaData = MediaMetadata.Builder()
            .setTitle("Title")
            .setAlbumTitle("Album")
            .build()
        val mediaMetaData = MediaItem.Builder()
            .setUri(mediaPath.toUri())
            .setMediaMetadata(metaData)
            .build()
        val mediaItem = mediaPath.let { MediaItem.fromUri(it) }
        val mediaSourceFactory =
            ProgressiveMediaSource.Factory(MediaCache.getInstance(context = applicationContext).cacheFactory)
        val mediaSource = mediaItem.let { mediaSourceFactory.createMediaSource(it) }
        mediaSource.let { player?.setMediaSource(it, true) }
        player?.repeatMode = Player.REPEAT_MODE_ALL
        player?.setMediaItem(mediaMetaData)
        player?.prepare()
    }

    private fun minimize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            enterPictureInPictureMode(updatePictureInPictureParams())
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun updatePictureInPictureParams(): PictureInPictureParams {
        // Calculate the aspect ratio of the PiP screen.
        val aspectRatio = Rational(binding.videoView.width, binding.videoView.height)
        // The movie view turns into the picture-in-picture mode.
        val visibleRect = Rect()
        binding.videoView.getGlobalVisibleRect(visibleRect)
        val params = PictureInPictureParams.Builder().setAspectRatio(aspectRatio)
            // Specify the portion of the screen that turns into the picture-in-picture mode.
            // This makes the transition animation smoother.
            .setSourceRectHint(visibleRect)
            // The screen automatically turns into the picture-in-picture mode when it is hidden
            // by the "Home" button.
            .setAutoEnterEnabled(true)
            .build()
        setPictureInPictureParams(params)
        return params
    }

    private fun releasePlayer() {
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }

    private fun pause() {
        player?.playWhenReady = false
    }

    private fun play() {
        player?.playWhenReady = true
    }

    private fun restartPlayer() {
        player?.seekTo(0)
        player?.playWhenReady = true
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_ENDED -> {
                    handlingPlayIcon(false)
                }

                Player.STATE_READY -> {
                    handlingPlayIcon(true)
                }

                Player.STATE_BUFFERING -> {
                    handlingPlayIcon(playStatus = false, isBuffering = true)
                }

                Player.STATE_IDLE -> {}
            }
        }
    }
    private fun handlingPlayIcon(playStatus: Boolean, isBuffering: Boolean = false) {
        binding.apply {
            ivPlayOrPause.gone()
            if (isBuffering) {
                ivPlayOrPause.setBackgroundResource(R.drawable.ic_simple_icon)
                videoProgress.visible()
            } else {
                if (playStatus) {
                    play()
                    ivPlayOrPause.setBackgroundResource(R.drawable.ic_pause_icon)
                } else {
                    pause()
                    ivPlayOrPause.setBackgroundResource(R.drawable.ic_play_icon)
                }
                videoProgress.gone()
            }
        }
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (player?.isPlaying == true) {
                minimize()
            } else {
                finishActivity()
            }
        }
    }
}