package com.app.muselink.ui.activities.chatactivity

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.databinding.ActivityChatBinding
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.adapter.chatscreen.UserChatAdapter
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.bottomsheets.MoreInfoChatBottomSheet
import com.app.muselink.ui.fragments.chatscreen.modals.ChatListRes
import com.app.muselink.ui.fragments.chatscreen.modals.MessageResponse
import com.app.muselink.ui.fragments.chatscreen.modals.ModalMessage
import com.app.muselink.ui.fragments.chatscreen.modals.RecentChatDetails
import com.app.muselink.util.DateTimeUtils
import com.app.muselink.util.hideKeyboard
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class ChatViewModelOriginal @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {


    var binding: ActivityChatBinding? = null
    var lifecycleOwner: LifecycleOwner? = null
    var messageText = ObservableField<String>()
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private val _getChatListing = requestApi.switchMap { requestApi ->
        repository.getChatListing(requestApi)
    }
    var listChatMessages = ArrayList<ModalMessage>()
    private val chatListing: LiveData<Resource<ChatListRes>> = _getChatListing
    var userChatAdapter: UserChatAdapter? = null
    var recentChatDetails: RecentChatDetails? = null
    var receiverId: String? = ""
    var senderId = ""
    var receivername: String? = ""
    private var webSocketClient: WebSocketClient? = null
    private var messageFromDm = false

    /**
     * Get Intent Data
     * */
    fun getIntentData() {
        if (activity.intent != null) {
            val bundle = activity.intent.extras
            if (bundle != null) {
                if (bundle.containsKey(AppConstants.RECENT_CHAT_MODAL)) {
                    recentChatDetails =
                        activity.intent.getSerializableExtra(AppConstants.RECENT_CHAT_MODAL) as RecentChatDetails?
                    if (recentChatDetails?.receiver_id.toString() == SharedPrefs.getUser().id.toString()) {
                        receiverId = recentChatDetails?.sender_id.toString()
                        receivername = recentChatDetails?.senderName.toString()
                    } else {
                        receiverId = recentChatDetails?.receiver_id.toString()
                        receivername = recentChatDetails?.receiverName.toString()
                    }
                }
                if (bundle.containsKey(AppConstants.MATCH_SCREEN)) {
                    receiverId = activity.intent.getStringExtra(AppConstants.receiverId)
                    receivername = activity.intent.getStringExtra(AppConstants.receiverName)
                }
                if (bundle.containsKey(AppConstants.DM)) {
                    messageFromDm = true
                    receiverId = activity.intent.getStringExtra(AppConstants.receiverId)
                    receivername = activity.intent.getStringExtra(AppConstants.receiverName)
                    val message = activity.intent.getStringExtra(AppConstants.message)
                    messageText.set(message)
                }
            }
        }

        binding?.tvUserName?.text = receivername
    }

    /**
     * Recyclerview Initialization
     * */
    fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        binding?.rvChat?.layoutManager = linearLayoutManager
        userChatAdapter = UserChatAdapter(activity, listChatMessages)
        binding?.rvChat?.adapter = userChatAdapter
    }
    /**
     * Cal chat listing
     * */
    fun getChatListing() {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.RECEIVER_ID.value] = receiverId.toString()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApi.value = request
    }
    /**
     * Scroll to end
     * */
    fun scrollToEnd() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.rvChat?.scrollToPosition(listChatMessages.size - 1)
        }, 300)
    }
    /**
     * Send message
     * */
    fun onClickSendMessage() {
        try {
            if (messageText.get().isNullOrEmpty().not()) {
                sendMessage()
            } else {
                showToast(activity, activity.getString(R.string.please_enter_comment))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun onClickAddImages(view:View){

    }

    fun onClickMore(view: View) {
         val bundle= bundleOf("fromId" to "" ,"toId" to "" , "matchingType" to "")
        val fragment=MoreInfoChatBottomSheet()
        fragment.arguments=bundle
        fragment.show(((view.context as Activity) as AppCompatActivity).supportFragmentManager, "AuthDialog")
    }
    /**
     * Connect socket
     * */
    fun connectionToWebSocket() {
        val chatroom = if (SharedPrefs.getUser().id!!.toInt() < 23) {
            SharedPrefs.getUser().id!! + "-$receiverId"
        } else {
            "$receiverId-" + SharedPrefs.getUser().id!!
        }
        val uri = URI.create(SyncConstants.WEB_SOCKET_CONNECTION_URL + "?token=" + SharedPrefs.getUser().Chat_Uniq_Number + "&&room=" + chatroom + "&&userID=" + SharedPrefs.getUser().id)
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {
                /*  messageFromDm = false
                  sendMessage()*/
            }

            override fun onMessage(message: String) {
                if (message != "null") {
                    val jsonObject = JSONObject(message)
                    if (jsonObject["Response"] == "true") {
                        val gson = Gson()
                        val model: MessageResponse =
                            gson.fromJson(jsonObject.toString(), MessageResponse::class.java)
                        if (model.isSuccess()) {
                            listChatMessages.add(model.data!![0])
                            CoroutineScope(Dispatchers.Main).launch {
                                userChatAdapter?.updateList(listChatMessages)
                                messageText.set("")
                                activity.hideKeyboard()
                                binding?.rvChat?.scrollToPosition(userChatAdapter!!.itemCount - 1)
                            }
                        } else {
                            showToast(activity, model.message)
                        }
                    }
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
            }

            override fun onError(ex: Exception) {
            }
        }
        webSocketClient!!.connect()
    }

    private fun sendMessage() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("userID", SharedPrefs.getUser().id)
        jsonObject.addProperty("recieverID", receiverId)
        jsonObject.addProperty("msg", messageText.get().toString())
        jsonObject.addProperty("type", "Chat")
        jsonObject.addProperty("serviceType", "Chat")
        val chatroom = if (SharedPrefs.getUser().id!!.toInt() < 23) {
            SharedPrefs.getUser().id!! + "-$receiverId"
        } else {
            "$receiverId-" + SharedPrefs.getUser().id!!
        }
        jsonObject.addProperty("room", chatroom)
        webSocketClient?.send(jsonObject.toString())
        val modalMessage = ModalMessage()
        modalMessage.sender_id = SharedPrefs.getUser().id
        modalMessage.senderName = SharedPrefs.getUser().name
        modalMessage.receiverName = receivername
        modalMessage.receiver_id = receiverId
        modalMessage.message = messageText.get().toString()
        modalMessage.created_on = DateTimeUtils.getCureentDateTime()
        listChatMessages.add(0, modalMessage)
        userChatAdapter?.updateList(listChatMessages)
        messageText.set("")
        binding?.rvChat?.scrollToPosition(userChatAdapter!!.itemCount - 1)
    }
    /**
     * Response Observe
     * */
    fun setObserverChatListing() {
        chatListing.observe(lifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data.isNullOrEmpty().not()) {
                                binding?.noDataFound = true
                                listChatMessages = it.data.data!!
                                userChatAdapter?.updateList(listChatMessages)
                                binding?.rvChat?.scrollToPosition(userChatAdapter!!.itemCount - 1)
                            } else {
                                binding?.noDataFound = false
                            }
                        } else {
                            binding?.noDataFound = false
                        }
                    } else {
                        binding?.noDataFound = false
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    binding?.noDataFound = false
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }

    /**
     *Close socket on destroy
     * */
    fun onDispose() {
        if (webSocketClient != null && webSocketClient!!.isOpen) {
            webSocketClient!!.close()
        }
    }
}