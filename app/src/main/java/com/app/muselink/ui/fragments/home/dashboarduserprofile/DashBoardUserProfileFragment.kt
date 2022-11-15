package com.app.muselink.ui.fragments.home.dashboarduserprofile

import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.FragmentDashboardUserprofileBinding
import com.app.muselink.ui.base.fragment.BaseViewModelFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashBoardUserProfileFragment :
    BaseViewModelFragment<FragmentDashboardUserprofileBinding, DashBoardUserProfileViewModel>() {

    override fun getViewModelClass(): Class<DashBoardUserProfileViewModel> {
        return DashBoardUserProfileViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.fragment_dashboard_userprofile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.binding = binding
        binding?.vm = viewModel
        viewModel?.lifecycleOwner = this
        viewModel?.init()
        viewModel?.setObserverUserListing()
        viewModel?.setObserverFavUser()
        viewModel?.getUserList()
    }
}