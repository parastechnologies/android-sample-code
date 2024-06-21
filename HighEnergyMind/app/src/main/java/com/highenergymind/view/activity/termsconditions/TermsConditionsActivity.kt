package com.highenergymind.view.activity.termsconditions

import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.PageContentResponse
import com.highenergymind.data.PageData
import com.highenergymind.databinding.ActivityTermsConditionsBinding
import com.highenergymind.utils.fullScreenStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TermsConditionsActivity : BaseActivity<ActivityTermsConditionsBinding>() {
    val viewModel by viewModels<PageContentViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_terms_conditions
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolTitle()
        setCollectors()
        onClick()
        getContent()

    }

    private fun getContent() {
        viewModel.apply {
            map.clear()
            map[ApiConstant.PAGE] = "term"
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

    private fun onClick() {
        binding.customTool.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setToolTitle() {
        binding.customTool.tvTitle.text = getString(R.string.terms_and_conditions)
    }

}