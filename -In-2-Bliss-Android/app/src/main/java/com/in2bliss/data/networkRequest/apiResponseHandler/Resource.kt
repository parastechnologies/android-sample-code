package com.in2bliss.data.networkRequest.apiResponseHandler

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val apiName: String? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(data = data)

    class Error<T>(message: String) :
        Resource<T>(message = message)

    class UnAuthorized<T>(message: String) : Resource<T>(message = message)

    class Failure<T>(message: String, apiName: String) :
        Resource<T>(message = message, apiName = apiName)

    class SubscriptionExpired<T>(message: String) : Resource<T>(message = message)

    class UserDeleteByAdmin<T>(message: String) : Resource<T>(message = message)

    class UserSuspend<T>(message: String) : Resource<T>(message = message)
    class Loading<T> : Resource<T>()
}
