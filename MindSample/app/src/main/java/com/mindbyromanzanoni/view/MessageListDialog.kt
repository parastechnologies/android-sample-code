package com.mindbyromanzanoni.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseFragment
import com.mindbyromanzanoni.data.response.chatUsers.ChatUsers
import com.mindbyromanzanoni.databinding.ItemMessageListBinding
import com.mindbyromanzanoni.databinding.MessageDialogBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.setSearchTextWatcher
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.view.activity.chatList.UserChatListActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessageListDialog : BaseFragment<MessageDialogBinding>(MessageDialogBinding::inflate) {
    private val viewModal: HomeViewModel by viewModels()
    private var filteredList: ArrayList<ChatUsers> = arrayListOf()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDataFromViewModal()
        RunInScope.ioThread {
            viewModal.hitChatUsersApi()
        }
    }

    /** Observer Response via View model*/
    fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.chatUsersSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            filteredList = data.data
                            getWatcherSearchMeditation()
                            initMessageListRecyclerView()
                        } else {
                            showErrorSnack(requireActivity(), data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(requireActivity(), msg)
                        }
                    }
                }
            }
        }
        viewModal.showLoading.observe(requireActivity()) {
            binding.apply {
                shimmerCommentList.shimmerAnimationEffect(it)
            }
        }
        binding.ivImg.setOnClickListener {
            if (binding.etSearch.text.isNullOrEmpty().not()) {
                binding.etSearch.setText("")
            }
        }
    }

    private fun getWatcherSearchMeditation() {
        // Call the extension function to set up the text watcher
        binding.etSearch.setSearchTextWatcher { query ->
            if (query.isEmpty()) {
                binding.ivImg.setImageResource(R.drawable.search)
            } else {
                binding.ivImg.setImageResource(R.drawable.rating_close)
            }
            filter(query)
        }
    }

    /** Function to filter data based on search query*/
    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val list = if (query.isEmpty()) {
            filteredList
        } else {
            filteredList.filter { item ->
                item.name.contains(query, ignoreCase = true)
            }
        }
        if (list.isEmpty()) {
            resourceListAdapter.submitList(list)
        } else {
            resourceListAdapter.submitList(list)
        }
    }

    /** set recycler view Message  List */
    private fun initMessageListRecyclerView() {
        binding.rvNotification.adapter = resourceListAdapter
        resourceListAdapter.submitList(filteredList)
    }

    private val resourceListAdapter = object : GenericAdapter<ItemMessageListBinding, ChatUsers>() {
        override fun getResourceLayoutId(): Int {
            return R.layout.item_message_list
        }

        override fun onBindHolder(
            holder: ItemMessageListBinding,
            dataClass: ChatUsers,
            position: Int
        ) {
            holder.tvUserName.text = dataClass.name
            //holder.tvEmail.text = dataClass.email
            if (dataClass.name.isNotEmpty()) {
                val name: String = dataClass.name.substring(0,1)
                holder.tvFirstChar.text = name
            }
          //  holder.circleImageView.setImageFromUrl(R.drawable.placeholder_mind, dataClass.userImage)

            holder.root.setOnClickListener {
                val bundle = bundleOf(AppConstants.RECEIVER_USER_ID to dataClass.userId)
                requireActivity().launchActivity<UserChatListActivity>(0, bundle) { }
                //dismiss()
            }
        }
    }
}