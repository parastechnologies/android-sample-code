package com.highenergymind.view.activity.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivitySplashBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getUserCountry
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.activity.onboard.OnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.activity_splash
    }

    override fun initView() {
        fullScreenStatusBar(false)

        Log.e("cont", "initView: ${getUserCountry(this)}")

        startSplash()
        lifecycleScope.launch {
            delay(2700)
            if (sharedPrefs.getLoginStatus()) {
                val bundle = Bundle()
                val data = intent?.data
                if (data?.pathSegments?.contains("inviteLink")==true) {
                    val trackId = data.lastPathSegment
                    bundle.putString(ApiConstant.TRACK_ID, trackId)
                }else if(data?.pathSegments?.contains("inviteMusicLink")==true){
                    val musicId = data.lastPathSegment
                    bundle.putString(ApiConstant.MUSIC_ID, musicId)
                }
                intentComponent(HomeActivity::class.java, bundle)
            } else {
                val country= getUserCountry(this@SplashActivity)
                if (country=="de-at" || country=="de-li" || country=="de-lu" || country=="de" || country=="de-ch" || country=="de-rDE"){
                    (application as ApplicationClass).selectedLanguage="de-rDE"
                }
                intentComponent(OnBoardingActivity::class.java)
            }
            finish()
        }
    }

    private fun startSplash() {
        binding.animationView.playAnimation()
    }


}