package com.app.muselink.ui.bottomsheets.changeEmail

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.ChangePasswordRes
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.isEmailValid

class ChangeEmailViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var email = ""
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    private val _changeEmail = requestApi.switchMap { requestApi ->
        repository.changeEmail(requestApi)
    }
    val changeEmailResponse: LiveData<Resource<ChangePasswordRes>> = _changeEmail
    init {
        email = SharedPrefs.getUser().email.toString()
    }
    /**
     * Validations
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        if (email.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_EMAIL)
        } else if (!isEmailValid(email)) {
            formErrors.add(AppConstants.FormErrors.INVALID_EMAIL)
        }
        return formErrors.isEmpty()
    }
    /**
     * Call Api Change Email
     * */
    fun callApiChangeEmail() {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.EMAIL.value] = email
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
            requestApi.value = request
        }
    }

}