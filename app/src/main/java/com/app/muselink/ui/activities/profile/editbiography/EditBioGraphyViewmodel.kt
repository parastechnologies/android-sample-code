package com.app.muselink.ui.activities.profile.editbiography

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
import com.app.muselink.constants.IntentConstant
import com.app.muselink.data.modals.responses.CommonResponse
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class EditBioGraphyViewmodel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var biniding: ViewBinding? = null
    var lifeCycle: LifecycleOwner? = null
    var enableButton = ObservableField<Boolean>()
    var showLoader = ObservableField<Boolean>()
    var biography = ObservableField<String>()
    var charCounts = ObservableField<String>()


    private val requestApi = MutableLiveData<HashMap<String, Any>>()

    private val _updateBiography = requestApi.switchMap { requestApi ->
        repository.updateBiography(requestApi)
    }

    val formErrors = ObservableArrayList<AppConstants.FormErrors>()

    fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        val charLength = s.toString().length
        charCounts.set(charLength.toString() + "/500")
    }

    fun isFormValid(): Boolean {
        formErrors.clear()
        if (biography.get().isNullOrEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_BIOGRAPHY)
        }
        return formErrors.isEmpty()
    }

    fun getIntentData() {
        enableButton.set(true)
        charCounts.set("0/500")
        val bundle = activity.intent.extras
        if (bundle != null) {
            if (bundle.containsKey(IntentConstant.BIOGRAPHY)) {
                val data = activity.intent.getStringExtra(IntentConstant.BIOGRAPHY)
                if (data.isNullOrEmpty().not()) {
                    biography.set(data)
                    val charLength = biography.get().toString().length
                    charCounts.set(charLength.toString() + "/500")
                }
            }
        }

    }

    val updateBiographyResponse: LiveData<Resource<CommonResponse>> = _updateBiography

    fun setupObserversUpdateBioGraphy() {

        updateBiographyResponse.observe(lifeCycle!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    showLoader.set(false)
                    enableButton.set(true)
                    if (it.data != null) {
                        if (it.data!!.isSuccess()) {
                            showToast(activity, activity.getString(R.string.updated_successfully))
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
                }

                Resource.Status.LOADING -> {
                    showLoader.set(true)
                    enableButton.set(false)
                }
            }
        })

    }

    fun callApiUpdateBiography() {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
//            request.put(SyncConstants.APIParams.USER_ID.value, "32")
            request.put(SyncConstants.APIParams.BIOGRAPHY.value, biography.get().toString())
            requestApi.value = request
        }
    }

}