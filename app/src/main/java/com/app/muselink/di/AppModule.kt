package com.app.muselink.di

import com.app.muselink.retrofit.ApiClient
import com.app.muselink.retrofit.ApiRemoteDataSource
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.ApiServices
import com.app.muselink.util.SyncConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson,
    ) : Retrofit = Retrofit.Builder()
        .client(provideClient())
        .baseUrl(SyncConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideClient() : OkHttpClient  = ApiClient().getOkHttp()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): ApiServices = retrofit.create(ApiServices::class.java)


    @Singleton
    @Provides
    fun provideCharacterRemoteDataSource(apiServices: ApiServices) = ApiRemoteDataSource(apiServices)

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: ApiRemoteDataSource) =
        ApiRepository(remoteDataSource)
}