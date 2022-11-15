package com.app.muselink
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class MainApplication : MultiDexApplication(), LifecycleObserver {
    var serviceIntent:Intent?=null
    companion object {
        var mContext: Context? = null
        fun getApplicationInstance(): Context {
            return mContext!!
        }
        var mainApplication: MainApplication? = null
        fun getApplication(): MainApplication {
            return mainApplication!!
        }
    }
    override fun onCreate() {
        super.onCreate()
        mContext = this
        mainApplication = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        serviceIntent= Intent(this, CallOfflineService::class.java)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun appInForeground() {
        serviceIntent?.putExtra("userId", SharedPrefs.getString(AppConstants.PREFS_USER_ID))
        serviceIntent?.putExtra("status", "1")
        startService(serviceIntent)
    }
    @SuppressLint("EnqueueWork")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appInBackground() {
        serviceIntent?.putExtra("userId", SharedPrefs.getString(AppConstants.PREFS_USER_ID))
        serviceIntent?.putExtra("status", "0")
        startService(serviceIntent)
    }
}