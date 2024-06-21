package com.mindbyromanzanoni.view.activity.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.microsoft.signalr.HubConnectionState
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.data.response.home.ScreenType
import com.mindbyromanzanoni.databinding.ActivityDashboardBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.socket.SignalR
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.openChromeTab
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.edificationVideoPlayer.EdificationVideoPlayerActivity
import com.mindbyromanzanoni.view.activity.eventDetails.EventDetailActivity
import com.mindbyromanzanoni.view.activity.message.MessageActivity
import com.mindbyromanzanoni.view.activity.notification.NotificationActivity
import com.mindbyromanzanoni.view.activity.nowPlaying.NowPlayingActivity
import com.mindbyromanzanoni.view.activity.onboarding.OnBoardingActivity
import com.mindbyromanzanoni.view.activity.resource.ResourceDetailActivity
import com.mindbyromanzanoni.view.activity.setting.SettingScreenActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@DashboardActivity

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        observeDataFromViewModal()
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        callBackHandlingUpdateProfile()
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostView) as NavHostFragment
        navController = navHostFragment.navController
        navigation = findViewById(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(navigation, navHostFragment.navController)
        navController.addOnDestinationChangedListener(this)
        bottomNavigationController()
        hitNotificationStatusAPI()
        onClick()
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        RunInScope.mainThread {
            if (SignalR.hubConnection?.connectionState != HubConnectionState.CONNECTED) {
                SignalR.connectSocket(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN), {})
            }
        }
    }
    private fun hitNotificationStatusAPI() {
        RunInScope.ioThread {
            viewModal.hitNotificationStatusApi()
        }
    }
    override fun onResume() {
        super.onResume()
        RunInScope.ioThread {
            viewModal.hitChatCountApi()
        }
    }

    private fun callBackHandlingUpdateProfile() {
        NotificationActivity.callbackNotificationStatus = { status ->
            if (!status) {
                binding.ivNotification.setImageResource(R.drawable.ic_notification)
            } else {
                binding.ivNotification.setImageResource(R.drawable.unseen_notification)
            }
        }
    }

    private fun onClick() {
        binding.apply {
            ivNotification.setOnClickListener {
                launchActivity<NotificationActivity> { }
            }
            ivMessage.setOnClickListener {
                launchActivity<MessageActivity> { }
            }
            ivSetting.setOnClickListener {
                launchActivity<SettingScreenActivity> { }
            }
            ivMyUniversity.setOnClickListener {
                openChromeTab("https://mindmasterclass.thinkific.com/courses/healing-from-trauma-1")
            }
        }
    }

    private fun bottomNavigationController() {
        navigation.setOnItemSelectedListener { item ->
            binding.apply {
                when (item.itemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.homeFragment)
                        tvHeading.gone()
                        ivHeading.gone()
                        ivHomeLogo.visible()
                        item.setIcon(R.drawable.home_active)
                    }

                    R.id.meditationsFragment -> {
                        ivHomeLogo.gone()
                        tvHeading.visible()
                        ivHeading.visible()
                        navController.navigate(R.id.meditationsFragment)
                        tvHeading.text = getString(R.string.meditation)
                        ivHeading.setImageResource(R.drawable.ic_title_meditations)
                        item.setIcon(R.drawable.meditations_active)
                    }

                    R.id.edificationFragment -> {
                        navController.navigate(R.id.edificationFragment)
                        ivHomeLogo.gone()
                        tvHeading.visible()
                        ivHeading.visible()
                        tvHeading.text = getString(R.string.edification)
                        ivHeading.setImageResource(R.drawable.ic_title_edification)
                        item.setIcon(R.drawable.edification_active)
                    }

                    R.id.journalFragment -> {
                        navController.navigate(R.id.journalFragment)
                        ivHomeLogo.gone()
                        tvHeading.visible()
                        ivHeading.visible()
                        tvHeading.text = getString(R.string.journal)
                        ivHeading.setImageResource(R.drawable.ic_title_journal)
                        item.setIcon(R.drawable.journal_active)
                    }

                    R.id.resourceFragment -> {
                        navController.navigate(R.id.resourceFragment)
                        ivHomeLogo.gone()
                        tvHeading.visible()
                        ivHeading.visible()
                        tvHeading.text = getString(R.string.resources)
                        ivHeading.setImageResource(R.drawable.ic_title_resource)
                        item.setIcon(R.drawable.resources_active)
                    }
                }
            }
            // Deselect previously selected item (change its icon to the outline version)
            val menu = navigation.menu
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                if (menuItem != item) {
                    when (menuItem.itemId) {
                        R.id.homeFragment -> menuItem.setIcon(R.drawable.home_light)
                        R.id.meditationsFragment -> menuItem.setIcon(R.drawable.meditation_light)
                        R.id.edificationFragment -> menuItem.setIcon(R.drawable.edification_light)
                        R.id.journalFragment -> menuItem.setIcon(R.drawable.journal_light)
                        R.id.resourceFragment -> menuItem.setIcon(R.drawable.resource_light)
                        // Handle other items similarly
                    }
                }
            }
            true
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishActivity()
        }
    }

    /** Observer Response via View model for*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.notificationStatusSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            setData(data.data)
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModal.chatCount.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if ((data.count ?: 0) > 0) {
                                RunInScope.mainThread {
                                    binding.rlCounts.visible()
                                    binding.tvCounts.text = data.count.toString()
                                }
                            }else{
                                binding.rlCounts.gone()
                            }
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }

        viewModal.showLoading.observe(activity) {
            if (it) {
                MyProgressBar.showProgress(activity)
            } else {
                MyProgressBar.hideProgress()
            }
        }
    }

    private fun setData(data: Boolean) {
        if (data) {
            binding.ivNotification.setImageResource(R.drawable.unseen_notification)
        } else {
            binding.ivNotification.setImageResource(R.drawable.ic_notification)
        }
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Alert")
            .setMessage("Allow app to send notifications.")
            .setPositiveButton("Ok") { _, _ ->
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private var hasNotificationPermissionGranted = false
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        showNotificationPermissionRationale()
                    }
                }
            }
        }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    /** Handle Deep linking -- get event id from url*/
    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val type = data.getQueryParameter("Type")?.toInt()
            val eventId = data.getQueryParameter("EventId")?.toInt()
            if (sharedPrefs.getUserLogin()) {
                if (type != null && eventId != null) {
                    when (type) {
                        ScreenType.EVENTS.type -> {
                            val bundle = bundleOf(AppConstants.EVENT_ID to eventId)
                            launchActivity<EventDetailActivity>(0, bundle) { }
                        }

                        ScreenType.MEDITATION.type -> {
                            val bundle = bundleOf(
                                AppConstants.SCREEN_TYPE to AppConstants.HOME_SCREEN,
                                AppConstants.EVENT_ID to eventId
                            )
                            launchActivity<NowPlayingActivity>(0, bundle) { }
                        }

                        ScreenType.EDIFICATION.type -> {
                            val bundle = bundleOf(
                                AppConstants.SCREEN_TYPE to AppConstants.HOME_SCREEN,
                                AppConstants.EVENT_ID to eventId
                            )
                            launchActivity<EdificationVideoPlayerActivity>(0, bundle) {
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        }

                        ScreenType.RESOURCE.type -> {
                            val bundle = bundleOf(
                                AppConstants.SCREEN_TYPE to AppConstants.HOME_SCREEN,
                                AppConstants.EVENT_ID to eventId
                            )
                            launchActivity<ResourceDetailActivity>(0, bundle) { }
                        }
                    }
                }
            } else {
                launchActivity<OnBoardingActivity> { }
                finishAffinity()
            }
        }
    }
}