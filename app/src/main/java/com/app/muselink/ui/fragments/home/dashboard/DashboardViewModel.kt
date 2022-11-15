package com.app.muselink.ui.fragments.home.dashboard

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.FavouriteUserRes
import com.app.muselink.databinding.FragmentDashboardBinding
import com.app.muselink.model.responses.SubscriptionStatusRes
import com.app.muselink.model.responses.UploadedLimitStatusResponseModel
import com.app.muselink.model.responses.UploadedSongResponse
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import soup.neumorphism.ShapeType

class DashboardViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity) : BaseViewModel(activity) {
    var binding: FragmentDashboardBinding? = null
    var lifecycleOwner: LifecycleOwner? = null
    var subscriptionStatus: LiveData<Resource<SubscriptionStatusRes>> = MutableLiveData()
    private val requestApiSubscriptionStatus = MutableLiveData<HashMap<String, String>>()
    private val requestApiGetAllSongs = MutableLiveData<HashMap<String, String>>()
    private val requestApiFavourite = MutableLiveData<HashMap<String, Any>>()
    private val uploadLimitHashmap = MutableLiveData<HashMap<String, String>>()
    /**Get Subscription Status Response Observer*/
    private val _subscriptionStatus = requestApiSubscriptionStatus.switchMap { requestApi ->
        repository.subscriptionStatus(requestApi)
    }
    val subscriptionResponse: LiveData<Resource<SubscriptionStatusRes>> = _subscriptionStatus
    /**Get All Uploaded Audio Response Observer*/
    private val _getAllAudioRes = requestApiGetAllSongs.switchMap { requestApi ->
        repository.getAllUploadedSongs(requestApi)
    }
    val uploadedAudioResponse: LiveData<Resource<UploadedSongResponse>> = _getAllAudioRes
    /**Get Upload Limit Response Observer*/
    private val _checkUploadLimitResponse = uploadLimitHashmap.switchMap { requestApi ->
        repository.getUploadLimitStatus(requestApi)
    }
    val checkUploadLimitResponse: LiveData<Resource<UploadedLimitStatusResponseModel>> = _checkUploadLimitResponse
    /**Favourite Song Response Observer*/
    private val _favouriteUser = requestApiFavourite.switchMap { requestApi ->
        repository.favouriteAudio(requestApi)
    }
    private val favouriteUser: LiveData<Resource<FavouriteUserRes>> = _favouriteUser
    init {
        /**Call api for get all songs*/
        val request = HashMap<String, String>()
        if (SharedPrefs.isUserLogin()) {
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        } else {
            request[SyncConstants.APIParams.USER_ID.value] = "0"
        }
        requestApiGetAllSongs.value = request
        /**Check for user is login or not and Call api for Subscription*/
        if (SharedPrefs.isUserLogin()) {
            val hashmapStatus = hashMapOf(SyncConstants.APIParams.USER_ID.value to SharedPrefs.getUser().id.toString())
            requestApiSubscriptionStatus.value = hashmapStatus
            val hashMapUploadLimit = hashMapOf(SyncConstants.APIParams.USER_ID.value to SharedPrefs.getUser().id.toString())
             uploadLimitHashmap.value = hashMapUploadLimit
        }
        /**Initialize Broadcast Receiver for call subscription api*/
        LocalBroadcastManager.getInstance(activity).registerReceiver(callApiBroadcastReceiver(),IntentFilter().also { it.addAction(SUBSCRIPTION) })
    }
    /**OnClick favourite user*/
    fun onClickFav() {
        if (!SharedPrefs.isUserLogin()) {
            val signUpTypesBottomSheet = SignUpTypesBottomSheet()
            signUpTypesBottomSheet.show((activity as AppCompatActivity).supportFragmentManager, "AuthDialog")
            SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
        } else {
            addToFav()
        }
    }
    /**Add to favourite api call*/
    private fun addToFav() {
        val currentItem = SingletonInstances.currentAudioFilePlay
        if (currentItem != null) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.FROM_ID.value] = SharedPrefs.getUser().id.toString()
            request[SyncConstants.APIParams.AUDIO_ID.value] = currentItem.audioId.toString()
            request[SyncConstants.APIParams.TO_ID.value] = currentItem.userId.toString()
            requestApiFavourite.value = request
        }
    }
    /**GetFavourite User Response Observer*/
    fun setObserverFavUser() {
        favouriteUser.observe(lifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            try {
                                SingletonInstances.listAudioFiles!![SingletonInstances.currentAudioFilePlayPos!!].favoriteAudio = 1
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            binding?.npmStar?.setShapeType(ShapeType.PRESSED)
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.data?.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(activity, it.data?.message)
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }
    /**OnDispose Unregister Broadcast Receiver*/
     fun onDispose(){
        try {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(callApiBroadcastReceiver())
        }catch (e:java.lang.Exception){}
    }

    /**Broadcast Receiver fro Call Api's*/
    private fun  callApiBroadcastReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SUBSCRIPTION) {
                val hashmapStatus = hashMapOf(SyncConstants.APIParams.USER_ID.value to SharedPrefs.getUser().id.toString())
                 requestApiSubscriptionStatus.value = hashmapStatus
                val hashmapUploadLimit = hashMapOf(SyncConstants.APIParams.USER_ID.value to SharedPrefs.getUser().id.toString())
                uploadLimitHashmap.value = hashmapUploadLimit
            }
        }
    }
    companion object {
        var UPLOADED_AUDIO_COUNT=0
        var PREMIUM_MAX_UPLOADED_COUNTS=12
        var PREMIUM_MAX_DM_COUNTS=5
        var NON_PREMIUM_MAX_UPLOADED_COUNTS=6
        var NON_PREMIUM_MAX_DM_COUNTS=1
        var DM_COUNT=0
        const val SUBSCRIPTION = "SUBSCRIPTION"
    }
}