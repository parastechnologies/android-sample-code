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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt
class UploadCompleteViewModel @ViewModelInject constructor(
    val repository: ApiRepository, override var activity: Activity) : BaseViewModel(activity) {
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
    fun uploadAudio(bundle: Bundle?) {
        val msgIntent = Intent(activity, UploadAudioServices::class.java)
        if (bundle != null) {
            msgIntent.putExtras(bundle)
            activity.startService(msgIntent)
        }
    }
    fun onDispose() {
        if (job != null) {
            job?.cancel()
        }
    }

}