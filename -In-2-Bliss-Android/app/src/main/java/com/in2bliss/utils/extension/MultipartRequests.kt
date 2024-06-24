package com.in2bliss.utils.extension

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


/**
 * Creating file multi part for api request
 * @param apiKey
 * */
fun File?.createMultiPartForFile(
    apiKey: String
): MultipartBody.Part? {
    return if (this != null){
        MultipartBody.Part.createFormData(
            name = apiKey,
            filename = this.name,
            body = this.asRequestBody("*/*".toMediaTypeOrNull())
        )
    }else null
}

/**
 * Creating null multipart for api request if file is empty or null
 * @param apiKey
 * */
fun createNullMultiPart(
    apiKey: String
): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        name = apiKey,
        filename = null,
        "".toRequestBody("*/*".toMediaTypeOrNull())
    )
}

/**
 * Creating multipart request body for string
 * */
fun String.createMultipartForString(): RequestBody {
    return this.toRequestBody("application/json".toMediaTypeOrNull())
}