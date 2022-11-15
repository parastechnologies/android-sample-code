package com.app.muselink.ui.activities.uploadsection

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.IntentConstant
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import com.app.muselink.visualizersmooth.CircleBarVisualizerSmooth
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import com.app.muselink.widgets.visualizer.AudioPlayer
import com.app.muselink.widgets.visualizer.CircleLineVisualizer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_skip_uploaded_complete.*
import kotlinx.android.synthetic.main.activity_upload_complete.btnContinueUpload


@AndroidEntryPoint
class SkipUploadedCompleteActivity : BaseActivity() {
    private var isPlay = false
    private var imgPausePlay: ImageView? = null
    private var circleLineVisualizer: CircleBarVisualizerSmooth? = null
    private var exoPlayerAudio: ExoPlayerAudio? = null
    private var mAudioPlayer: AudioPlayer? = null
    override fun getLayout(): Int {
        return R.layout.activity_skip_uploaded_complete
    }
    val viewModel: SkipUploadedCompleteViewModel? by lazy { ViewModelProvider(this).get(SkipUploadedCompleteViewModel::class.java) }

    var musicPath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentData()
        setListeners()
        initView()
    }

    private fun intentData() {
        musicPath = intent.extras?.getString(IntentConstant.MP3_FILE, "")
        tvLink.text= SyncConstants.AUDIO_FILE_TRIM+musicPath
    }

    private fun initView(){
        mAudioPlayer = AudioPlayer()
        circleLineVisualizer = findViewById(R.id.circleVisualizer)
//        circleLineVisualizer?.isDrawLine = true
        exoPlayerAudio = ExoPlayerAudio(this, exoPlayerAudioNavigator, AppConstants.SongType.TRIM.value)
        val llPausePlay = findViewById<LinearLayout>(R.id.llPausePlay)
        imgPausePlay = findViewById(R.id.imgPausePlay)
        llPausePlay?.setOnClickListener {
            if (!isPlay) {
                isPlay = true
                imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause))
                exoPlayerAudio?.startPlayAudio()
                startPlayingAudio()
            } else {
                exoPlayerAudio?.pausePlayAudio()
                isPlay = false
                imgPausePlay?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play_icon))
            }
        }
        initializePlayer()
    }
    /**
     * Visualizer
     * */
    private fun startPlayingAudio() {
        val component = exoPlayerAudio?.getAudioPlayer()?.audioComponent
        val audioSessionId = component?.audioSessionId
        if (audioSessionId != -1) circleLineVisualizer?.setPlayer(audioSessionId!!)
    }
    private fun initializePlayer(){


        exoPlayerAudio?.initSingleSong(SyncConstants.AUDIO_FILE_TRIM+musicPath.toString(),false)
    }
    private fun setListeners() {
        btnContinueUpload.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        copyButton.setOnClickListener {
            setClipboard(tvLink.text.toString())
            showToast(this,"Text Copied")
        }
    }
     private fun setClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
    val exoPlayerAudioNavigator = object : ExoPlayerAudio.ExoPlayerAudioNavigator {
        override fun onSongCompleted() {}
        override fun onPlayerReady() {}
        override fun onNextSongPlayed() {}
        override fun nextSongNotExist() {}
        override fun playPreviousSound() {}
        override fun previousSongNotExist() {}
        override fun repeatSong() {}
        override fun getCurrentSongPos(value: String?) {}
        override fun updateProgress(value: Float) {}
    }

}