package com.app.muselink.ui.activities.settings.blockaccount

import android.os.Bundle
import com.app.muselink.R
import com.app.muselink.databinding.ActivityBlockedAccountBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

@AndroidEntryPoint
class BlockedAccountActiivty : BaseViewModelActivity<ActivityBlockedAccountBinding,BlockAccountViewmodel>(){

    override fun getLayout(): Int {
        return R.layout.activity_blocked_account
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding?.vm = viewModel
        viewModel?.viewLifecycleOwner = this
        viewModel?.binding = binding
        viewModel?.setupObservers()
        viewModel?.setupObserversUnBlock()
        viewModel?.initRecyclerView()
        viewModel?.callApiGetBlockAccounts()
        setToolbar()
    }

    private fun setToolbar() {
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.setText(getString(R.string.manage_my_account))
    }


    override fun getViewModelClass(): Class<BlockAccountViewmodel> {
        return BlockAccountViewmodel::class.java
    }

}