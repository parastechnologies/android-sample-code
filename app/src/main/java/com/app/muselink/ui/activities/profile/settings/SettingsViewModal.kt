package com.app.muselink.ui.activities.profile.settings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.play.core.review.ReviewManagerFactory
import com.app.muselink.R
import com.app.muselink.commonutils.CustomAlert
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.CommonResponse
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.activities.settings.privacypolicy.ActivityPrivacyPolicy
import com.app.muselink.ui.activities.settings.termofuse.ActivityTermOfUse
import com.app.muselink.ui.activities.welcome.WelcomeActivity
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.dialogfragments.ClearCacheDialog
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.activities.selectExplore.SelectExploreActivity
import com.app.muselink.util.*


class SettingsViewModal @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var viewLifecycleOwner: LifecycleOwner? = null
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    private val _logout = requestApi.switchMap { requestApi ->
        repository.logoutUser(requestApi)
    }
    val logoutResponse: LiveData<Resource<CommonResponse>> = _logout
    private val requestApiDeleteAccount = MutableLiveData<HashMap<String, Any>>()

    private val _deleteAccount = requestApiDeleteAccount.switchMap { requestApi ->
        repository.deleteAccount(requestApi)
    }

    val deleteAccountResponse: LiveData<Resource<CommonResponse>> = _deleteAccount

    fun callApiLogout() {
        val request = HashMap<String, Any>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApi.value = request
    }

    fun callApiDeleteAccount() {
        val request = HashMap<String, Any>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApiDeleteAccount.value = request
    }

    fun showAlertDeleteAccount() {
        val customAlert = CustomAlert(activity)
        customAlert.setCustomAlert(activity)
        customAlert.setTitle(activity.getString(R.string.alert))
        customAlert.setCustomMessage(activity.getString(R.string.are_you_sure_you_want_to_delete_account))
        customAlert.setPositiveBtnColor(ContextCompat.getColor(activity, R.color.color_purple_100))
        customAlert.setPositiveBtnCaption(activity.getString(R.string.yes))
        customAlert.setNegativeBtnCaption(activity.getString(R.string.no))
        customAlert.setCustomAlertListener(object : CustomAlert.CustomAlertListener {
            override fun onPositiveBtnClicked() {
                customAlert.cancelDialog()
                callApiDeleteAccount()
            }

            override fun onNegativeBtnClicked() {
                customAlert.cancelDialog()
            }

            override fun onDismiss() {

            }

        })
        customAlert.showAlertDialog(activity)
    }

    fun showAlertLogout() {

        val customAlert = CustomAlert(activity)
        customAlert.setCustomAlert(activity)
        customAlert.setTitle(activity.getString(R.string.alert))
        customAlert.setCustomMessage(activity.getString(R.string.are_you_sure_you_want_to_logout))
        customAlert.setPositiveBtnColor(ContextCompat.getColor(activity, R.color.color_purple_100))
        customAlert.setPositiveBtnCaption(activity.getString(R.string.yes))
        customAlert.setNegativeBtnCaption(activity.getString(R.string.no))
        customAlert.setCustomAlertListener(object : CustomAlert.CustomAlertListener {
            override fun onPositiveBtnClicked() {
                customAlert.cancelDialog()
                callApiLogout()
            }
            override fun onNegativeBtnClicked() {
                customAlert.cancelDialog()
            }
            override fun onDismiss() {}
        })
        customAlert.showAlertDialog(activity)
    }

    fun setupObserversDeleteAccount() {
        deleteAccountResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            SharedPrefs.clearPreference()
                            val intent = Intent(activity, SelectExploreActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NO_HISTORY
                            activity.startActivity(intent)
                            activity.finishAffinity()
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }
    fun setupObserversLogout() {
        logoutResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            diconnectFromFb()
                            clearCookies(activity)
                            SharedPrefs.clearPreference()
                            val intent = Intent(activity, SelectExploreActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NO_HISTORY
                            activity.startActivity(intent)
                            activity.finishAffinity()
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }
    fun onClickClearCache() {
//        val customAlert = CustomAlert(activity)
//        customAlert.setCustomAlert(activity)
//        customAlert.setTitle(activity.getString(R.string.alert))
//        customAlert.setCustomMessage(activity.getString(R.string.are_you_sure_you_want_to_clear_cache))
//        customAlert.setPositiveBtnColor(ContextCompat.getColor(activity, R.color.color_purple_100))
//        customAlert.setPositiveBtnCaption(activity.getString(R.string.yes))
//        customAlert.setNegativeBtnCaption(activity.getString(R.string.no))
//        customAlert.setCustomAlertListener(object : CustomAlert.CustomAlertListener {
//            override fun onPositiveBtnClicked() {
//                customAlert.cancelDialog()
//                showToast(activity, activity.getString(R.string.cache_cleared_successfully))
//            }
//            override fun onNegativeBtnClicked() {
//                customAlert.cancelDialog()
//            }
//            override fun onDismiss(){}
//        })
//        customAlert.showAlertDialog(activity)

        (activity as AppCompatActivity).supportFragmentManager.beginTransaction().add(ClearCacheDialog(), "ClearCache").commit()

    }
    fun onClickContactUs() {

        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = request.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                // There was some problem, continue regardless of the result.
            }
        }
//        val intent = Intent(Intent.ACTION_DIAL)
//        intent.data = Uri.parse("tel:9999999999")
//        activity.startActivity(intent)
    }
    fun onClickTermUsage() {
        activity.intentComponent(ActivityTermOfUse::class.java,null)
    }
    fun onClickAboutUs() {
//        val url = "https://lumenategrowth.com/about/"
//        try {
//            val uri = Uri.parse(url)
//            val i =
//                Intent(Intent.ACTION_VIEW, uri)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            activity.startActivity(i)
//        } catch (e: ActivityNotFoundException) {
//
//        }
        val bundle= bundleOf("screentype" to "setting")
        activity.intentComponent(WelcomeActivity::class.java,bundle)
    }

    fun onClickPrivacyPolicy() {
        activity.intentComponent(ActivityPrivacyPolicy::class.java,null)

    }

}
