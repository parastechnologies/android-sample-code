package com.app.muselink.ui.activities.uploadsection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.Nullable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.UploadAudioServices
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.UploadAudioResponseModel
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import kotlinx.android.synthetic.main.activity_upload_complete.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import kotlin.math.roundToInt


class SkipUploadedCompleteViewModel @ViewModelInject constructor(
    val repository: ApiRepository, override var activity: Activity
) : BaseViewModel(activity) {
    var job: Job? = null
    private val requestApi = MutableLiveData<HashMap<String, RequestBody>>()
    private val _uploadComplete = requestApi.switchMap { requestApi ->
        repository.uploadAudio(requestApi, fullAudio!!, trimAudio!!, arrayListRole, arrayListGoal)
    }
    val uploadResponse: LiveData<Resource<UploadAudioResponseModel>> = _uploadComplete
    var fullAudio: MultipartBody.Part? = null
    var trimAudio: MultipartBody.Part? = null
    var arrayListRole: ArrayList<Int> = ArrayList()
    var arrayListGoal: ArrayList<Int> = ArrayList()
    var totalLength: Long = 0L
    var uploadedLength: Long = 0L

    fun uploadAudio(bundle: Bundle?) {
        val msgIntent = Intent(activity, UploadAudioServices::class.java)
        if (bundle != null) {
            msgIntent.putExtras(bundle)
            activity.startService(msgIntent)
        }
    }

    fun uploadAudio(
        musicPath: String, MusicPath15Sec: String,
        arrayListRole: ArrayList<Int>,
        arrayListGoal: ArrayList<Int>, description: String, descriptionColor: String
    ) {
        val file = File(musicPath)
        val musicPath15SecFile = File(MusicPath15Sec)
        totalLength = file.length() + musicPath15SecFile.length()
        fullAudio = MultipartBody.Part.createFormData(
            "fullAudio",
            file.name,
            ProgressRequestBody(file, "audio/*")
        )
        trimAudio = MultipartBody.Part.createFormData(
            "trimAudio",
            musicPath15SecFile.name,
            ProgressRequestBody(musicPath15SecFile, "audio/*")
        )
        this.arrayListRole.clear()
        this.arrayListRole.addAll(arrayListRole)
        this.arrayListGoal.clear()
        this.arrayListGoal.addAll(arrayListGoal)
        val map = HashMap<String, RequestBody>()
        map["userId"] = SharedPrefs.getUser().id!!.toInt().toString().toRequestBody()
        map["description"] = description.toRequestBody()
        map["descriptionColor"] = descriptionColor.toRequestBody()
        requestApi.value = map
    }

    inner class ProgressRequestBody(private val mFile: File, private val content_type: String) :
        RequestBody() {
        @Nullable
        override fun contentType(): MediaType? {
            return content_type.toMediaTypeOrNull()
        }

        override fun contentLength(): Long {
            return mFile.length()
        }

        override fun writeTo(sink: BufferedSink) {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val `in` = FileInputStream(mFile)
            var uploaded: Long = 0
            try {
                var read: Int
                val handler = Handler(Looper.getMainLooper())
                while (`in`.read(buffer).also { read = it } != -1) {
                    uploaded += read.toLong()
                    uploadedLength += read.toLong()
                    handler.post(ProgressUpdater(uploadedLength, totalLength))
                    sink.write(buffer, 0, read)
                }
            } finally {
                `in`.close()
            }
        }
    }
    inner class ProgressUpdater(
        private val mUploaded: Long,
        private val mTotal: Long) :
        Runnable {
        override fun run() {
            val percent = (100 * mUploaded / mTotal).toInt()
            job = CoroutineScope(Dispatchers.Main).launch {
                activity.tvPercentage.text = "$percent%"
                activity.dashedProgressView.updateProgress((percent * 1.75f).roundToInt(),activity)
            }
        }
    }

    fun onDispose() {
        if (job != null) {
            job?.cancel()
        }
    }

}