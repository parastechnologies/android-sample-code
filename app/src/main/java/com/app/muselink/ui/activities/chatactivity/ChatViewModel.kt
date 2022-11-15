package com.app.muselink.ui.activities.chatactivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import com.app.muselink.commonutils.imageselection.ChoosePhotoHelper
import com.app.muselink.constants.AppConstants
import com.app.muselink.databinding.ActivityChatBinding
import com.app.muselink.enum.MessageType
import com.app.muselink.helpers.PermissionHelper
import com.app.muselink.model.responses.chat.UploadChatFileResponseModel
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
import com.app.muselink.util.FileUtils.getPath
import com.app.muselink.util.hideKeyboard
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.File
import java.net.URI


/**
 * url:-http://ankit.parastechnologies.in/muselink/Api/user/uploadChatStuff
param:-chatStuff
 * */
class ChatViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var permissionHelper: PermissionHelper? = null
    var binding: ActivityChatBinding? = null
    var file: File? = null
    var lifecycleOwner: LifecycleOwner? = null
    var messageText = ObservableField<String>()
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private val requestUploadChatApi = MutableLiveData<MultipartBody.Part>()
    var listChatMessages = ArrayList<ModalMessage>()
    var userChatAdapter: UserChatAdapter? = null
    var recentChatDetails: RecentChatDetails? = null
    var receiverId: String? = ""
    var senderId = ""
    var messageType = MessageType.TEXT.type
    var receivername: String? = ""
    private var choosePhotoHelper: ChoosePhotoHelper? = null

    private var webSocketClient: WebSocketClient? = null
    private var messageFromDm = false

    /**
     * get chat list api request
     * */
    private val _getChatListing = requestApi.switchMap { requestApi ->
        repository.getChatListing(requestApi)
    }
    private val chatListing: LiveData<Resource<ChatListRes>> = _getChatListing

    /**
     * Upload chat file api request
     * */
    private val _getChatUploadFile = requestUploadChatApi.switchMap { requestApi ->
        repository.uploadChatFile(requestApi)
    }
    private val uploadChatFile: LiveData<Resource<UploadChatFileResponseModel>> = _getChatUploadFile

    init {
        permissionHelper = PermissionHelper(activity)
        choosePhotoHelper = ChoosePhotoHelper(activity)
    }

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
                messageType = MessageType.TEXT.type
                sendMessage(messageText.get().toString())
            } else {
                showToast(activity, activity.getString(R.string.please_enter_comment))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * On Click option menu
     * */
    fun onClickMore(view: View) {
        val bundle = bundleOf("fromId" to "", "toId" to "", "matchingType" to "")
        val fragment = MoreInfoChatBottomSheet()
        fragment.arguments = bundle
        fragment.show(
            ((view.context as Activity) as AppCompatActivity).supportFragmentManager,
            "AuthDialog"
        )
    }

    fun onClickAddImages(view: View) {
        if (permissionHelper!!.checkPermissionCameraStorage()) {
            choosePhotoHelper = ChoosePhotoHelper(view.context as Activity)
            choosePhotoHelper?.showAlertDialogImage(activityResultLauncher)
        } else {
            permissionHelper?.checkAndRequestCameraPermission(requestPermissionsResult)
        }
    }

    /**
     * Activity result callback
     * */
    var activityResultLauncher: ActivityResultLauncher<Intent> =
        (activity as AppCompatActivity).registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageURI: Uri? = if (result?.data != null) {
                    result.data?.data
                } else {
                    choosePhotoHelper?.cameraUri
                }
                messageType = MessageType.IMAGE.type
                file = File(getPath(activity, selectedImageURI!!))
                val requestBodyCoverPhoto =
                    file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                requestUploadChatApi.value = MultipartBody.Part.createFormData(
                    "chatStuff",
                    file?.name,
                    requestBodyCoverPhoto!!
                )
            }
        }

    /**
     * Permission Result Listener
     * */
    val requestPermissionsResult = object : PermissionHelper.OnRequestPermissionsResult {
        override fun onPermissionsGranted() {}
        override fun onPermissionsDenied() {
            permissionHelper?.checkAndRequestCameraPermission(this)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        val uri =
            URI.create(SyncConstants.WEB_SOCKET_CONNECTION_URL + "?token=" + SharedPrefs.getUser().Chat_Uniq_Number + "&&room=" + chatroom + "&&userID=" + SharedPrefs.getUser().id)
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {}
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

            override fun onClose(code: Int, reason: String, remote: Boolean) {}
            override fun onError(ex: Exception) {}
        }
        webSocketClient!!.connect()
    }

    /**
     * On Send Message
     * */
    private fun sendMessage(message: String?) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("userID", SharedPrefs.getUser().id)
        jsonObject.addProperty("recieverID", receiverId)
        jsonObject.addProperty("msg", message.toString())
        jsonObject.addProperty("type", "Chat")
        jsonObject.addProperty("serviceType", "Chat")
        jsonObject.addProperty("MessageType", messageType)
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
        modalMessage.MessageType = messageType
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
     * Response Observe
     * */
    fun setObserverChatFileUpload() {
        uploadChatFile.observe(lifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data?.status == "200") {
                        sendMessage(it.data.fileName)
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