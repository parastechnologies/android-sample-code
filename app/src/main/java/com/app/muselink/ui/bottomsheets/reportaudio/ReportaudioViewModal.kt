package com.app.muselink.ui.bottomsheets.reportaudio

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.viewbinding.ViewBinding
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.ChangePasswordRes
import com.app.muselink.data.modals.responses.CommonResponse
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants

class ReportaudioViewModal  @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {


    var message = ""
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null

    private val _reportAudio = requestApi.switchMap { requestApi ->
        repository.addReportAudio(requestApi)
    }

    val _reportAudioRes: LiveData<Resource<CommonResponse>> = _reportAudio

    /**
     * Validations
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        if (message.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_MESSAGE)
        }
        return formErrors.isEmpty()
    }

    /**
     * Call Forget password Api
     * */
    fun callApiReport(audioId:String) {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.AUDIO_ID.value] = audioId
            request[SyncConstants.APIParams.MESSAGE.value] = message
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
            requestApi.value = request
        }
    }
}