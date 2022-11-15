package com.app.muselink.ui.bottomsheets.logintypes

import android.app.Activity
import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.auth_integrations.FacebookHelper
import com.app.muselink.auth_integrations.InstagramActivity
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.RequestCodeConstants
import com.app.muselink.model.responses.LoginResponse
import com.app.muselink.data.modals.responses.SignUpResponse
import com.app.muselink.data.modals.responses.UserDetailModel
import com.app.muselink.listener.DropBoxUserInfoInterface
import com.app.muselink.listener.DropboxInterface
import com.app.muselink.retrofit.Resource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants

class LoginTypesViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity), DropBoxUserInfoInterface {
    companion object{
        var signinDropbox: DropboxInterface?=null
    }
    var facebook: FacebookHelper? = null


    init {
        HomeActivity.dropBoxUserInfoInterface=this

    }
    var faceBookInterface = object : FacebookHelper.FaceBookInterface {
        override fun onFailure(message: String?){}
        override fun showProgress(){}
        override fun hideProgress(){}
        override fun onSuccessFaceBook(userDetailModel: UserDetailModel?) {
            val request = HashMap<String, String>()
            request[SyncConstants.APIParams.SOCIAL_ID.value] =
                userDetailModel?.faceBookId.toString()
            request[SyncConstants.APIParams.SIGNUP_TYPE.value] =
                SyncConstants.AuthTypes.SOCIAL.value
            requestSocialApi.value = request
        }
    }
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private val requestSocialApi = MutableLiveData<HashMap<String, String>>()

    private val _signup = requestSocialApi.switchMap { requestApi ->
        repository.signUpUser(requestApi)
    }
    val signUpResponse: LiveData<Resource<SignUpResponse>> = _signup
    private val _login = requestApi.switchMap { requestApi ->
        repository.loginUser(requestApi)
    }
    val loginResponse: LiveData<Resource<LoginResponse>> = _login
    fun callInstagramLogin(bottomSheetDialog: BottomSheetDialogFragment) {
        SingletonInstances.setBottomSheetDialogInstance(bottomSheetDialog)
        val intent = Intent(activity, InstagramActivity::class.java)
        activity.startActivityForResult(intent, RequestCodeConstants.REQUEST_CODE_INSTAGRAM_LOGIN)
    }
    fun callFacebookLogin(bottomSheetDialog: BottomSheetDialogFragment) {
        SingletonInstances.setBottomSheetDialogInstance(bottomSheetDialog)
        facebook = FacebookHelper(activity,faceBookInterface)
        facebook?.facebookLogin()
    }

    fun callDropboxLogin(bottomSheetDialog: BottomSheetDialogFragment) {
        SingletonInstances.setBottomSheetDialogInstance(bottomSheetDialog)
        signinDropbox!!.onClickLoginDropBox()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCodeConstants.REQUEST_CODE_INSTAGRAM_LOGIN) {
            if (data != null) {
                val userId = data.getStringExtra(AppConstants.INSTAGRAM_SOCIAL_ID)
                val request = HashMap<String, String>()
                request[SyncConstants.APIParams.SOCIAL_ID.value]=userId!!
                request[SyncConstants.APIParams.SIGNUP_TYPE.value]=SyncConstants.AuthTypes.SOCIAL.value
                requestSocialApi.value = request
            }
        } else {
            facebook?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun getUserData(socialId: String, socialType: String, countryName: String) {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.SOCIAL_ID.value] =
            socialId
        request[SyncConstants.APIParams.SIGNUP_TYPE.value] =
            SyncConstants.AuthTypes.SOCIAL.value
        requestSocialApi.value = request

    }
}