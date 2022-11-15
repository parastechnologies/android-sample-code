package com.app.muselink.ui.activities.settings.manageaccount

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.AccountStatusRes
import com.app.muselink.data.modals.responses.ProfileDetailsRes
import com.app.muselink.model.ui.UserDetails
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.listener.OnUpdate
import com.app.muselink.ui.activities.settings.chnagepassword.ActivityChnagePassword
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.bottomsheets.changeEmail.ChangeEmailBottomSheet
import com.app.muselink.ui.bottomsheets.changePhoneNumber.ChangePhoneNumberBottomSheet
import com.app.muselink.ui.bottomsheets.changeUsername.ChangeUsernameBottomSheet
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class ManageAccountViewmodel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var username = ObservableField<String>()
    var phonenumber = ObservableField<String>()
    var email = ObservableField<String>()
    var password = ObservableField<String>()

    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null

    private val requestApi = MutableLiveData<HashMap<String, Any>>()

    private val _getAccountDetails = requestApi.switchMap { requestApi ->
        repository.getProfileDetails(requestApi)
    }

    val getProfileResponse: LiveData<Resource<ProfileDetailsRes>> = _getAccountDetails

    private val requestApiStatus = MutableLiveData<HashMap<String, Any>>()

    private val _setStatus = requestApiStatus.switchMap { requestApi ->
        repository.setAccountStatus(requestApi)
    }

    val statusResponse: LiveData<Resource<AccountStatusRes>> = _setStatus

    fun setupObserversAccountStatus() {

        statusResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data!!.isSuccess()) {
                            showToast(activity, it.data.message)
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

    fun callApiAccountStatus(type: String, status: Int) {
        val requets = HashMap<String, Any>()
        requets.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
//        requets.put(SyncConstants.APIParams.USER_ID.value, "1")
        requets.put(SyncConstants.APIParams.PERMISSION_TYPE.value, type)
        if (type.equals(SyncConstants.AccountStatus.ACCOUNT_STATUS.value)) {
            requets.put(SyncConstants.APIParams.ACCOUNT_STATUS.value, status.toString())
        } else if (type.equals(SyncConstants.AccountStatus.DIRECT_MESSAGE_STATUS.value)) {
            requets.put(SyncConstants.APIParams.DIRECT_MESSAGE_STATUS.value, status.toString())
        } else if (type.equals(SyncConstants.AccountStatus.SOUDN_FILE_STATUS.value)) {
            requets.put(SyncConstants.APIParams.SOUND_FILE_STATUS.value, status.toString())
        }
        requestApiStatus.value = requets
    }


    fun callApiGetAccountDetails() {
        val requets = HashMap<String, Any>()
        requets.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
        requestApi.value = requets
    }

    var userDetails: UserDetails? = null

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
                            setUserDetails()
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
    var userDetailsNavigator: UserDetailsNavigator? = null
    fun setReferenceUserDetails(userDetailsNavigator: UserDetailsNavigator) {
        this.userDetailsNavigator = userDetailsNavigator
    }

    fun onClickChangePassword() {
        val intent = Intent(activity, ActivityChnagePassword::class.java)
        activity.startActivity(intent)
    }

    fun onClickPhoneNumberChange(view: View) {
        ChangePhoneNumberBottomSheet(
            object : OnUpdate {
                override fun onEmailUpdate() {}
                override fun onPhoneUpdate() {
                    phonenumber.set(SharedPrefs.getUser().phone.toString())
                }
                override fun onUsernameUpdate() {}
            }
        ).show((activity as AppCompatActivity).supportFragmentManager, "ChangePhoneNumber")
    }
    fun onClickUserNameChange(view: View) {
        ChangeUsernameBottomSheet(
            object : OnUpdate {
                override fun onEmailUpdate() {}
                override fun onPhoneUpdate() {}
                override fun onUsernameUpdate() {
                    username.set(SharedPrefs.getUser().User_Name.toString())
                }
            }
        ).show((activity as AppCompatActivity).supportFragmentManager, "ChangeUserName")
    }
    fun onClickEmailChange(view: View) {
        ChangeEmailBottomSheet(
            object : OnUpdate {
                override fun onEmailUpdate() {
                    email.set(SharedPrefs.getUser().email.toString())
                }
                override fun onPhoneUpdate() {}
                override fun onUsernameUpdate() {}
            }
        ).show((activity as AppCompatActivity).supportFragmentManager, "ChangeEmail")
    }
    interface UserDetailsNavigator {
        fun userDetails(userDetails: UserDetails)
    }

    private fun setUserDetails() {
        username.set(userDetails?.User_Name)
        phonenumber.set(userDetails?.phone)
        email.set(userDetails?.Email)
        password.set(userDetails?.password)
    }

}