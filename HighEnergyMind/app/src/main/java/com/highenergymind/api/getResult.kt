package com.highenergymind.api

import com.highenergymind.base.BaseResponse
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException


suspend fun <T> getResult(
    call: suspend () -> Response<T>,
    apiType: String
): ResponseResult<ResponseWrapper> {
    try {
        val response = call()
        return when (response.code()) {
            200, 201 -> {
                val body = response.body() as BaseResponse
                if (body.success == true) {
                    ResponseResult.SUCCESS(ResponseWrapper(body, ""))
                } else if (body.code == 401) {

                    ResponseResult.UNAUTHORIZED(ResponseWrapper("", body.message?:"Login session ended"))
                } else {
                    ResponseResult.ERROR(
                        ResponseWrapper(
                            "",
                            body.message ?: "Server error"
                        )
                    )
                }

            }

            400, 422 -> {
                val json = response.errorBody()?.string()?.let { JSONObject(it) }
                ResponseResult.ERROR(ResponseWrapper(json?.getString("errorMessage"), ""))
            }

            401 -> {
                val json = response.errorBody()?.string()?.let { JSONObject(it) }
                ResponseResult.UNAUTHORIZED(ResponseWrapper("", json?.getString("message")?:"Login session ended"))
            }

            else -> {
                ResponseResult.ERROR(ResponseWrapper("", "Something went wrong, please try again"))
            }
        }

    } catch (exception: Exception) {
        return when (exception) {
            is UnknownHostException -> {
                ResponseResult.FAILURE(
                    ResponseWrapper(
                        apiType,
                        "No internet connectivity detected.Please reconnect and try again."
                    )
                )
            }

            is SocketTimeoutException -> {
                ResponseResult.FAILURE(ResponseWrapper(apiType, "Connection Time Out"))
            }

            else -> {
                ResponseResult.FAILURE(
                    ResponseWrapper(
                        apiType,
                        exception.localizedMessage ?: "Something went wrong"
                    )
                )
            }
        }
    }
}