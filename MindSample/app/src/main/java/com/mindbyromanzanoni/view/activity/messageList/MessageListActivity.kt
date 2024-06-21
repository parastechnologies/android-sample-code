package com.mindbyromanzanoni.view.activity.messageList

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.microsoft.signalr.HubConnectionState
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.userMessageList.UserMessageListResponse
import com.mindbyromanzanoni.data.response.userMessageList.UserMessageResponse
import com.mindbyromanzanoni.databinding.ActivityMessageListBinding
import com.mindbyromanzanoni.databinding.ItemMessageListBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.socket.SignalR
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.MessageListDialog
import com.mindbyromanzanoni.view.activity.chatList.UserChatListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MessageListActivity : BaseActivity<ActivityMessageListBinding>() {
    private var showSearch = false
    var activity = this@MessageListActivity

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getLayoutRes(): Int = R.layout.activity_message_list
    override fun initView() {
        setToolbar()
        clickListeners()
        setRecyclerViewAdapter()
        showProgress(true)
    }

    private fun setRecyclerViewAdapter() {
        binding.rvNotification.apply {
            layoutManager = LinearLayoutManager(this@MessageListActivity)
            adapter = resourceListAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (SignalR.hubConnection?.connectionState != HubConnectionState.CONNECTED) {
            SignalR.connectSocket(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN), {
                SignalR.hubConnection?.invoke("UsersChatList")
            })
        } else {
            SignalR.hubConnection?.invoke("UsersChatList")
        }
        setMessageReceiver()
    }

    override fun onPause() {
        super.onPause()
        SignalR.hubConnection?.remove("ShowChatUsersList")
        SignalR.hubConnection?.remove("ReceiveMessage")
    }

    private fun setMessageReceiver() {
        SignalR.hubConnection?.on("ShowChatUsersList", {
            if (it != null) {
                val response = Gson().fromJson(it, UserMessageResponse::class.java)
                if (!response.Data.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        showProgress(false)
                        binding.noChatsFound.gone()
                        initMessageListRecyclerView(response.Data ?: arrayListOf())
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.noChatsFound.visible()
                        binding.shimmerCommentList.shimmerAnimationEffect(false)
                    }
                }
            }
            RunInScope.mainThread {
                binding.shimmerCommentList.shimmerAnimationEffect(false)
            }
        }, String::class.java)

        SignalR.hubConnection?.on("ReceiveMessage", {
            RunInScope.mainThread {
                SignalR.hubConnection?.invoke("UsersChatList")
            }
        }, String::class.java)
    }
    override fun viewModel() {}
    private fun clickListeners() {
        binding.apply {
            relativeLayout.setOnClickListener {
                if (!showSearch) {
                    frame.visible()
                    idShowMessages.rotation = 180f
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, MessageListDialog()).commitAllowingStateLoss()
                } else {
                    idShowMessages.rotation = 0f
                    frame.gone()
                }
                showSearch = !showSearch
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

    /** set recycler view Message  List */
    private fun initMessageListRecyclerView(isResponse: ArrayList<UserMessageListResponse>) {
        resourceListAdapter.submitList(isResponse)
    }

    /** Observer Response via View model*/
    private fun showProgress(isLoading: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.apply {
                if (isLoading) {
                    rvNotification.gone()
                } else {
                    rvNotification.visible()
                }
                shimmerCommentList.shimmerAnimationEffect(isLoading)
            }
        }
    }

    private val resourceListAdapter =
        object : GenericAdapter<ItemMessageListBinding, UserMessageListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.item_message_list
            }

            override fun onBindHolder(
                holder: ItemMessageListBinding,
                dataClass: UserMessageListResponse,
                position: Int
            ) {
                holder.tvUserName.text = dataClass.FullName.capitalize(Locale.getDefault())
                if ((dataClass.FullName).isNotEmpty()) {
                    val name: String = dataClass.FullName.substring(0, 1)
                    holder.tvFirstChar.text = name ?: ""
                }
                //holder.tvEmail.text = dataClass.LastMessage
                if (dataClass.UnseenCount > 0) {
                    holder.rlCounts.visible()
                    holder.tvCounts.text = dataClass.UnseenCount.toString()
                } else {
                    holder.rlCounts.gone()
                }
                // holder.circleImageView.setImageFromUrl(R.drawable.placeholder_mind, dataClass.Image)
                holder.root.setOnClickListener {
                    val bundle = bundleOf(AppConstants.RECEIVER_USER_ID to dataClass.OtherUserId)
                    launchActivity<UserChatListActivity>(0, bundle) { }
                }
            }

            override fun submitList(list: List<UserMessageListResponse>?) {
                super.submitList(list?.let { ArrayList(it) })
            }
        }

}