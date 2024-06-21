package com.highenergymind.di.module

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.highenergymind.BuildConfig
import com.highenergymind.api.ApiConstant
import com.highenergymind.api.ApiService
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .header(
                        ApiConstant.AUTHORIZATION,
                        "${ApiConstant.BEARER} ${SharedPrefs(sharedPreferences).getAccessToken()?:""}"
                    ).build()

                Log.i(
                    ApiConstant.AUTHORIZATION,
                    "http ${ApiConstant.BEARER} " + (SharedPrefs(sharedPreferences).getAccessToken()?:"")
                )
                val origResponse = chain.proceed(request)
                origResponse
            })
            .build()
    }

    @Singleton
    @Provides
    fun retrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun getSharedPref(sharedPreferences: SharedPreferences): SharedPrefs {
        return SharedPrefs(sharedPreferences)
    }

}