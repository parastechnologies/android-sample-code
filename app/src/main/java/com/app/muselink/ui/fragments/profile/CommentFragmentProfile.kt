package com.app.muselink.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.comment.CommentListingResponse
import com.app.muselink.data.modals.responses.comment.CommentResponse
import com.app.muselink.data.modals.responses.comment.CommentResponseData
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.adapter.SmileyAdapter
import com.app.muselink.ui.adapter.profile.CommentsAdapterBlackTheme
import com.app.muselink.ui.bottomsheets.BottomSheetViewallComments
import com.app.muselink.util.hideKeyboard
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_commenst_black.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI


class CommentFragmentProfile(val modalAudioFile: ModalAudioFile) : BaseFragment() {


    private var rootView: View? = null
    private var mCommentsAdapter: CommentsAdapterBlackTheme? = null
    private var mSmileyAdapter: SmileyAdapter? = null
    var rvCommentData: RecyclerView? = null
    var rvSmiley: RecyclerView? = null
    private var smileyArraylist = ArrayList<String>()
    private var webSocketClient: WebSocketClient? = null
    var messageArrayList: ArrayList<CommentResponseData>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_commenst_black, container, false)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        init()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageArrayList = ArrayList()
        rvCommentData = rootView?.findViewById(R.id.rvCommentData)
        rvSmiley = rootView?.findViewById(R.id.rvSmiley)
        viewAllComments.setOnClickListener {
            BottomSheetViewallComments(requireActivity(),modalAudioFile).show(childFragmentManager, "AllComments")
        }
        init()
        connectionToWebSocket()

        imgSendComment?.setOnClickListener {
            sendComment()
        }
    }

    fun init() {
        initRecyclerView()
        initRecyclerViewSmiley()
    }

    fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        rvCommentData?.layoutManager = linearLayoutManager
        mCommentsAdapter = CommentsAdapterBlackTheme(requireContext(),messageArrayList)
        rvCommentData!!.adapter = mCommentsAdapter
    }

    fun sendComment() {
        if (webSocketClient != null) {
            if (edtComment.text.isNullOrEmpty().not()) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("user_id", SharedPrefs.getUserId())
                jsonObject.addProperty("music_id", modalAudioFile.audioId)
                jsonObject.addProperty("comment", edtComment.text.toString())
                jsonObject.addProperty("type", "add_comment")
                jsonObject.addProperty("serviceType", "Commenting")
                webSocketClient?.send(jsonObject.toString())
            } else {
                showToast(requireActivity(), requireActivity().getString(R.string.please_enter_comment))
            }
        } else {
            showToast(requireActivity(), "Something went wrong")
        }
    }
    /**
     * Connect Web socket
     * */
    @Throws(Exception::class)
    fun connectionToWebSocket() {
        messageArrayList?.clear()
        val uri = URI.create(SyncConstants.WEB_SOCKET_CONNECTION_URL)
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("music_id", modalAudioFile.audioId)
                jsonObject.addProperty("type", "commentList")
                jsonObject.addProperty("serviceType", "Commenting")
                webSocketClient?.send(jsonObject.toString())
            }
            override fun onMessage(message: String) {
                if (message != "null") {
                    val jsonObject = JSONObject(message)
                    if (jsonObject["Response"] == "true" && jsonObject["type"] == "commentList") {
                        val gson = Gson()
                        val model: CommentListingResponse = gson.fromJson(jsonObject.toString(), CommentListingResponse::class.java)
                        messageArrayList?.addAll(model.data!!)
                        CoroutineScope(Dispatchers.Main).launch {
                            mCommentsAdapter?.updateListComments(messageArrayList!!)
                            rvCommentData?.scrollToPosition(mCommentsAdapter!!.itemCount - 1)
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
                                    try {
                                        mCommentsAdapter?.updateListComments(messageArrayList!!)
                                        edtComment.setText("")
                                        requireActivity().hideKeyboard()
                                        rvCommentData?.scrollToPosition(mCommentsAdapter!!.itemCount - 1)
                                    }catch (e:java.lang.Exception){
                                        e.printStackTrace()
                                    }
                                }
                            }
                        } else {
                            showToast(requireActivity(), model.message)
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        commentsCounts?.text = messageArrayList?.size.toString()
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


    private fun initRecyclerViewSmiley() {
        val linearLayoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvSmiley?.layoutManager = linearLayoutManager
        mSmileyAdapter = SmileyAdapter(requireContext(), smileys())
        rvSmiley?.adapter = mSmileyAdapter
    }

    private fun smileys(): List<String> {
        smileyArraylist = ArrayList()
        smileyArraylist.add("😊")
        smileyArraylist.add("😆")
        smileyArraylist.add("😬")
        smileyArraylist.add("😐")
        smileyArraylist.add("🤔")
        smileyArraylist.add("😜")
        smileyArraylist.add("😏")
        smileyArraylist.add("😳")
        smileyArraylist.add("😍")
        return smileyArraylist
    }

}