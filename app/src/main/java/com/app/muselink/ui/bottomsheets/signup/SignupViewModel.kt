package com.app.muselink.ui.bottomsheets.signup

import android.app.Activity
import android.content.Context
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.commonutils.SharedPrefs
import com.ehsanmashhadi.library.view.CountryPicker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.SignUpResponse
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.listener.CountrySelectionNavigator
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.isEmailValid
import com.app.muselink.util.isPasswordValid
import com.app.muselink.util.isPhoneValid
import java.util.*
import kotlin.collections.HashMap

class SignupViewModel @ViewModelInject constructor(
    val repository: ApiRepository,
    activity: Activity) : BaseViewModel(activity) {
    var email = ""
    var password = ""
    var authType = ""
    var phone = ""
    var phoneCode = "+91"
    var deviceToken = ""
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private val _signup = requestApi.switchMap { requestApi ->
        repository.signUpUser(requestApi)
    }
    val signUpResponse: LiveData<Resource<SignUpResponse>> = _signup
    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            deviceToken = task.result.toString()
        })
    }
    /**
     * Signup Api
     * */
    fun callApiSignUp() {
        if (isFormValid()) {
            val request = HashMap<String, String>()
            if (authType == SyncConstants.AuthTypes.PHONE.value) {
                SharedPrefs.save(AppConstants.MOBILE_NUM, "$phoneCode $phone")
                request[SyncConstants.APIParams.PHONE.value] = phoneCode + phone
                request[SyncConstants.APIParams.SIGNUP_TYPE.value] = authType
                request[SyncConstants.APIParams.DEVICE_TYPE.value] = "Android"
                request[SyncConstants.APIParams.DEVICE_TOKEN.value] = deviceToken
                request[SyncConstants.APIParams.COUNTRY_NAME.value] = getCountry()
            } else {
                request[SyncConstants.APIParams.EMAIL.value] = email
                request[SyncConstants.APIParams.PASSWORD.value] = password
                request[SyncConstants.APIParams.SIGNUP_TYPE.value] = authType
                request[SyncConstants.APIParams.DEVICE_TYPE.value] = "Android"
                request[SyncConstants.APIParams.DEVICE_TOKEN.value] = deviceToken
                request[SyncConstants.APIParams.COUNTRY_NAME.value] = getCountry()
            }
            requestApi.value = request
        }
    }
    /**
     * Country Picker
     * */
    var countryPicker: CountryPicker? = null
    fun openCountryPicker(context: Context, countrySelectionNavigator: CountrySelectionNavigator) {
        countryPicker = CountryPicker.Builder(context)
            .showingDialCode(true)
            .showingFlag(true)
            .setPreSelectedCountry("India")
            .enablingSearch(true)
            .setCountrySelectionListener { country ->
                countrySelectionNavigator.onSelectCountry(country)
            }.build()
        countryPicker?.show(context as AppCompatActivity)
    }

    /**
     *Get Country name
     **/
    private  fun getCountry(): String {
        val tm: TelephonyManager =
            activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue: String = tm.networkCountryIso
        return Locale("", countryCodeValue).displayCountry

    }
    /**
     * Validation
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        if (authType == SyncConstants.AuthTypes.PHONE.value) {
            if (phone.isEmpty()) {
                formErrors.add(AppConstants.FormErrors.MISSING_PHONE)
            } else if (!isPhoneValid(phone)) {
                formErrors.add(AppConstants.FormErrors.INVALID_PHONE)
            }
        } else {
            if (email.isEmpty()) {
                formErrors.add(AppConstants.FormErrors.MISSING_EMAIL)
            } else if (!isEmailValid(email)) {
                formErrors.add(AppConstants.FormErrors.INVALID_EMAIL)
            } else if (password.isEmpty()) {
                formErrors.add(AppConstants.FormErrors.MISSING_PASSWORD)
            } else if (!isPasswordValid(password)) {
                formErrors.add(AppConstants.FormErrors.INVALID_PASSWORD)
            }
        }
        return formErrors.isEmpty()
    }
}