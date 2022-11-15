package com.app.muselink.ui.bottomsheets.changeUsername

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
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import kotlin.collections.set

class ChangeUserNameViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var email = ""
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null
    private val _changeEmail = requestApi.switchMap { requestApi ->
        repository.changeUserName(requestApi)
    }
    val changeEmailResponse: LiveData<Resource<ChangePasswordRes>> = _changeEmail

    init {
        email = SharedPrefs.getUser().User_Name.toString()
    }

    /**
     * Validations
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        if (email.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.INVALID_USERNAME)
        }
        return formErrors.isEmpty()
    }

    /**
     * Call Forget password Api
     * */
    fun callApiUserPassword() {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.USER_NAME.value] = email
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
            requestApi.value = request
        }
    }
}