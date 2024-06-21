package com.highenergymind.view.activity.privacypolicy

import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.PageContentResponse
import com.highenergymind.data.PageData
import com.highenergymind.databinding.ActivityPrivacyPolicyBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.activity.termsconditions.PageContentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>() {
    val viewModel by viewModels<PageContentViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_privacy_policy
    }

    override fun initView() {
        fullScreenStatusBar()
            setToolTitle()
        getBundleData()
        setCollectors()
        getContent()
        onClick()
    }

    private fun getBundleData() {
        if (intent.hasExtra(getString(R.string.name))) {
            binding.customTool.tvTitle.text = intent.getStringExtra(getString(R.string.name))
        }
    }

    private fun onClick() {
        binding.customTool.ivBack.setOnClickListener {
            finish()
        }
    }



    private fun setToolTitle() {
        binding.customTool.tvTitle.text = getString(R.string.privacy_policy)
    }

    private fun getContent() {
        viewModel.apply {
            map.clear()
            map[ApiConstant.PAGE] = "privacy"
            getPageContent()
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            lifecycleScope.launch {
                pageContentResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as PageContentResponse

                        setData(response.data)
                    })
                }
            }
        }
    }

    private fun setData(data: PageData) {
        binding.apply {
            tvData.movementMethod = ScrollingMovementMethod()

            tvData.text = data.description

        }
    }


}