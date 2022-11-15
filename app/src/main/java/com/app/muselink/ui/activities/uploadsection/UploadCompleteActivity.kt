package com.app.muselink.ui.activities.uploadsection

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.IntentConstant
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.showToast
import com.app.muselink.visualizersmooth.CircleBarVisualizerSmooth
import com.app.muselink.widgets.ViewRecorder
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import com.app.muselink.widgets.visualizer.AudioPlayer
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_SUCCESS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_upload_complete.*
import kotlinx.android.synthetic.main.layout_equilizer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt

@AndroidEntryPoint
class UploadCompleteActivity : BaseActivity() {
    val viewModel: UploadCompleteViewModel? by lazy {
        ViewModelProvider(this).get(UploadCompleteViewModel::class.java)
    }
    var musicPath: String? = ""
    var trimAudio: String = ""
    private var musicPath15Sec: String? = ""
      var count = 0
    var takeScreenshot = true
    private var circleLineVisualizer: CircleBarVisualizerSmooth? = null
    private var exoPlayerAudio: ExoPlayerAudio? = null
    private var mAudioPlayer: AudioPlayer? = null
    private var mViewRecorder = ViewRecorder()

    //
    /**
     * [onCreate]
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentData()
        setListeners()
        observer()
        mAudioPlayer = AudioPlayer()
        circleLineVisualizer = findViewById(R.id.circleVisualizer)
        exoPlayerAudio =
            ExoPlayerAudio(this, exoPlayerAudioNavigator, AppConstants.SongType.TRIM.value)
        initializePlayer()
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.activity_upload_complete
    }

    /**
     * Intent data
     * */
    private fun intentData() {
        musicPath = intent.extras?.getString(IntentConstant.MusicPath, "")
        musicPath15Sec = intent.extras?.getString(IntentConstant.MusicPath15Sec, "")
      }

    /**
     * Take screen-short from visualizer
     * */
    private fun createVideo() {
        startRecord()
    }

    /**
     * Upload observer
     * */
    private fun observer() {
        viewModel?.uploadResponse?.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Handler(mainLooper).post {
                        Toast.makeText(
                            this@UploadCompleteActivity,
                            it.data?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Status.ERROR -> {
                    Handler(mainLooper).post {
                        Toast.makeText(
                            this@UploadCompleteActivity,
                            it.data?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Status.LOADING -> {
                }
            }
        }
    }

    /**
     * Click listener
     * */
    private fun setListeners() {
        btnContinueUpload.setOnClickListener {
            viewModel?.onDispose()
            val intent = Intent(this, SkipUploadedCompleteActivity::class.java)
            intent.putExtra(IntentConstant.MP3_FILE, trimAudio)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Broadcast receiver to upload progress
     * */
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == "Response") {
                val response: String? = intent.getStringExtra("response")
                try {
                    val json = JSONObject(response.toString())
                    if (json.getString("status") == "200") {
                        val dataJson = json.get("data") as JSONObject
                        trimAudio = dataJson.get("Trim_Audio") as String
                        val message = json.get("message").toString()
                        showToast(this@UploadCompleteActivity, message)
                        btnContinueUpload.visibility = View.VISIBLE
                    } else {
                        val message = json.get("message").toString()
                        showToast(this@UploadCompleteActivity, message)
                    }
                } catch (e: java.lang.Exception) {

                }
                //{"status":"400","message":"Your Limit has been Exid So please Wait for one Day"}
                //{"status":"200","message":"Audio File has been Uploaded","data":{"Audio_Id":"7","User_Id":"7","Full_Audio":"Over_the_Horizon.mp3","Trim_Audio":"trimmed.mp3","Description":"uuuuuu","Description_Color":"#73CADC","Notification_Status":"1","Audio_Date":"2021-10-01","Recording_Video":"Recording_Fri Oct 01 18:12:34 GMT+05:30 2021.mp4"},"recordingVideo":"Recording_Fri Oct 01 18:12:34 GMT+05:30 2021.mp4"}
            }
            if (intent.action == "custom-event-name") {
                val percentage: Int = intent.getIntExtra("percentage", 0)
                CoroutineScope(Dispatchers.Main).launch {
                    tvPercentage.text = "$percentage%"
                    dashedProgressView.updateProgress(
                        (percentage * 1.75f).roundToInt(),
                        this@UploadCompleteActivity
                    )

                }
            }

        }
    }

    /**
     * Initialize Player
     * */
    private fun initializePlayer() {
        musicPath15Sec?.let { exoPlayerAudio?.initSingleSong(it, true) }
        CoroutineScope(Dispatchers.Main).launch {
            showDialog()
            delay(1000)
            exoPlayerAudio?.startPlayAudio()
            startPlayingAudio()
            createVideo()
        }
    }

    private fun startRecord() {
        mViewRecorder = ViewRecorder()
        val outMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = windowManager.defaultDisplay
            display.getMetrics(outMetrics)
        }
        mViewRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mViewRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mViewRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mViewRecorder.setVideoSize(outMetrics.widthPixels, outMetrics.heightPixels)
        mViewRecorder.setVideoEncodingBitRate(2000 * 1000)
        mViewRecorder.setVideoFrameRate(60)
        if(Build.VERSION.SDK_INT < 26) {
            mViewRecorder.setOutputFile(makeDirectory().absolutePath)
        }
        else{
            mViewRecorder.setOutputFile(makeDirectory())
        }
        mViewRecorder.setOnErrorListener(mOnErrorListener)
        mViewRecorder.setRecordedView(musicPlayerView)
        try {
            mViewRecorder.prepare()
            mViewRecorder.start()
        }catch(e: IOException) {
            return
        }
    }
    private val mOnErrorListener =
        MediaRecorder.OnErrorListener { _, _, _ ->
           // mViewRecorder.reset()
            mViewRecorder.release()
        }

    @Suppress("DEPRECATION")
    private fun makeDirectory(): File {

        
        val recordedFile =
            File(Environment.getExternalStorageDirectory().path, "MuseLink/Video/recorded.mp4")
        if (recordedFile.exists()) {
            recordedFile.delete()
        }
        val destinationRoot = File(Environment.getExternalStorageDirectory().path, "MuseLink/Video")
        val file = if (!destinationRoot.exists()) {
            destinationRoot.mkdirs()
            File("$destinationRoot/recorded.mp4")
        } else {
            destinationRoot.mkdirs()
            File("$destinationRoot/recorded.mp4")
        }
        return file
    }

    /**
     * Visualizer
     * */
    private fun startPlayingAudio() {
        try {
            val component = exoPlayerAudio?.getAudioPlayer()?.audioComponent
            val audioSessionId = component?.audioSessionId
            if (audioSessionId != -1 || audioSessionId != -2 || audioSessionId != -3)
                circleLineVisualizer?.setPlayer(audioSessionId!!)
        }catch (e:Exception){
            showToast(this,e.localizedMessage.toString())
        }
    }

    /**
     * [ExoPlayerAudio.ExoPlayerAudioNavigator]
     * */
    val exoPlayerAudioNavigator = object : ExoPlayerAudio.ExoPlayerAudioNavigator {
        override fun onSongCompleted() {

            mViewRecorder.release()
             imageAudioVideo()
        }

        override fun onPlayerReady() {}
        override fun onNextSongPlayed() {}
        override fun nextSongNotExist() {}
        override fun playPreviousSound() {}
        override fun previousSongNotExist() {}
        override fun repeatSong() {}
        override fun getCurrentSongPos(value: String?) {}
        override fun updateProgress(value: Float) {}
    }

    @Suppress("DEPRECATION")
    fun imageAudioVideo() {
        val recordedFile =
            File(Environment.getExternalStorageDirectory().path, "MuseLink/Video/merge.mp4")
        if (recordedFile.exists()) {
            recordedFile.delete()
        }
        val videoDestination = if (Build.VERSION.SDK_INT >= 30) {
       //     File(filesDir, "MuseLink/Video")
            File(Environment.getExternalStorageDirectory().path, "MuseLink/Video")

        } else {
            File(Environment.getExternalStorageDirectory().path, "MuseLink/Video")
        }
        if (!videoDestination.exists()) {
            videoDestination.mkdir()
        }
        try {
            //val cmd = "-i " + videoDestination.path + "/recorded.mp4" + " -i " + File(musicPath15Sec!!).path + " -shortest -threads 0 -preset ultrafast -strict -2 " + (videoDestination.path + "/merge.mp4")
            val cmd = "-i " + videoDestination.path + "/recorded.mp4" + " -i " + File(musicPath15Sec!!).path + " -c:v copy -c:a aac " + (videoDestination.path + "/merge.mp4")
            when (FFmpeg.execute(cmd)) {
                RETURN_CODE_SUCCESS -> {
                    hideDialog()
                    intent.putExtra("VideoFile", File(Environment.getExternalStorageDirectory().path, "MuseLink/Video/recorded.mp4").toString())
                    viewModel?.uploadAudio(intent.extras)
                }
                RETURN_CODE_CANCEL -> {
                    hideDialog()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    /**
     * [onResume]
     * */
    override fun onResume() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter().also {
                it.addAction("custom-event-name")
                it.addAction("Response")
            })
        super.onResume()
    }
    /**
     * [onPause]
     * */
    override fun onPause() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        } catch (e: Exception) {
        }
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exoPlayerAudio?.stopPlayer()
        viewModel?.onDispose()
    }
}