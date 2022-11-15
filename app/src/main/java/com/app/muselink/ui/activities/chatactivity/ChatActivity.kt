package com.app.muselink.ui.activities.chatactivity

import android.os.Bundle
import com.app.muselink.R
import com.app.muselink.databinding.ActivityChatBinding
import com.app.muselink.helpers.PermissionHelper
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import com.app.muselink.util.finishActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*

@AndroidEntryPoint
class ChatActivity : BaseViewModelActivity<ActivityChatBinding, ChatViewModel>() {
    var permissionHelper: PermissionHelper? = null

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.activity_chat
    }
    /**
     * [ChatViewModel]
     * */
    override fun getViewModelClass(): Class<ChatViewModel> {
        return ChatViewModel::class.java
    }
    /**
     * [onCreate]
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.vm = viewModel
        viewModel?.binding = binding
        viewModel?.lifecycleOwner = this
        viewModel?.getIntentData()
        viewModel?.initRecyclerView()
        viewModel?.setObserverChatListing()
        viewModel?.setObserverChatFileUpload()
        viewModel?.getChatListing()
        setListeners()
    }
    /**
     * Click listener
     * */
    private fun setListeners() {
        nmcChatBackPRess.setOnClickListener {
            finishActivity()
        }
    }

    /**
     *[onRequestPermissionsResult]
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        viewModel?.connectionToWebSocket()
    }

    override fun onDestroy() {
        viewModel?.onDispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finishActivity()
    }
}