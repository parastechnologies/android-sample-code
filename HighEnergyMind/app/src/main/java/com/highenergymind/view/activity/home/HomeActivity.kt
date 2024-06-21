package com.highenergymind.view.activity.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityHomeBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.musicdetails.MusicDetailsActivity
import com.highenergymind.view.activity.profile.ProfileActivity
import com.highenergymind.view.activity.recent.RecentPlayActivity
import com.highenergymind.view.activity.trackDetail.TrackDetailActivity
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.activity_home
    }

    override fun initView() {
        fullScreenStatusBar()
        setupNavController()
        getBundleData()
        clicks()
        if (ApplicationClass.isEnglishSelected.not()) {
//           setSmallSize()
        }
        if (checkNotificationPermissionNot()) {
            notificationPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }
    }


    override fun onResume() {
        super.onResume()
        handlePremium()
    }

    fun handlePremium() {
        binding.apply {
            val userData = sharedPrefs.getUserData()
            if (userData?.isSubscription.isNullOrEmpty()) {
                ivPremium.setImageResource(R.drawable.ic_premium_home)
            } else {
                ivPremium.setImageResource(R.drawable.ic_purchased_home)
            }
        }
    }


//    private fun getSubscriptionStatus() {
//        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
//            override fun onError(error: PurchasesError) {
//
//            }
//
//            override fun onReceived(customerInfo: CustomerInfo) {
//
//                val userData = sharedPrefs.getUserData()
//
//                if (customerInfo.entitlements["pro"]?.isActive == true) {
//                    // Grant user "pro" access
//                    userData?.isSubscription = customerInfo.activeSubscriptions.first()
//
//                } else {
//                    userData?.isSubscription = ""
//                }
//                sharedPrefs.saveUserData(userData!!)
//                handlePremium()
//            }
//        })
//
//    }

    private fun checkNotificationPermissionNot(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    }


    private fun getBundleData() {
        binding.apply {
            when (intent.getIntExtra(AppConstant.SCREEN_FROM, 0)) {
                R.id.profile -> {
                    bottomNav.selectedItemId = R.id.profile
                }

                R.id.editProfileIV -> {
                    bottomNav.selectedItemId = R.id.profile
                    intentComponent(ProfileActivity::class.java)

                }
            }

            if (intent.hasExtra(ApiConstant.TRACK_ID)) {

                val trackId = intent.getStringExtra(ApiConstant.TRACK_ID)
                intentComponent(TrackDetailActivity::class.java, Bundle().also {
                    it.putString(ApiConstant.TRACK_ID, trackId)
                })
            } else if (intent.hasExtra(ApiConstant.MUSIC_ID)) {
                val musicId = intent.getStringExtra(ApiConstant.MUSIC_ID)
                intentComponent(MusicDetailsActivity::class.java, Bundle().also {
                    it.putString(ApiConstant.MUSIC_ID, musicId)
                })
            }
        }
    }

    private fun clicks() {
        binding.apply {
            ivPremium.setOnClickListener {
                if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty()) {
                    intentComponent(UnlockFeatureActivity::class.java)
                } else {
                    intentComponent(RecentPlayActivity::class.java)
                }
            }
        }
    }

    private fun setupNavController() {
        binding.apply {
            val navController = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            bottomNav.setupWithNavController(navController.navController)
            navController.navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.affirmation -> {
                        fullScreenStatusBar(false)
                    }

                    else -> {
                        fullScreenStatusBar()
                    }
                }
            }
        }
    }

    private var notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }

}