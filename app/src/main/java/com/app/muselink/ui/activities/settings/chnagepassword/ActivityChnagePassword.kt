package com.app.muselink.ui.activities.settings.chnagepassword

import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.ActivityChangePasswordBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

@AndroidEntryPoint
class ActivityChnagePassword : BaseViewModelActivity<ActivityChangePasswordBinding, ChangePasswordViewModel>(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding?.vm = viewModel
        viewModel?.binding = binding
        viewModel?.viewLifecycleOwner = this
        viewModel?.setUpObserverChnagePassword()

        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.visibility = View.GONE
        tvHeading.setText(getString(R.string.change_password))

    }

    override fun getViewModelClass(): Class<ChangePasswordViewModel> {
       return ChangePasswordViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.activity_change_password
    }

}