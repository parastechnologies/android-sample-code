package com.app.muselink.ui.activities.splash

import android.os.Bundle
import android.os.Handler
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.activities.selectExplore.SelectExploreActivity
import com.app.muselink.ui.activities.welcome.WelcomeActivity
import com.app.muselink.util.intentComponent

class SplashActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(mainLooper).postDelayed({
            if (SharedPrefs.getFirstTime(AppConstants.IS_FIRST_TIME)) {
                intentComponent(SelectExploreActivity::class.java, null)
                finish()
            } else {
                intentComponent(WelcomeActivity::class.java, null)
                finish()
            }
        }, 1500)
    }
}