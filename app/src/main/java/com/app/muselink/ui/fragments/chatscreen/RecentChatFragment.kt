package com.app.muselink.ui.fragments.chatscreen

import android.content.Context
import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.FragmentRecentChatBinding
import com.app.muselink.listener.NotificationCountListener
import com.app.muselink.ui.base.fragment.BaseViewModelFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentChatFragment : BaseViewModelFragment<FragmentRecentChatBinding, RecentChatViewModel>() {
    var mCallback: NotificationCountListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.binding = binding
        binding?.vm = viewModel
        viewModel?.lifecycleOwner = this
        viewModel?.initRecyclerView()
        viewModel?.setObserverRecentChatListing(mCallback)
        viewModel?.getRecentChatListing()
    }

    override fun getViewModelClass(): Class<RecentChatViewModel> {
        return RecentChatViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.fragment_recent_chat
    }

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