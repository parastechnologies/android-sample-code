package com.mindbyromanzanoni.view.activity.notification

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.notification.NotificationListResponse
import com.mindbyromanzanoni.data.response.notification.ReminderData
import com.mindbyromanzanoni.databinding.ActivityNotificationBinding
import com.mindbyromanzanoni.databinding.ItemNotificationListBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.convertDateTimeUtcToLocal
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.getTimeInAgo
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.setReminder.SetReminderActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@NotificationActivity
    var notificationReminderData : ReminderData? = null

    companion object {
        lateinit var callbackNotificationStatus :(Boolean)->Unit
    }

    override fun getLayoutRes() = R.layout.activity_notification

    override fun initView() {
        setToolbar()
        observeDataFromViewModal()
        apiHit()
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.tvToolTitle.text = getString(R.string.Notification)
            toolbar.ivNotification.visibility = View.VISIBLE
            toolbar.ivNotification.setImageResource(R.drawable.set_reminder_svg)
            toolbar.ivNotification.setOnClickListener {
                launchActivity<SetReminderActivity>(null){}
                /*  val bottomSheetSetReminder = BottomSheetSetReminder(notificationReminderData)
                bottomSheetSetReminder.callbackStatus = {status,id->
                    if (status){
                        notificationReminderData?.reminderTypeId = id
                    }
                }
                bottomSheetSetReminder.show(supportFragmentManager, "")*/
            }
            toolbar.ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }
    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitNotificationListApi()
        }
    }
    /** set recycler view Meditation  List */
    private fun initMeditationRecyclerView(data: ArrayList<NotificationListResponse>) {
        binding.rvNotification.adapter = resourceListAdapter
        resourceListAdapter.submitList(data)
    }
    private val resourceListAdapter =
        object : GenericAdapter<ItemNotificationListBinding, NotificationListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_notification_list
            }

            @SuppressLint("SetTextI18n")
            override fun onBindHolder(
                holder: ItemNotificationListBinding,
                dataClass: NotificationListResponse,
                position: Int
            ) {
                holder.apply {
                    tvTitle.text = dataClass.title+" ("+getTimeInAgo(dataClass.notificatioOn)+")"
                    tvDesc.text = dataClass.body
                }
            }
        }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.notificationListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        notificationReminderData = data?.data?.remiderDetail
                        if (data?.success == true) {
                            if (data.data.notificationList?.isEmpty() == true){
                                binding.noDataFound.visible()
                            }else{
                                binding.noDataFound.gone()
                                callbackNotificationStatus.invoke(false)
                                initMeditationRecyclerView(data.data.notificationList ?: ArrayList())
                            }

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

        viewModal.showLoading.observe(activity) {
            if (it) {
                MyProgressBar.showProgress(activity)
            } else {
                MyProgressBar.hideProgress()
            }
        }
    }
}