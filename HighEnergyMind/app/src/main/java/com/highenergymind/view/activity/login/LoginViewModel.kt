package com.highenergymind.view.activity.login

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.highenergymind.R
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ApiService
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.api.getResult
import com.highenergymind.base.BaseViewModel
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.getFirebaseToken
import com.highenergymind.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by developer on 04/03/24
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val apiService: ApiService, val sharedPrefs: SharedPrefs
) : BaseViewModel(context) {
    var fireToken:String=""
    init {
        getFirebaseToken {
            fireToken=it
        }
    }
    val loginResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> = MutableSharedFlow()


    /**
     * Login api
     */
    fun loginApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                {
                    apiService.loginApi(map)
                }, ApiEndPoint.LOGIN
            )
            loginResponse.emit(response)
            isLoading.emit(false)
        }
    }

    val socialLoginResponse: MutableSharedFlow<ResponseResult<ResponseWrapper>> =
        MutableSharedFlow()

    fun socialLoginApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response = getResult(
                {
                    apiService.socialLogin(map)
                }, ApiEndPoint.SOCIAL_LOGIN
            )
            socialLoginResponse.emit(response)
            isLoading.emit(false)
        }
    }

    lateinit var oneTapClient: SignInClient
    val REQ_ONE_TAP = 2

    fun showGoogleLoginSheet(activity: Activity, authorization: Boolean = false) {
        showLoader(true)
        oneTapClient = Identity.getSignInClient(activity)
        val signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setServerClientId(getViewContext().getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(authorization).build()
        ).build()

        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(activity) { result ->
            showLoader(false)
            try {
                activity.startIntentSenderForResult(
                    result.pendingIntent.intentSender, REQ_ONE_TAP, null, 0, 0, 0, null
                )
            } catch (e: IntentSender.SendIntentException) {
                getViewContext().showToast(e.localizedMessage)
                Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }.addOnFailureListener(activity) { e ->
            showLoader(false)
            getViewContext().showToast(e.localizedMessage)
            e.localizedMessage?.let {
                Log.d("TAG", it)
            }
        }
    }


    override fun retry(type: String) {
    }
}