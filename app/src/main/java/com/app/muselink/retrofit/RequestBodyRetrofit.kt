package com.app.muselink.retrofit

import com.app.muselink.util.SyncConstants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object RequestBodyRetrofit {
    fun toRequestBodyString(value: String?): RequestBody {
        return value!!.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun toRequestBodyNullImageSupport(value: String?): MultipartBody.Part {
        val data = value!!.toString().toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            SyncConstants.APIParams.SUPPORT_FILE.value,
            null,
            data
        )
    }

    fun toRequestBodyProfileImage(value: String?): MultipartBody.Part {
        val file = File(value)
        val data = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            SyncConstants.APIParams.PROFILE_PICTURE.value,
            file.name,
            data
        )
    }

    fun toRequestBodySupportFile(value: String?): MultipartBody.Part {
        val file = File(value)
        val data = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            SyncConstants.APIParams.SUPPORT_FILE.value,
            file.name,
            data
        )
    }


}