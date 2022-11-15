package com.app.muselink

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.IntentConstant
import com.app.muselink.retrofit.ApiServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class UploadAudioServices : IntentService {

    constructor() : super("ServiceIntent")

    @Inject
    lateinit var repository: ApiServices
    var musicPath: String? = ""
    var MusicPath15Sec: String? = ""
    var description: String? = ""
    var videoFile: String? = ""
    var descriptionColor: String? = ""
    var arrayListRole: ArrayList<String> = ArrayList()
    var arrayListTags: ArrayList<String> = ArrayList()
    var arrayListGoal: ArrayList<String> = ArrayList()

    var fullAudio: MultipartBody.Part? = null
    var trimAudio: MultipartBody.Part? = null
    var mergeVideo: MultipartBody.Part? = null
    var totalLength: Long = 0L
    var uploadedLength: Long = 0L

    override fun onHandleIntent(intent: Intent?) {
        musicPath = intent?.extras?.getString(IntentConstant.MusicPath, "")
        MusicPath15Sec = intent?.extras?.getString(IntentConstant.MusicPath15Sec, "")
        description = intent?.extras?.getString(IntentConstant.Description, "")
        videoFile = intent?.extras?.getString("VideoFile", "")
        descriptionColor = intent?.extras?.getString(IntentConstant.DescriptionColor, "")
        arrayListRole.addAll(intent?.extras?.getSerializable(IntentConstant.Role) as ArrayList<String>)
        arrayListGoal.addAll(intent.extras?.getSerializable(IntentConstant.Goal) as ArrayList<String>)
        arrayListTags.addAll(intent.extras?.getSerializable(IntentConstant.TAGS) as ArrayList<String>)

        val file = File(musicPath)
        val musicPath15SecFile = File(MusicPath15Sec)
        val videoFilePath = File(videoFile)
        totalLength = file.length() + musicPath15SecFile.length() + videoFilePath.length()
        fullAudio = MultipartBody.Part.createFormData(
            "fullAudio",
            file.name,
            ProgressRequestBody(file, "audio/*")
        )
        trimAudio = MultipartBody.Part.createFormData(
            "trimAudio",
            "${Date().time}_${musicPath15SecFile.name}",
            ProgressRequestBody(musicPath15SecFile, "audio/*")
        )
        mergeVideo = MultipartBody.Part.createFormData(
            "recordingVideo",
            "Recording_${Date().time}_.mp4",
            ProgressRequestBody(videoFilePath, "video/*"))
        val map = HashMap<String, RequestBody>()
        map["userId"] = SharedPrefs.getUser().id!!.toInt().toString().toRequestBody()
        map["description"] = description.toString().toRequestBody()
        map["descriptionColor"] = descriptionColor.toString().toRequestBody()
        for (i in 0 until arrayListRole.size) {
            map["roleId[]"] = toRequestBodyJson(arrayListRole[i])
        }
        for (i in 0 until arrayListGoal.size) {
            map["goalId[]"] = toRequestBodyJson(arrayListGoal[i])
        }

        Log.d("dsasdadsad",arrayListTags.toString())

        for (i in 0 until arrayListTags.size) {
            map["locations[]"] = toRequestBodyJson(arrayListTags[i])
        }
        repository.uploadAudio_Change(map, fullAudio!!, trimAudio!!, mergeVideo!!)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>) {
                    val responseIntent = Intent("Response")
                    responseIntent.putExtra("response", response.body()?.string().toString())
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(responseIntent)
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })

//        repository.uploadAudio_(map, fullAudio!!, trimAudio!!,arrayListRole, arrayListGoal).enqueue(object : Callback<RequestBody> {
//            override fun onResponse(
//                call: Call<RequestBody>,
//                response: Response<RequestBody>) {
//
//                Log.d("Adasdadadad",response.body().toString())
//
//            }
//            override fun onFailure(call: Call<RequestBody>, t: Throwable) {
//                Log.e("","")
//            }
//        })

    }

    fun toRequestBodyJson(value: String): RequestBody {
        return value.toRequestBody("application/json".toMediaTypeOrNull())
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
        private val mTotal: Long
    ) :
        Runnable {
        override fun run() {
            val percent = (100 * mUploaded / mTotal).toInt()
            CoroutineScope(Dispatchers.Main).launch {

                val intent = Intent("custom-event-name");
                intent.putExtra("percentage", percent)
                intent.putExtra("showProgress", percent != 100)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                /*activity.tvPercentage.text = "$percent%"
                activity.dashedProgressView.updateProgress(
                    (percent * 1.75f).roundToInt(),
                    activity
                )*/
            }
        }
    }

}