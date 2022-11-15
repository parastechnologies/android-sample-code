package com.app.muselink.ui.bottomsheets.forgotpassword

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.ForgotPasswordRes
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.isEmailValid

class ForgotPasswordViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var email = ""
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    private val _forgotPassword = requestApi.switchMap { requestApi ->
        repository.forgotPassword(requestApi)
    }
    val forgotPassResponse: LiveData<Resource<ForgotPasswordRes>> = _forgotPassword

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
     * Api call forget Password
     * */
    fun callApiForgotPassword() {
        if (isFormValid()) {
            val request = HashMap<String, String>()
            request[SyncConstants.APIParams.EMAIL.value] = email
            requestApi.value = request
        }
    }


}