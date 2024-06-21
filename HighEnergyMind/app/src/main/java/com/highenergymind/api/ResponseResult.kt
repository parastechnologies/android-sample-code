package com.highenergymind.api

sealed class ResponseResult<out T> {
    data class SUCCESS<T>(val result: T) : ResponseResult<T>()
    data class ERROR<T>(val result: T) : ResponseResult<T>()
    data class FAILURE<T>(val msg: T) : ResponseResult<T>()
    data class UNAUTHORIZED<T>(val result: T) : ResponseResult<T>()
}
data class ResponseWrapper(val data: Any?, val errorMsg: Any)