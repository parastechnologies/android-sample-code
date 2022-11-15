package com.app.muselink.ui.bottomsheets

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.comment.CommentListingResponse
import com.app.muselink.data.modals.responses.comment.CommentResponse
import com.app.muselink.data.modals.responses.comment.CommentResponseData
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.adapter.SmileyAdapter
import com.app.muselink.ui.adapter.profile.CommentsAdapterBlackTheme
import com.app.muselink.util.hideKeyboard
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_commenst_black.*
import kotlinx.android.synthetic.main.fragment_commenst_black.imgSendComment
import kotlinx.android.synthetic.main.layout_view_all_comments_bottomsheet.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class BottomSheetViewallComments (context: Context,val modalAudioFile: ModalAudioFile) : BottomSheetDialogFragment(){

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    private var smileyArraylist = ArrayList<String>()

    private var rootView: View? = null
    private var mComments_Adapter: CommentsAdapterBlackTheme? = null
    private var mSmiley_Adapter: SmileyAdapter? = null
    var rv_commentdata: RecyclerView? = null
    var rv_smiley: RecyclerView? = null
    var btnCloseComment: ImageView? = null
    var smiley_Arraylist = ArrayList<Int>()
    var messageArrayList: ArrayList<CommentResponseData>? = null
    private var webSocketClient: WebSocketClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView =  inflater.inflate(R.layout.layout_view_all_comments_bottomsheet, container, false)
        messageArrayList = ArrayList()
        rv_commentdata = rootView?.findViewById(R.id.rv_commentdata)
        rv_smiley = rootView?.findViewById(R.id.rv_smiley)
        rv_smiley = rootView?.findViewById(R.id.rv_smiley)
        btnCloseComment = rootView?.findViewById(R.id.btnCloseComment)

        btnCloseComment?.setOnClickListener {
            dismiss()
        }


        smiley_Arraylist.add(R.drawable.ic_love_face)
        smiley_Arraylist.add(R.drawable.ic_angry_face)
        smiley_Arraylist.add(R.drawable.ic_smile_face)
        smiley_Arraylist.add(R.drawable.ic_confuse_face)
        smiley_Arraylist.add(R.drawable.ic_laughing_face)
        smiley_Arraylist.add(R.drawable.ic_shock_face)
        smiley_Arraylist.add(R.drawable.ic_upset_face)
        smiley_Arraylist.add(R.drawable.ic_happy_face)
        smiley_Arraylist.add(R.drawable.ic_silent_face)

        init()


        return rootView



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgSendComment?.setOnClickListener {
            sendComment()
        }

        connectionToWebSocket()

    }

    fun init() {
        initRecyclerView()
        initRecyclerView_smiley()
    }

    fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        rv_commentdata?.layoutManager = linearLayoutManager
        mComments_Adapter = CommentsAdapterBlackTheme(requireActivity(),messageArrayList)
        rv_commentdata!!.adapter = mComments_Adapter
    }

    fun sendComment() {
        if (webSocketClient != null) {
            if (edtCommentBottomSheet.text.isNullOrEmpty().not()) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("user_id", SharedPrefs.getUserId())
                jsonObject.addProperty("music_id", modalAudioFile?.audioId)
                jsonObject.addProperty("comment", edtCommentBottomSheet.text.toString())
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
                jsonObject.addProperty("music_id", modalAudioFile?.audioId)
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
                            mComments_Adapter?.updateListComments(messageArrayList!!)
                            rvCommentData?.scrollToPosition(mComments_Adapter!!.itemCount - 1)
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
                                    mComments_Adapter?.updateListComments(messageArrayList!!)
                                    edtCommentBottomSheet.setText("")
                                    requireActivity().hideKeyboard()
                                    rvCommentData?.scrollToPosition(mComments_Adapter!!.itemCount - 1)
                                }
                            }
                        } else {
                            showToast(requireActivity(), model.message)
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        commentsCountsBottomSheet?.text = messageArrayList?.size.toString()
                    }
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {}
            override fun onError(ex: Exception) {}
        }
        webSocketClient!!.connect()
    }



    fun initRecyclerView_smiley() {
        val linearLayoutManager = LinearLayoutManager( activity,
            LinearLayoutManager.HORIZONTAL,
            false)
        rv_smiley?.layoutManager = linearLayoutManager
        mSmiley_Adapter = SmileyAdapter(requireActivity(),smileys())
        mSmiley_Adapter?.listener = object : SmileyAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                var stringBuilder = StringBuilder(edtCommentBottomSheet?.text.toString())
                stringBuilder = stringBuilder.append(smileyArraylist[position])
                edtCommentBottomSheet?.setText(stringBuilder)
                edtCommentBottomSheet?.setSelection(edtCommentBottomSheet?.text.toString().length)
            }
        }
        rv_smiley!!.adapter = mSmiley_Adapter
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }

    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View?>(bottomSheet!!)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet!!.getLayoutParams()
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet?.setLayoutParams(layoutParams)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        return displayMetrics.heightPixels
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