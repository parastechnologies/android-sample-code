package com.app.muselink.retrofit

import com.app.muselink.util.SyncConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiClient @Inject constructor(){
    fun getOkHttp() : OkHttpClient{
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .writeTimeout(SyncConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(SyncConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(SyncConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }
}