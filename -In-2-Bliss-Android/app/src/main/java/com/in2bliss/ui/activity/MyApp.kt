package com.in2bliss.ui.activity

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.in2bliss.data.model.TextAffirmation
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.domain.AudioConverterStatusListenerInterface
import com.in2bliss.domain.DownloadFIleInInternalStorageInterface
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.domain.RoomDataBaseInterface
import com.in2bliss.workManager.AudioConverterWorker
import com.in2bliss.workManager.DownloadWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var customWorkerFactory: CustomWorkerFactory


    var isDeepLinkData = false

     var textAddAffirmation: TextAffirmation? = null

    var setUpdatedData: ((TextAffirmation?,Boolean) -> Unit)? =null

    companion object {
        var instanceData: MyApp? = null
        fun getInstance(): MyApp? {
           return  instanceData
        }

    }

    override fun onCreate() {
        super.onCreate()
        instanceData=this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }





    /**
     * @return The [Configuration] used to initialize WorkManager
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(customWorkerFactory)
            .build()
    }

    class CustomWorkerFactory @Inject constructor(
        private val roomDataBaseInterface: RoomDataBaseInterface,
        private val downloadFIleInInternalStorageInterface: DownloadFIleInInternalStorageInterface,
        private val downloadStatusListener: DownloadStatusListenerInterface,
        private val sharedPreference: SharedPreference,
        private val audioConverterStatusListenerInterface: AudioConverterStatusListenerInterface
    ) : WorkerFactory() {

        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {

            return when (workerClassName) {
                DownloadWorker::class.java.name -> {
                    DownloadWorker(
                        context = appContext,
                        workerParameters = workerParameters,
                        downloadHelper = downloadFIleInInternalStorageInterface,
                        roomDatabase = roomDataBaseInterface,
                        downloadStatusListener = downloadStatusListener,
                        sharedPreference = sharedPreference
                    )
                }

                else -> {
                    AudioConverterWorker(
                        context = appContext,
                        workerParameters = workerParameters,
                        audioConverterStatusImpl = audioConverterStatusListenerInterface
                    )
                }
            }
        }
    }
}