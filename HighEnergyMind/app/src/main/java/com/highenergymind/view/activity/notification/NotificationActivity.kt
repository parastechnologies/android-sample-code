package com.highenergymind.view.activity.notification

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.NotificationResponse
import com.highenergymind.databinding.ActivityNotificationBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible
import com.highenergymind.view.adapter.NotificationAdapter
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResult
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResultHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>(), PaywallResultHandler {
    private lateinit var paywallActivityLauncher: PaywallActivityLauncher

    val viewModel by viewModels<NotificationViewModel>()
    val adapter by lazy {
        NotificationAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_notification
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        setAdapter()
        clicks()
        getNotifications()
//        paywallActivityLauncher = PaywallActivityLauncher(this, this)
//        paywallActivityLauncher.launchIfNeeded(requiredEntitlementIdentifier = "hem_default_offering")

    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                    binding.swipeToRefresh.isRefreshing = it
                }
            }
            lifecycleScope.launch {
                notificationResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as NotificationResponse
                        if (binding.tabLayout.selectedTabPosition==0){
                              binding.tabLayout.getTabAt(0)?.setText(getString(R.string.unread,response.data.size))
                        }
                        if (response.data.isEmpty()) binding.tvNoDataFound.visible() else binding.tvNoDataFound.gone()
                        adapter.submitList(response.data)
                    })
                }
            }
        }
    }

    private fun setAdapter() {
        binding.apply {
            rvNotification.adapter = adapter
        }
    }

    private fun clicks() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    getNotifications()
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                }
            })
            customTool.ivBack.setOnClickListener {
                finish()
            }
            swipeToRefresh.setOnRefreshListener {
                getNotifications()
            }
        }
    }

    private fun getNotifications() {
        viewModel.apply {
            map.clear()
            map[ApiConstant.READ_STATUS]=if (binding.tabLayout.selectedTabPosition==0) "unread" else "read"
            getNotificationList()
        }
    }

    override fun onActivityResult(result: PaywallResult) {
        Log.e("PaywallResult", "onActivityResult: ", )
        when(result){
            is PaywallResult.Purchased->{

            }
            is PaywallResult.Error->{

            }
            is PaywallResult.Cancelled->{

            }
        }
    }


}