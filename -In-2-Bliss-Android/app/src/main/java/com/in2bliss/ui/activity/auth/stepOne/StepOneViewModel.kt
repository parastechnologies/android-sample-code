package com.in2bliss.ui.activity.auth.stepOne

import com.bumptech.glide.RequestManager
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.StepOneResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class StepOneViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
    private val requestManager: RequestManager
) : BaseViewModel() {

    var reasonId: String? = null

    val adapter by lazy {
        CategoryAdapter(
            requestManager = requestManager
        )
    }

    private val mutableSignupReasonsResponse by lazy {
        MutableSharedFlow<Resource<StepOneResponse>>()
    }
    val signupReasonsResponse by lazy { mutableSignupReasonsResponse.asSharedFlow() }

    /**
     * Getting signup categories list
     * */
    private fun signUpReason() {
        networkCallIo {
            mutableSignupReasonsResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.signUpReasons()
                },
                apiName = ApiConstant.SIGNUP_REASONS
            )
            mutableSignupReasonsResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.SIGNUP_REASONS -> signUpReason()
        }
    }
}