package com.in2bliss.ui.activity.splash

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivitySplashBinding
import com.in2bliss.ui.activity.auth.appStatus.AppStatusActivity
import com.in2bliss.ui.activity.auth.stepFive.StepFiveActivity
import com.in2bliss.ui.activity.auth.stepFour.StepFourActivity
import com.in2bliss.ui.activity.auth.stepThree.StepThreeActivity
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.ui.activity.splash.splash_step.SplashStep
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(
    layout = R.layout.activity_splash
) {

    @Inject
    lateinit var sharedPreference: SharedPreference

    private var uri: Uri? = null
    private var replaceUri = "https://in2bliss.com.au/in2bliss/share/"

    override fun init() {
//        val bundle1 = NotificationModel()
//        bundle1.type = "0"
//        bundle1.dataId = "384"
//        val intent = Intent(this@SplashActivity, HomeActivity::class.java).apply {}
//        intent.putExtra(AppConstant.NOTIFICATION_TYPE, Gson().toJson(bundle1))
//        startActivity(intent)

        uri = intent?.data
        lifecycleScope.launch(Dispatchers.Main) {
            if (sharedPreference.userData?.data == null) {
                intent(
                    destination = SplashStep::class.java
                )
                finish()
                return@launch
            }

            val bundle = Bundle()

            withContext(Dispatchers.IO) {
                if (uri != null) {
                    val type = uri.toString().substringAfter("share/").substringBefore("/")
                    val id = uri.toString().substringAfter("share/").substringAfter("/")
                    bundle.putString(AppConstant.TYPE, type)
                    bundle.putString(AppConstant.ID, id)
                }
            }

            /** Checking sign-Up remaining steps */
            val destination =
                when (sharedPreference.userData?.data?.profileStatus) {
                    1 -> StepThreeActivity::class.java
                    2 -> StepFourActivity::class.java
                    3 -> StepFiveActivity::class.java
                    4 -> AppStatusActivity::class.java
                    else -> {
                        HomeActivity::class.java
                    }
                }
            delay(200.milliseconds)
            intent(
                destination = destination,
                bundle = bundle
            )
            finishAffinity()
        }
    }


}