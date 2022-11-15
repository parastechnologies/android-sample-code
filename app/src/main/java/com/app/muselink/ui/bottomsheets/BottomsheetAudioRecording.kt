package com.app.muselink.ui.bottomsheets

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import com.app.muselink.R
import com.app.muselink.constants.IntentConstant
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.activities.uploadsection.UploadMusicActivity
import com.app.muselink.widgets.uploadwaveform.customAudioViews.SoundFile
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import org.json.JSONObject
import soup.neumorphism.NeumorphCardView
import java.io.File
import java.io.IOException
import java.util.*

class BottomsheetAudioRecording(context: Context) : BottomSheetDialogFragment() {

    var recorder: MediaRecorder? = null
    private var rootView: View? = null
    private var mRecordedSoundFile: SoundFile? = null
    private var mHandler: Handler? = null
    private var tvRecordingDuration: TextView? = null
    private var progressRecord: ProgressBar? = null
    private var btnUploadRecord: NeumorphCardView? = null
    private var imgClose: ImageView? = null
    var IsUploadClick = false

    var runnable: Runnable? = null

    var handler = Handler()

    private var MillisecondTime: Long = 0

    var countValue = 1000


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onStop() {
        mRecordingKeepGoing = false
        tvRecordingDuration?.text = "00:00:00"
        SoundFile.stopRecording()
        handler.removeCallbacks(runnable!!)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IsUploadClick = false
        tvRecordingDuration?.text = "00:00:00"
        mRecordingKeepGoing = false
        mHandler = Handler()
        intiRecoding()

        imgClose?.setOnClickListener {
            stopRecording()
            if (runnable != null) {
                handler.removeCallbacks(runnable!!)
            }
            dismiss()
        }

        btnUploadRecord?.setOnClickListener {
            if (mRecordingKeepGoing) {
                stopRecording()
                IsUploadClick = true
                progressRecord?.visibility = View.VISIBLE
                mRecordingKeepGoing = false
                if (IsUploadClick) {
                    progressRecord?.visibility = View.GONE
                    dismiss()
                    val bundle = bundleOf(
                        IntentConstant.hasRecordedSoundFile to true,
                        IntentConstant.MusicPath to currentFile?.path
                    )
                    val intent = Intent(requireActivity(), UploadMusicActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    mRecordingKeepGoing = false
                    tvRecordingDuration?.text = "00:00:00"
                }
            }
        }
    }


    private var isAudioRecording = false
    private var mRecordingLastUpdateTime: Long = 0
    private var mRecordingKeepGoing = false

    fun getCurrentTime(): Long {
        return System.nanoTime() / 1000000
    }

    private fun intiRecoding() {
        isAudioRecording = true
//        startRecording1()
        startRecording()
        mRecordingLastUpdateTime = getCurrentTime()
        mRecordingKeepGoing = true
    }

    private fun startTimer() {
        runnable = object : Runnable {
            override fun run() {
                MillisecondTime += countValue
                val millis: Long = MillisecondTime % 1000
                val second: Long = MillisecondTime / 1000 % 60
                val minute: Long = MillisecondTime / (1000 * 60) % 60
                val hour: Long = MillisecondTime / (1000 * 60 * 60) % 24
                tvRecordingDuration!!.text = String.format("%02d:%02d:%02d", hour, minute, second)
//                Log.i("recoded_time", "run:" + String.format("%02d:%02d:%02d.%d", hour, minute, second, millis))
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable!!)
    }


    private var mRecordingTime = 0.0



    private fun startRecording1() {
        val listener: SoundFile.ProgressListener = SoundFile.ProgressListener { elapsedTime ->
            val now: Long = getCurrentTime()
            if (now - mRecordingLastUpdateTime > 5) {
                mRecordingTime = elapsedTime
                // Only UI thread can update Views such as TextViews.
                requireActivity().runOnUiThread {
                    val min = (mRecordingTime / 60).toInt()
                    val sec = (mRecordingTime - 60 * min).toFloat()
                    tvRecordingDuration?.text = String.format(
                        Locale.US,
                        "%02d:%05.2f",
                        min,
                        sec
                    )
                }
                mRecordingLastUpdateTime = now
            }
            mRecordingKeepGoing
        }

        // Record the audio stream in a background thread
        val mRecordAudioThread: Thread = object : Thread() {
            override fun run() {
                try {
                    mRecordedSoundFile = SoundFile.record(listener)
                    if (mRecordedSoundFile == null) {
                        val runnable = Runnable { Log.e("error >> ", "sound file null") }
                        mHandler?.post(runnable)
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }

                Log.e("mRecordedSoundFile>> ", "sound file${Gson().toJson(mRecordedSoundFile)}")
                val string = Gson().toJson(mRecordedSoundFile)
                val jsonObject = JSONObject(string)
                if (jsonObject.has("mDecodedBytes")) {
                    val jsonHb = jsonObject.getJSONObject("mDecodedBytes")
                    val bytesArray = jsonHb.getJSONArray("hb").toString().toByteArray()
                    Log.e("bytesArray>> ", "sound file${bytesArray}")

                    //  val bmp = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.size ?: 0)
                }

                val runnable = Runnable {
                    if (IsUploadClick) {
                        progressRecord?.visibility = View.GONE
                        dismiss()
                        SingletonInstances.setRecordedSoundFile(mRecordedSoundFile)
                        val bundle = bundleOf(
                            IntentConstant.hasRecordedSoundFile to true,
                            IntentConstant.MusicPath to mRecordedSoundFile
                        )
                        val intent = Intent(requireActivity(), UploadMusicActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    } else {
                        mRecordingKeepGoing = false
                        tvRecordingDuration?.setText("00:00:00")
                    }
                }
                mHandler?.post(runnable)
            }
        }
        mRecordAudioThread.start()
    }


    private fun stopRecording() {
        if (null != recorder) {
            recorder!!.stop()
            recorder!!.reset()
            recorder!!.release()
            handler.removeCallbacks(runnable!!)
            recorder = null
        }
    }

    private fun getFilename(): String {
        val filepath = context?.getExternalFilesDir(null)
        val file = File(filepath, Environment.DIRECTORY_MUSIC)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".mp3"
    }


    var currentFile: File? = null


    private fun startRecording() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            MediaRecorder()
        } else {
            MediaRecorder()
        }
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        currentFile = File(getFilename())
        recorder!!.setOutputFile(currentFile!!.path)
        recorder!!.setOnErrorListener(errorListener)
        recorder!!.setOnInfoListener(infoListener)
        try {
            recorder!!.prepare()
            recorder!!.start()
            startTimer()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val errorListener = MediaRecorder.OnErrorListener { mr, what, extra ->
        Log.i("MUSE_LINK_DATA", "errorListener: $mr")
    }

    private val infoListener = MediaRecorder.OnInfoListener { mr, what, extra ->

        Log.i("MUSE_LINK_DATA", "infoListener: $mr")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.bottom_sheet_audio_recording, container, false)
        tvRecordingDuration = rootView?.findViewById(R.id.tvRecordingDuration)
        progressRecord = rootView?.findViewById(R.id.progressRecord)
        btnUploadRecord = rootView?.findViewById(R.id.btnUploadRecord)
        imgClose = rootView?.findViewById(R.id.imgClose)
        return rootView
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }

    }

    @Suppress("DEPRECATION")
    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View?>(bottomSheet!!)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.setLayoutParams(layoutParams)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }
}