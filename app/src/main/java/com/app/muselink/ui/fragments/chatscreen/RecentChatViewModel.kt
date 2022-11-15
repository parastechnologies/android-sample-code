package com.app.muselink.ui.fragments.chatscreen

import android.app.Activity
import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.RecentChatRes
import com.app.muselink.databinding.FragmentRecentChatBinding
import com.app.muselink.listener.NotificationCountListener
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.activities.chatactivity.ChatActivity
import com.app.muselink.ui.adapter.chatscreen.ChatUnReadMessageAdapter
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.fragments.chatscreen.modals.RecentChatDetails
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.intentComponent

class RecentChatViewModel @ViewModelInject constructor(
    val repository: ApiRepository, override var activity: Activity
) : BaseViewModel(activity) {
    var binding: FragmentRecentChatBinding? = null
    var lifecycleOwner: LifecycleOwner? = null
    private val requestApi = MutableLiveData<HashMap<String, String>>()
    private var chatNotificationsAdapter: ChatUnReadMessageAdapter? = null
    private val _getRecentChat = requestApi.switchMap { requestApi ->
        repository.getRecentChat(requestApi)
    }
    private val recentChatRes: LiveData<Resource<RecentChatRes>> = _getRecentChat
    fun getRecentChatListing() {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApi.value = request
    }

    val chatUnReadMessageAdapterNavigator = object :
        ChatUnReadMessageAdapter.ChatUnReadMessageAdapterNavigator {
        override fun onClickItem(position: Int) {
            val bundle = bundleOf(AppConstants.RECENT_CHAT_MODAL to listRecentChat[position])
            activity.intentComponent(ChatActivity::class.java, bundle)
        }
    }
    var listRecentChat = ArrayList<RecentChatDetails>()

    /**
     * Init Recycler view
     * */
    fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        binding?.rvChatNotifications?.layoutManager = linearLayoutManager
        chatNotificationsAdapter =
            ChatUnReadMessageAdapter(activity, chatUnReadMessageAdapterNavigator, listRecentChat)
        binding?.rvChatNotifications!!.adapter = chatNotificationsAdapter
    }

    /**
     * Recent Chat Response Observer
     * */
    fun setObserverRecentChatListing(mCallback: NotificationCountListener?) {
        recentChatRes.observe(lifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data.isNullOrEmpty().not()) {
                                binding?.noDataFound = true
                                listRecentChat = it.data.data!!
                                chatNotificationsAdapter?.updateList(listRecentChat)
                                mCallback?.setCount(it.data.data!!.size.toString(), "RecentChat")
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
}
