package com.app.muselink.ui.bottomsheets.changePhoneNumber

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.ehsanmashhadi.library.view.CountryPicker
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.ChangePasswordRes
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.listener.CountrySelectionNavigator
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.isPhoneValid

class ChangePhoneNumberViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var phone = SharedPrefs.getUser().phone.toString()
    var phoneCode = "+91"
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    val changePhoneNumber = requestApi.switchMap { requestApi ->
        repository.changePhoneNumber(requestApi)
    }
    val changePhoneNumberResponse: LiveData<Resource<ChangePasswordRes>> = changePhoneNumber

    /**
     * Validations
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        if (phone.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_PHONE)
        } else if (!isPhoneValid(phone)) {
            formErrors.add(AppConstants.FormErrors.INVALID_PHONE)
        }
        return formErrors.isEmpty()
    }

    /**
     *Call api for change password
     * */
    fun callApiChangePhoneNumber() {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.PHONE.value] = phone
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
            requestApi.value = request

        }
    }
    /**
     * Country code picker
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
            }
            .build()
        countryPicker?.show(context as AppCompatActivity)
    }
}