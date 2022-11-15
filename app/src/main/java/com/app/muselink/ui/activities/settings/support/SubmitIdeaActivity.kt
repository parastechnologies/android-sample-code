package com.app.muselink.ui.activities.settings.support

import android.os.Bundle
import androidx.lifecycle.Observer
import com.app.muselink.R
import com.app.muselink.databinding.ActivitySubmitIdeaBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

@AndroidEntryPoint
class SubmitIdeaActivity :  BaseViewModelActivity<ActivitySubmitIdeaBinding, SupportViewModel>(){

    private fun setToolbar() {
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.setText(getString(R.string.submit_an_idea))
    }

    override fun getLayout(): Int {
        return R.layout.activity_submit_idea
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.vm = viewModel
        viewModel?.supportType = SyncConstants.SupportTypes.SUBMIT_A_IDEA.value
        viewModel?.enableButton?.set(true)
        setupObservers()
        setToolbar()
    }


    private fun setupObservers() {

        viewModel?._supportRes?.observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    viewModel?.showLoader?.set(false)
                    viewModel?.enableButton?.set(true)
                    if (it.data != null) {
                        if (it.data!!.isSuccess()) {
                            showToast(this, it.data.message)
                            finish()
                        }else {
                            showToast(this, it.data.message)
                        }
                    }else {
                        showToast(this, it.data?.message)
                    }
                }
                Resource.Status.ERROR -> {
                    viewModel?.showLoader?.set(false)
                    viewModel?.enableButton?.set(true)
                    showToast(this, it.message)
                }
                Resource.Status.LOADING -> {
                    viewModel?.showLoader?.set(true)
                    viewModel?.enableButton?.set(false)
                }
            }
        })

    }

    override fun getViewModelClass(): Class<SupportViewModel> {
        return SupportViewModel::class.java
    }

}