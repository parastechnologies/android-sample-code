package com.mindbyromanzanoni.view.activity.message

import com.microsoft.signalr.HubConnectionState
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityMessageBinding
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.socket.SignalR
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.openChromeTab
import com.mindbyromanzanoni.utils.openComingSoonDialog
import com.mindbyromanzanoni.view.activity.contactUs.ContactUsActivity
import com.mindbyromanzanoni.view.activity.messageList.MessageListActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessageActivity : BaseActivity<ActivityMessageBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.activity_message
    }
    override fun initView() {
        setToolbar()
        clickListeners()
        RunInScope.ioThread {
            if (SignalR.hubConnection?.connectionState != HubConnectionState.CONNECTED) {
                SignalR.connectSocket(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN), {})
            }
        }
    }
    private fun clickListeners() {
        binding.apply {
            cvTalkToBot.setOnClickListener {
                 openChromeTab("https://www.mindfreed.org/contact-8-1")
            }
            cvQuestion.setOnClickListener {
                launchActivity<ContactUsActivity> { }
            }
            cvMessage.setOnClickListener {
                launchActivity<MessageListActivity> { }
            }
            cvSessionBook.setOnClickListener {
                openChromeTab("https://www.mindfreed.org/book-online")
            }
        }
    }
    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.message_heading)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }
    override fun viewModel() {
    }

}