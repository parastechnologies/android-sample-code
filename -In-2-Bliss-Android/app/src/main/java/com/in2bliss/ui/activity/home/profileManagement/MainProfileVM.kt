package com.in2bliss.ui.activity.home.profileManagement

import androidx.databinding.ObservableField
import com.in2bliss.R
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.MainProfileList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainProfileVM @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface
) : BaseViewModel() {

    val currentStreak = ObservableField("00 Days")
    val totalEntries = ObservableField("00 Days")
    val mainProfileAdapter by lazy {
        MainProfileAdapter()
    }

    private val mutableLogoutResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val logoutResponse by lazy {
        mutableLogoutResponse.asSharedFlow()
    }

    private val mutableDeleteResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val accountDeleteResponse by lazy {
        mutableDeleteResponse.asSharedFlow()
    }

    private fun logout() {
        networkCallIo {
            mutableLogoutResponse.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.logout()
                },
                apiName = ApiConstant.LOGOUT
            )
            mutableLogoutResponse.emit(value = response)
        }
    }

    private fun deleteAccount() {
        networkCallIo {
            mutableDeleteResponse.emit(value = Resource.Loading())
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.deleteAccount()
                },
                apiName = ApiConstant.ACCOUNT_DELETE
            )
            mutableDeleteResponse.emit(value = response)
        }
    }


    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.LOGOUT -> logout()
            ApiConstant.ACCOUNT_DELETE -> deleteAccount()
        }
    }

    val mainProfileList = arrayListOf(
//        MainProfileList(
//            R.drawable.ic_fav_profile,
//            R.string.favourites
//        ),
        MainProfileList(
            R.drawable.ic_download_profile,
            R.string.downloads
        ),
        MainProfileList(
            R.drawable.ic_my_affirmation_profile,
            R.string.my_affirmations
        ),
        MainProfileList(
            R.drawable.ic_profile_details,
            R.string.profile_details
        ),
        MainProfileList(
            R.drawable.ic_notification_setting,
            R.string.notification_settings
        ),
        MainProfileList(
            R.drawable.ic_manage_subscription,
            R.string.manage_subscription
        ),
        MainProfileList(
            R.drawable.ic_contact_us,
            R.string.contact_us
        ),
        MainProfileList(
            R.drawable.ic_about_profile,
            R.string.about_in2bliss
        ),
        MainProfileList(
            R.drawable.ic_rate_app,
            R.string.rate_the_app
        ),
        MainProfileList(
            R.drawable.ic_share_profile,
            R.string.share_in2bliss
        ),
        MainProfileList(
            R.drawable.ic_term_privacy,
            R.string.privacy_policy
        ),
        MainProfileList(
            R.drawable.ic_term_privacy,
            R.string.terms_condition
        ),
        MainProfileList(
            R.drawable.ic_delete_profile,
            R.string.delete_account
        )
    )
}

