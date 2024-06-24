package com.in2bliss.data.recorder

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.in2bliss.R
import com.in2bliss.domain.RecordAudioInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

class RecordAudioImpl : RecordAudioInterface {

    private var mediaRecorder: MediaRecorder? = null
    private var audioUri: Uri? = null
    private var isInitialized: Boolean = false
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var recorderProgress = 0L
    private var job: Job? = null

    private val mutableIsRecording by lazy {
        MutableStateFlow(false)
    }
    private val mutableAmplitude by lazy {
        MutableStateFlow(0L)
    }
    private val mutableRecordingProgress by lazy {
        MutableStateFlow(0L)
    }

    /**
     * Record audio
     **/
    override fun startRecordAudio(context: Context) {

        synchronized(this) {

            CoroutineScope(Dispatchers.Main).launch {
                mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else MediaRecorder()

                val calendar = Calendar.getInstance()
                val fileName =
                    "${context.getString(R.string.app_name)}_${calendar.get(Calendar.YEAR)}-${
                        calendar.get(Calendar.MONTH) + 1
                    }-${calendar.get(Calendar.DAY_OF_MONTH)}_${calendar.get(Calendar.HOUR)}:${
                        calendar.get(Calendar.MINUTE)
                    }.aac"

                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                        put(
                            MediaStore.Audio.Media.RELATIVE_PATH,
                            "${Environment.DIRECTORY_MUSIC}/${context.getString(R.string.app_name)}"
                        )
                    }
                    audioUri = context.contentResolver.insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    val file =
                        audioUri?.let { uri ->
                            context.contentResolver.openFileDescriptor(
                                uri,
                                "w"
                            )
                        }
                    mediaRecorder?.setOutputFile(file?.fileDescriptor)

                } else {
                    val dir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                        fileName
                    )
                    if (dir.exists().not()) dir.mkdir()
                    val file = File(dir, fileName)
                    withContext(Dispatchers.IO) {
                        if (!file.exists()) {
                            file.createNewFile()
                        }
                    }
                    audioUri = FileProvider.getUriForFile(
                        context, "com.in2bliss", file
                    )
                    mediaRecorder?.setOutputFile(file)
                }
                mediaRecorder?.prepare()
                isInitialized = true
                mediaRecorder?.start()
                isRecording(
                    isRecording = true
                )
            }
        }
    }

    /**
     * Get amplitude and recorder progress
     * */
    private fun mediaRecorderAmplitude() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {

            launchInMain {
                try {
                    recorderProgress += 200
                    mutableAmplitude.emit(value = mediaRecorder?.maxAmplitude?.toLong() ?: 0L)
                    mutableRecordingProgress.emit(value = recorderProgress)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            runnable?.let { run ->
                if (mutableIsRecording.value) {
                    handler?.postDelayed(run, 200)
                } else handler?.removeCallbacks(run)
            }
        }
        runnable?.run()
    }

    /**
     * Pausing media recorder
     * */
    override fun pauseAudioRecorder() {
        synchronized(this) {
            isRecording(
                isRecording = false
            )
            mediaRecorder?.pause()
        }
    }

    /**
     * Resume audio recorder
     * */
    override fun resumeAudioRecorder() {
        synchronized(this) {
            isRecording(
                isRecording = true
            )
            mediaRecorder?.resume()
        }
    }

    private fun isRecording(
        isRecording: Boolean
    ) {
        launchInMain {
            mutableIsRecording.emit(
                value = isRecording
            )
            mediaRecorderAmplitude()
        }
    }

    /**
     * Stopping media recorder
     * */
    override fun stopAudioRecorder(): Uri? {
        synchronized(this) {
            isRecording(
                isRecording = false
            )
            mediaRecorder?.stop()
            isInitialized = false
            handler = null
            runnable = null
            mediaRecorder?.release()
            return audioUri
        }
    }

    /**
     * Return state flow is recording
     * */
    override fun isRecording(): StateFlow<Boolean> {
        return mutableIsRecording.asStateFlow()
    }

    /**
     * Is media recorder initialized
     * */
    override fun isMediaRecorderInitialized(): Boolean {
        return isInitialized
    }

    /**
     * Amplitude callback
     * */
    override fun getAmplitude(): StateFlow<Long> {
        return mutableAmplitude.asStateFlow()
    }

    /**
     * Recorder progress
     * */
    override fun getRecorderProgress(): StateFlow<Long> {
        return mutableRecordingProgress.asStateFlow()
    }

    /**
     * Launching coroutines in main thread
     * */
    private fun launchInMain(body: suspend () -> Unit) {
        if (job != null) job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            body.invoke()
        }
    }
}

