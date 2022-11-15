package com.app.muselink.ui.bottomsheets.signuptypes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
/*import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.base.VideoObject
import com.bytedance.sdk.open.tiktok.share.Share*/
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.auth_integrations.FacebookHelper
import com.app.muselink.auth_integrations.InstagramActivity
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.RequestCodeConstants
import com.app.muselink.data.modals.responses.SignUpResponse
import com.app.muselink.data.modals.responses.UserDetailModel
import com.app.muselink.listener.DropBoxUserInfoInterface
import com.app.muselink.listener.DropboxInterface
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import java.util.*
import kotlin.collections.HashMap

class SignupTypesViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity),DropBoxUserInfoInterface {

    var userDetailModel=UserDetailModel()
    companion object{
        var signinDropbox: DropboxInterface?=null
    }

    var facebook: FacebookHelper? = null

    init {
        HomeActivity.dropBoxUserInfoInterface=this
    }

    var faceBookInterface = object : FacebookHelper.FaceBookInterface {

        override fun onFailure(message: String?) {

        }

        override fun showProgress() {

        }

        override fun hideProgress() {

        }

        override fun onSuccessFaceBook(userDetailModel: UserDetailModel?) {
            val request = HashMap<String, String>()
            request[SyncConstants.APIParams.SOCIAL_ID.value] =
                userDetailModel?.faceBookId.toString()
            request[SyncConstants.APIParams.SIGNUP_TYPE.value] =
                SyncConstants.AuthTypes.SOCIAL.value
            request[SyncConstants.APIParams.COUNTRY_NAME.value] = getCountry()
            requestApi.value = request
        }

    }
    /**
     *Get Country name
     **/
    private fun getCountry(): String {
        val tm: TelephonyManager =
            activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue: String = tm.networkCountryIso
        return Locale("", countryCodeValue).displayCountry
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCodeConstants.REQUEST_CODE_INSTAGRAM_LOGIN) {
            if (data != null) {
                val userId = data.getStringExtra(AppConstants.INSTAGRAM_SOCIAL_ID)
                val accesstoken = data.getStringExtra(AppConstants.INSTAGRAM_ACCESS_TOKEN)
                val request = HashMap<String, String>()
                if (userId != null) {
                    request[SyncConstants.APIParams.SOCIAL_ID.value] = userId
                }
                request[SyncConstants.APIParams.SIGNUP_TYPE.value] =
                    SyncConstants.AuthTypes.SOCIAL.value
                requestApi.value = request
            }
        } else {
            facebook?.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun callSoundCloudSignUp(bottomSheetDialog: BottomSheetDialogFragment) {

      /*  val tiktokOpenApi = TikTokOpenApiFactory.create(activity)

        val request = Share.Request()
//initialize the resource path, please provide absolute path
        val mUri = ArrayList<String>()
//2.build share content for photos/videos into TikTokMediaContent
        val videoObject = VideoObject()
        videoObject.mVideoPaths = mUri
        val content = MediaContent()
        content.mMediaObject = videoObject
        request.mMediaContent = content
//4.start share
        tiktokOpenApi.share(request)*/
        /* SingeltonInstances.setBottomSheetDialogInstance(bottomSheetDialog)
         val intent = Intent(activity, SoundCloudActivity::class.java)
         activity.startActivityForResult(intent, RequestCodeConstants.REQUEST_CODE_SOUND_CLOUD_AUTH)*/
    }

    fun callInstagramLogin(bottomSheetDialog: BottomSheetDialogFragment) {
        SingletonInstances.setBottomSheetDialogInstance(bottomSheetDialog)
        val intent = Intent(activity, InstagramActivity::class.java)
        activity.startActivityForResult(intent, RequestCodeConstants.REQUEST_CODE_INSTAGRAM_LOGIN)
    }

    fun callFacebookLogin(bottomSheetDialog: BottomSheetDialogFragment) {
        facebook = FacebookHelper(activity, faceBookInterface)
        facebook?.facebookLogin()
    }

    fun callDropboxLogin(bottomSheetDialog: BottomSheetDialogFragment) {
        SingletonInstances.setBottomSheetDialogInstance(bottomSheetDialog)
        signinDropbox!!.onClickLoginDropBox()
    }

    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private val _signup = requestApi.switchMap { requestApi ->
        repository.signUpUser(requestApi)
    }

    val signUpResponse: LiveData<Resource<SignUpResponse>> = _signup

    override fun getUserData(socialId: String, socialType: String, countryName: String) {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.SOCIAL_ID.value] = socialId
        request[SyncConstants.APIParams.SIGNUP_TYPE.value] = SyncConstants.AuthTypes.SOCIAL.value
        request[SyncConstants.APIParams.COUNTRY_NAME.value] = getCountry()
        requestApi.value = request
    }
}