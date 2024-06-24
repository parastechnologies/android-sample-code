package com.in2bliss.ui.activity.home.profileManagement.about

import androidx.activity.viewModels
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity<ActivityAboutBinding>(
    layout = R.layout.activity_about
) {

    private val viewModel: AboutVM by viewModels()

    override fun init() {
        binding.data = viewModel
        binding.toolBar.tvTitle.text = getString(R.string.about_jess)
        binding.toolBar.ivBack.setOnClickListener { finish() }
    }
}

