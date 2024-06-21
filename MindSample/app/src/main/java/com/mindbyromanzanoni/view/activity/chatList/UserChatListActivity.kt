package com.mindbyromanzanoni.view.activity.chatList

import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.microsoft.signalr.HubConnectionState
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.userMessageList.MessageListResponse
import com.mindbyromanzanoni.data.response.userMessageList.MessageResponse
import com.mindbyromanzanoni.databinding.ActivityUserChatListBinding
import com.mindbyromanzanoni.databinding.ItemChatListBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.socket.SignalR
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.convertDateFormatMessage
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.view.activity.dashboard.DashboardActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class UserChatListActivity : BaseActivity<ActivityUserChatListBinding>() {

    private val viewModal: HomeViewModel by viewModels()
    private var lastVisibleItemPosition = 0
    private var isLastItemVisible: Boolean = false
    var activity = this@UserChatListActivity
    private var myList: ArrayList<MessageListResponse> = arrayListOf()

    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int = R.layout.activity_user_chat_list
    override fun initView() {
        setToolbar()
        getIntentData()
        initRecyclerView()
        observeDataFromViewModal()
        clickListener()
        setKeyboardListener()

        if (SignalR.hubConnection?.connectionState != HubConnectionState.CONNECTED) {
            SignalR.connectSocket(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN), {
                SignalR.hubConnection?.invoke(
                    "ChatList",
                    sharedPrefs.getUserData()?.userId ?: 0,
                    viewModal.otherUSerId.get() ?: 0)
            })
        } else {
            SignalR.hubConnection?.invoke(
                "ChatList",
                sharedPrefs.getUserData()?.userId ?: 0,
                viewModal.otherUSerId.get() ?: 0
            )
        }
    }
    companion object {
        var callbackUserList: ((Boolean) -> Unit)? = null
    }
    override fun onResume() {
         super.onResume()
        setSignalR()
    }
    override fun onPause() {
         super.onPause()
        try {
            SignalR.hubConnection?.remove("ShowChatList")
            SignalR.hubConnection?.remove("ReceiveMessage")
        } catch (_: Exception) {
        }
    }
    private fun setSignalR() {
        SignalR.hubConnection?.on("ShowChatList", {
            val model: MessageResponse = Gson().fromJson(it, MessageResponse::class.java)
            RunInScope.mainThread {
                binding.linearProgressIndicator.gone()
                initChatListRecyclerView(model.Messages ?: arrayListOf())
            }
        }, String::class.java)
        SignalR.hubConnection?.on("ReceiveMessage", {
            val model = Gson().fromJson(it, MessageListResponse::class.java)
            RunInScope.mainThread {
                myList.add(model)
                SignalR.hubConnection?.invoke("ReadMessage",model.ChatId)
                initChatListRecyclerView(myList)
            }
        }, String::class.java)
    }
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            viewModal.otherUSerId.set(intent.getInt(AppConstants.RECEIVER_USER_ID))
        }
    }
    private fun setKeyboardListener() {
        binding.rvNotification.apply {
            binding.rvNotification.addOnLayoutChangeListener { v, _, _, _, bottom, _, _, _, oldBottom ->
                 if (bottom < oldBottom) {
                    v.post {
                        try {
                            smoothScrollToPosition((adapter?.itemCount ?: 0) - 1)
                        }catch (_:Exception){}
                    }
                }
            }
        }
    }
    private fun clickListener() {
        binding.apply {
            btnSend.setOnClickListener {
                if (validator.isValidChatMessage(activity, binding)) {
                    RunInScope.ioThread {
                        sendMessageToSocket()
                    }
                }
            }
            etMessage.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (validator.isValidChatMessage(activity, binding)) {
                        RunInScope.ioThread {
                            sendMessageToSocket()
                        }
                    }
                    true
                } else {
                    false
                }
            }
        }
    }
    override fun viewModel() {
        binding.viewModel = viewModal
    }
    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.message_heading)
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTaskRoot) {
                    startActivity(
                        Intent(
                            this@UserChatListActivity, DashboardActivity::class.java
                        )
                    )
                }
                finishActivity()
            }
        })
    }
    /** set recycler view Chat List */
    private fun initChatListRecyclerView(messages: ArrayList<MessageListResponse>) {
        myList = messages
        chatListAdapter.submitList(myList)
        val itemCount = chatListAdapter.itemCount
        lastVisibleItemPosition = itemCount - 1
        binding.rvNotification.scrollToPosition(lastVisibleItemPosition)
    }
    private fun initRecyclerView() {
        binding.rvNotification.adapter = chatListAdapter
    }
    private val chatListAdapter =
        object : GenericAdapter<ItemChatListBinding, MessageListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_chat_list
            }
            override fun onBindHolder(holder: ItemChatListBinding, dataClass: MessageListResponse, position: Int) {
                if (sharedPrefs.getUserData()?.userId == dataClass.UserId) {
                    holder.apply {
                        tvMyMessage.text = dataClass.Message.toString().trim()
                        tvTimeMy.text = convertDateFormatMessage(dataClass.MessageOn)
                        myLayout.visible()
                        userMessageLayout.gone()
                        if (dataClass.IsRead == true) {
                            ivMessageSeen.setBackgroundResource(R.drawable.ic_message_seen)
                        } else {
                            ivMessageSeen.setBackgroundResource(R.drawable.ic_message_un_seen)
                        }
                    }
                } else {
                    holder.apply {
                        myLayout.gone()
                        userMessageLayout.visible()
                        tvUserMessage.text = dataClass.Message.toString().trim()
                        tvTimeUser.text = convertDateFormatMessage(dataClass.MessageOn)
                    }
                }
                holder.root.setOnClickListener {}
            }
        }
    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.sendMessageSharedFlow.collectLatest { resp ->
                RunInScope.mainThread {
                    initChatListRecyclerView(myList)
                }
            }
        }
        viewModal.showLoading.observe(activity) {
            binding.apply {
                if (it) {
                    linearProgressIndicator.visible()
                } else {
                    linearProgressIndicator.gone()
                }
            }
        }
    }
    override fun finishActivity(requestCode: Int) {
        super.finishActivity(requestCode)
        callbackUserList?.invoke(true)
    }
    private fun sendMessageToSocket() {
        binding.apply {
            try {
                val request = HashMap<String, Any>()
                request["SenderId"] = sharedPrefs.getUserData()?.userId ?: 0
                request["ReceiverId"] = viewModal.otherUSerId.get() ?: 0
                request["Message"] = etMessage.text.toString()
                request["MessageType"] = "text"
                etMessage.setText("")
                SignalR.hubConnection?.send("SendMessages", request)
            } catch (e: Exception) {
                SignalR.connectSocket(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN), {
                    setSignalR()
                })

            }
        }
    }

}
