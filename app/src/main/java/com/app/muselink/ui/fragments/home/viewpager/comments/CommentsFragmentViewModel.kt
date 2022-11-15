package com.app.muselink.ui.fragments.home.viewpager.comments

import android.app.Activity
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.comment.CommentListingResponse
import com.app.muselink.data.modals.responses.comment.CommentResponse
import com.app.muselink.data.modals.responses.comment.CommentResponseData
import com.app.muselink.databinding.FragmentCommentsBinding
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.adapter.Comments_Adapter
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.hideKeyboard
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI


class CommentsFragmentViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    private var webSocketClient: WebSocketClient? = null
    var binding: FragmentCommentsBinding? = null
    var mCommentsAdapter: Comments_Adapter? = null
    var messageArrayList: ArrayList<CommentResponseData>? = null
    var comment = ObservableField<String>()

    /**
     * Initialize RecyclerView
     * */
    fun initRecyclerView() {
        messageArrayList = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity)
        binding?.rvCommentData?.layoutManager = linearLayoutManager
        mCommentsAdapter = Comments_Adapter(activity, messageArrayList!!)
        binding?.rvCommentData!!.adapter = mCommentsAdapter
    }

    /**
     * Send Comment
     * */
    fun sendComment() {
        if (webSocketClient != null) {
            if (comment.get().isNullOrEmpty().not()) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("user_id", SharedPrefs.getUserId())
                jsonObject.addProperty("music_id", SingletonInstances.currentAudioFilePlay?.audioId)
                jsonObject.addProperty("comment", comment.get().toString())
                jsonObject.addProperty("type", "add_comment")
                jsonObject.addProperty("serviceType", "Commenting")
                webSocketClient?.send(jsonObject.toString())
            } else {
                showToast(activity, activity.getString(R.string.please_enter_comment))
            }
        } else {
            showToast(activity, "Something went wrong")
        }
    }

    /**
     * Connect Web socket
     * */
    @Throws(Exception::class)
    fun connectionToWebSocket() {
        messageArrayList?.clear()
        if (mCommentsAdapter != null) {
            mCommentsAdapter?.updateListComments(messageArrayList!!)
        }
        val uri = URI.create(SyncConstants.WEB_SOCKET_CONNECTION_URL)
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("music_id", SingletonInstances.currentAudioFilePlay?.audioId)
                jsonObject.addProperty("type", "commentList")
                jsonObject.addProperty("serviceType", "Commenting")
                webSocketClient?.send(jsonObject.toString())
            }

            override fun onMessage(message: String) {
                if (message != "null") {
                    val jsonObject = JSONObject(message)
                    if (jsonObject["Response"] == "true" && jsonObject["type"] == "commentList") {
                        val gson = Gson()
                        val model: CommentListingResponse =
                            gson.fromJson(jsonObject.toString(), CommentListingResponse::class.java)
                        messageArrayList?.addAll(model.data!!)
                        CoroutineScope(Dispatchers.Main).launch {
                            mCommentsAdapter?.updateListComments(messageArrayList!!)
                            binding?.rvCommentData?.scrollToPosition(mCommentsAdapter!!.itemCount - 1)
                        }
                    }
                    if (jsonObject["Response"] == "true" && jsonObject["type"] == "add_comment") {
                        val gson = Gson()
                        val model: CommentResponse =
                            gson.fromJson(jsonObject.toString(), CommentResponse::class.java)
                        if (model.isSuccess()) {
                            if (model.data!!.musicID!! == SingletonInstances.currentAudioFilePlay?.audioId) {
                                messageArrayList?.add(model.data!!)
                                CoroutineScope(Dispatchers.Main).launch {
                                    mCommentsAdapter?.updateListComments(messageArrayList!!)
                                    comment.set("")
                                    activity.hideKeyboard()
                                    binding?.rvCommentData?.scrollToPosition(mCommentsAdapter!!.itemCount - 1)
                                }
                            }
                        } else {
                            showToast(activity, model.message)
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        binding?.commentsCounts?.text = messageArrayList?.size.toString()
                    }
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {}
            override fun onError(ex: Exception) {}
        }
        webSocketClient!!.connect()
    }

    fun onDispose() {
        if (webSocketClient != null && webSocketClient!!.isOpen) {
            webSocketClient!!.close()
        }
    }
}
