package com.in2bliss.dependencyInjection

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.in2bliss.BuildConfig
import com.in2bliss.data.audioConverter.AudioConverterStatusImpl
import com.in2bliss.data.downloadFileInInternalStorage.DownloadFileInInternalStorageImp
import com.in2bliss.data.downloadStatusListener.DownloadStatusListenerImpl
import com.in2bliss.data.networkRequest.ApiService
import com.in2bliss.data.roomDataBase.OfflineAudioDao
import com.in2bliss.data.roomDataBase.OfflineAudioDataBase
import com.in2bliss.data.roomDataBase.RoomDataBaseImpl
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.data.networkRequest.ApiHelperImpl
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.domain.AudioConverterStatusListenerInterface
import com.in2bliss.domain.DownloadFIleInInternalStorageInterface
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.domain.RoomDataBaseInterface
import com.in2bliss.utils.constants.AppConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object

AppModule {

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("In2Bliss", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideInterceptor(
        sharedPreference: SharedPreference
    ): Interceptor {
        return Interceptor { chain ->
            val request = if (sharedPreference.userData?.token.isNullOrEmpty()) {
                chain.request().newBuilder().build()
            } else {
                Log.i("my_token", "provideInterceptor: " + sharedPreference.userData?.token)
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + sharedPreference.userData?.token)
                    .build()
            }
            chain.proceed(request = request)
        }
    }

    @Provides
    @Singleton
    fun providesOkHttp(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor = loggingInterceptor)
            .addInterceptor(interceptor = interceptor)
            .addInterceptor(
                interceptor = ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRequestManger(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context).setDefaultRequestOptions(
            RequestOptions().diskCacheStrategy(
                DiskCacheStrategy.DATA
            )
        )
    }

    @Provides
    @Singleton
    fun provideApiHelper(apiService: ApiService): ApiHelperInterface {
        return ApiHelperImpl(apiService = apiService)
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .componentRegistry {
                add(SvgDecoder(context))
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideImageRequest(@ApplicationContext context: Context): ImageRequest.Builder {
        return ImageRequest.Builder(context)
            .crossfade(true)
            .crossfade(500)
    }

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): OfflineAudioDataBase {
        return Room.databaseBuilder(
            context = context,
            OfflineAudioDataBase::class.java,
            name = "OfflineAudioDataBase"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(offlineAudioDataBase: OfflineAudioDataBase): OfflineAudioDao {
        return synchronized(this) { offlineAudioDataBase.dataBaseDao() }
    }

    @Provides
    @Singleton
    fun provideRoomDataBaseHelper(offlineAudioDao: OfflineAudioDao): RoomDataBaseInterface {
        return RoomDataBaseImpl(offlineAudioDao)
    }

    @Provides
    @Singleton
    fun provideDownloadFileInInternalStorage(): DownloadFIleInInternalStorageInterface {
        return DownloadFileInInternalStorageImp()
    }

    @Provides
    @Singleton
    fun provideDownloadStatus(): DownloadStatusListenerInterface {
        return DownloadStatusListenerImpl()
    }

    @Singleton
    @Provides
    fun provideAudioConverterStatus(): AudioConverterStatusListenerInterface {
        return AudioConverterStatusImpl()
    }
}