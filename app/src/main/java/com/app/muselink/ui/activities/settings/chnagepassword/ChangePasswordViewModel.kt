package com.app.muselink.ui.activities.settings.chnagepassword

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import androidx.viewbinding.ViewBinding
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.ChangePasswordRes
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.isPasswordValid
import com.app.muselink.util.showToast

class ChangePasswordViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var oldPasseord = ObservableField<String>()
    var newPasseord = ObservableField<String>()
    var confirmPasseord = ObservableField<String>()
    var enableButton = ObservableField<Boolean>()
    var showLoader = ObservableField<Boolean>()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null
    private val requestApi = MutableLiveData<HashMap<String, Any>>()

    private val chnagePassword = requestApi.switchMap { requestApi ->
        repository.changePassword(requestApi)
    }

    val chnagePasswwordResponse: LiveData<Resource<ChangePasswordRes>> = chnagePassword

    fun setUpObserverChnagePassword(){

        showLoader.set(false)
        enableButton.set(true)

        chnagePasswwordResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    showLoader.set(false)
                    enableButton.set(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            SharedPrefs.clearPreference()
                            showToast(activity,activity.getString(R.string.please_login_again_to_continue))
//                            val intent = Intent(activity, HomeActivity::class.java)
//                            activity.startActivity(intent)
//                            activity.finishAffinity()
                            activity.finish()
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.message)
                    }
                }

                Resource.Status.ERROR -> {
                    showLoader.set(false)
                    enableButton.set(true)
                    showToast(activity, it.message)
                }

                Resource.Status.LOADING -> {
                    showLoader.set(true)
                    enableButton.set(false)
                }
            }
        })

    }

    fun isFormValid(): Boolean {

        formErrors.clear()
        if (oldPasseord.get().isNullOrEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_OLD_PASSWORD)
        } else if (newPasseord.get().isNullOrEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_NEW_PASSWORD)
        }else if (!isPasswordValid(newPasseord.get().toString())) {
            formErrors.add(AppConstants.FormErrors.INVALID_PASSWORD)
        }  else if (confirmPasseord.get().isNullOrEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_CONFIRM_PASSWORD)
        } else if (!confirmPasseord.get().toString().equals(confirmPasseord.get().toString())) {
            formErrors.add(AppConstants.FormErrors.INVALID_NEW_PASSWORD)
        }
        return formErrors.isEmpty()
    }

    fun onClickSubmit(){
        if(isFormValid()){
            callApiChnagePassword()
        }
    }

    fun callApiChnagePassword() {
        val requets = HashMap<String, Any>()
        requets.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
        requets.put(SyncConstants.APIParams.OLD_PASSWORD.value, oldPasseord.get().toString())
        requets.put(SyncConstants.APIParams.NEW_PASSWORD.value, newPasseord.get().toString())
        requets.put(SyncConstants.APIParams.CONFIRM_PASSWORD.value, confirmPasseord.get().toString())
//        requets.put(SyncConstants.APIParams.USER_ID.value, "1")
        requestApi.value = requets
    }

}