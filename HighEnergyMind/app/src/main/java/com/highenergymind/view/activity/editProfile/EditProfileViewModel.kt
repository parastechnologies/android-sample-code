package com.highenergymind.view.activity.editProfile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ApiService
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.api.getResult
import com.highenergymind.base.BaseViewModel
import com.highenergymind.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @ApplicationContext context: Context, val apiService: ApiService, val sharedPrefs: SharedPrefs

) : BaseViewModel(context) {
    var imagePart: MultipartBody.Part? = null
    val partMap by lazy {
        HashMap<String, RequestBody>()
    }
    val editProfileResponse by lazy { MutableSharedFlow<ResponseResult<ResponseWrapper>>() }

    fun editProfileApi() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            val response =
                getResult({ apiService.editProfile(imagePart, partMap) }, ApiEndPoint.EDIT_PROFILE)
            editProfileResponse.emit(response)
            isLoading.emit(false)
        }
    }

    override fun retry(type: String) {

    }


}