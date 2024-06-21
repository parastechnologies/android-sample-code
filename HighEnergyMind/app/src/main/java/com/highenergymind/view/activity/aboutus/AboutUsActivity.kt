package com.highenergymind.view.activity.aboutus

import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.PageContentResponse
import com.highenergymind.data.PageData
import com.highenergymind.databinding.ActivityAboutUsBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.activity.termsconditions.PageContentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutUsActivity : BaseActivity<ActivityAboutUsBinding>() {
    val viewModel by viewModels<PageContentViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_about_us
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolTitle()
        onClick()
        setCollectors()
        getContent()
    }

    private fun onClick() {
        binding.customTool.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setToolTitle() {
        binding.customTool.tvTitle.text = getString(R.string.about_us)
    }

    private fun getContent() {
        viewModel.apply {
            map.clear()
            map[ApiConstant.PAGE] = "about"
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