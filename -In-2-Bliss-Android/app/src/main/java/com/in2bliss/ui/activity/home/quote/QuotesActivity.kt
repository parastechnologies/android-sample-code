package com.in2bliss.ui.activity.home.quote

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.ActivityQuotesBinding
import com.in2bliss.ui.activity.home.notification.NotificationVM
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import com.in2bliss.utils.notification.NotificationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuotesActivity : BaseActivity<ActivityQuotesBinding>(
    layout = R.layout.activity_quotes
) {

    @Inject
    lateinit var requestManager: RequestManager

    private val viewModel: QuotesViewModel by viewModels()
    private val viewModelNotification: NotificationVM by viewModels()

    var bundle = NotificationModel()

    override fun init() {
        getBundleData()
        observer()
        settingRecyclerView()
        onClick()
    }

    private fun getBundleData() {
        try {
            Gson().fromJson(
                intent.getStringExtra(AppConstant.NOTIFICATION_TYPE),
                NotificationModel::class.java
            ).let {
                bundle = it
                viewModel.quotesId = bundle.dataId
            }
        } catch (e: Exception) {
            println(e)
        }


        if (bundle.id?.isEmpty()?.not() == true) {
            viewModelNotification.notificationId = bundle.id ?: ""
            viewModelNotification.retryApiRequest(
                apiName = ApiConstant.NOTIFICATION_READ
            )
        }
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun observer() {
        viewModel.viewPagerAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> {
                    binding.pbProgress.gone()
                }

                is LoadState.Loading -> {
                    binding.pbProgress.visible()
                }

                is LoadState.NotLoading -> {
                    binding.tvNoDatFound.visibility(
                        isVisible = viewModel.viewPagerAdapter.snapshot().items.isEmpty()
                    )
                    binding.pbProgress.gone()
                }
            }
        }

        viewModel.getQuotesList().observe(
            this@QuotesActivity
        ) { quotesList ->
            lifecycleScope.launch {
                viewModel.viewPagerAdapter.submitData(quotesList)
                binding.tvNoDatFound.visibility(
                    isVisible = viewModel.viewPagerAdapter.snapshot().items.isEmpty()
                )
            }
        }
    }

    private fun settingRecyclerView() {
        binding.rvRecyclerView.adapter = viewModel.viewPagerAdapter
    }
}