package com.highenergymind.di

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.firstUpper
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class ApplicationClass : Application() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var selectedLanguage: String

    companion object {
        var isEnglishSelected: Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
        getSelectedLanguage()
        initAppFlyer()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
             PurchasesConfiguration.Builder(this, "api key").build()
        )
    }

    fun getSelectedLanguage() {
        val userData = sharedPrefs.getUserData()
        selectedLanguage = if ((userData?.language?.firstUpper()
                ?: AppConstant.LANGUAGE.ENGLISH.value) == AppConstant.LANGUAGE.ENGLISH.value
        ) "en" else "de-rDE"
        isEnglishSelected = selectedLanguage == "en"
    }

    private fun initAppFlyer() {
        val key = ""
        AppsFlyerLib.getInstance().init(key, null, this);
        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().start(this, key, object : AppsFlyerRequestListener {
            override fun onSuccess() {}
            override fun onError(errorCode: Int, errorDesc: String) {}
        })
    }


}