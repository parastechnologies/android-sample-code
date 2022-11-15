package com.app.muselink.ui.activities.settings.pushnotification

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import androidx.viewbinding.ViewBinding
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.NotificationResponse
import com.app.muselink.data.modals.responses.ProfileDetailsRes
import com.app.muselink.model.ui.UserDetails
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class PushNotificationViewModel@ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var notificationType = ""
    var statusValue = 0

    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null
    private val requestApi = MutableLiveData<HashMap<String, Any>>()

    private val _pushNotification = requestApi.switchMap { requestApi ->
        repository.changeNotificationStatus(requestApi)
    }

    val _pushNotificationRes: LiveData<Resource<NotificationResponse>> = _pushNotification

    fun changeNotificationStatus(){
        val requests = HashMap<String, Any>()
        requests.put(SyncConstants.APIParams.NOTIFICATION_SETTING_TYPE.value, notificationType)
        requests.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
        requests.put(SyncConstants.APIParams.STATUS_VALUE.value, statusValue)
        requestApi.value = requests
    }

    private val requestApiUserDetails = MutableLiveData<HashMap<String, Any>>()

    private val _getAccountDetails = requestApiUserDetails.switchMap { requestApi ->
        repository.getProfileDetails(requestApi)
    }

    val getProfileResponse: LiveData<Resource<ProfileDetailsRes>> = _getAccountDetails


    fun callApiGetAccountDetails() {
        val requets = HashMap<String, Any>()
        requets.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
        requestApiUserDetails.value = requets
    }

    fun setupObservers() {

        getProfileResponse?.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data!!.isSuccess()) {
                            SharedPrefs.saveUser(it.data.data)
                            userDetails = it.data.data
                            userDetailsNavigator?.userDetails(userDetails!!)
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.message)
                    }
                }

                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(activity, it.message)
                }

                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })

    }

    var userDetailsNavigator : UserDetailsNavigator? =null

    fun setReferenceUserDetails(userDetailsNavigator: UserDetailsNavigator){
        this.userDetailsNavigator = userDetailsNavigator
    }


    interface UserDetailsNavigator{
        fun userDetails(userDetails: UserDetails)
    }

    var userDetails : UserDetails? = null


}