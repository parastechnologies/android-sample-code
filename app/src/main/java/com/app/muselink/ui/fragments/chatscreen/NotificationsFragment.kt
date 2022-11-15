package com.app.muselink.ui.fragments.chatscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.notification.NotificationListResponseModel
import com.app.muselink.listener.NotificationCountListener
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.adapter.chatscreen.ChatNotificationsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat_unreadmessage.*
import javax.inject.Inject


@AndroidEntryPoint
class NotificationsFragment : BaseFragment() {
    private var rootView: View? = null
    private val requestApi = MutableLiveData<HashMap<String, String>>()

    @Inject
    lateinit var repository: ApiRepository
    private var chatUnReadMessageAdapter: ChatNotificationsAdapter? = null

    /**
     * Call get all notification
     * */
    private val notification = requestApi.switchMap { requestApi ->
        repository.notificationList(requestApi)
    }
    val response: LiveData<Resource<NotificationListResponseModel>> = notification

    /**
     * [onCreateView]
     * */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_chat_unreadmessage, container, false)
        return rootView
    }

    /**
     * [onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        callApi()
        setupObservers()
    }

    /**
     * Init Recycler View
     * */
    fun initRecyclerView() {
        chatUnReadMessageAdapter = ChatNotificationsAdapter(requireActivity())
        val linearLayoutManager = LinearLayoutManager(activity)
        rvChatUnReadMessage?.layoutManager = linearLayoutManager
        rvChatUnReadMessage!!.adapter = chatUnReadMessageAdapter
    }

    /**
     * Call api
     * */
    private fun callApi() {
        val map = HashMap<String, String>()
        map["userId"] = SharedPrefs.getUser().id.toString()
        requestApi.value = map
    }

    /**
     * Get description observer
     * */
    private fun setupObservers() {
        response.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.status.equals("200")) {
                            if (it.data.data != null) {
                                chatUnReadMessageAdapter?.setData(it.data.data!!)
                                noData.visibility = View.GONE
                                mCallback?.setCount(it.data.data!!.size.toString(), "Notification")
                            }
                        } else {
                            noData.visibility = View.VISIBLE
                        }
                    } else {
                        noData.visibility = View.VISIBLE
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    noData.visibility = View.VISIBLE
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }
    var mCallback: NotificationCountListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = context as NotificationCountListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + " must implement TextClicked"
            )
        }
    }

    override fun onDetach() {
        mCallback = null
        super.onDetach()
    }
}