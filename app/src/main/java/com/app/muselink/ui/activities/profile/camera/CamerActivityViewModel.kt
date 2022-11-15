package com.app.muselink.ui.activities.profile.camera

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import androidx.viewbinding.ViewBinding
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.UploadProfileImageRes
import com.app.muselink.retrofit.RequestBodyRetrofit
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CamerActivityViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var biniding: ViewBinding? = null
    var lifeCycle: LifecycleOwner? = null


    private val requestApi = MutableLiveData<HashMap<String, RequestBody>>()


    private val uploadSelctedImage = requestApi.switchMap { requestApi ->
        repository.updateUserProfileImage(requestApi,file!!)
    }

    val uploadImageResponse: LiveData<Resource<UploadProfileImageRes>> = uploadSelctedImage

    var file: MultipartBody.Part? = null

    fun onclickSave(pathFile:String){
        val requests = HashMap<String, RequestBody>()
        file = RequestBodyRetrofit.toRequestBodyProfileImage(pathFile)
        requests.put(
            SyncConstants.APIParams.USER_ID.value,
            RequestBodyRetrofit.toRequestBodyString(SharedPrefs.getUser().id.toString())
        )
        requestApi.value = requests
    }

    fun setupObserversUploadImage() {

        uploadImageResponse.observe(lifeCycle!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if(it.data!!.isSuccess()){
                        SharedPrefs.saveUserProfilePic(it.data.data)
                        activity.finish()
                    }else{
                        showToast(activity,it.data.message)
                    }
                }

                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(activity,it.data?.message)
                }

                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })

    }


}