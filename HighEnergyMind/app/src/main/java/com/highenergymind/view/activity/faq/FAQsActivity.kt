package com.highenergymind.view.activity.faq

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.Faq
import com.highenergymind.data.GetFaqResponse
import com.highenergymind.databinding.ActivityFaqsBinding
import com.highenergymind.`interface`.OnClick
import com.highenergymind.utils.fullScreenStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FAQsActivity : BaseActivity<ActivityFaqsBinding>() {
    val viewModel by viewModels<FaqViewModel>()
    lateinit var faqAdapter: FaqAdapter
    override fun getLayoutRes(): Int {
        return R.layout.activity_faqs
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolTitle()
        setCollectors()
        onClick()
        viewModel.getFaqApi()
    }

    private fun setToolTitle() {
        binding.customTool.tvTitle.text = getString(R.string.faqs)
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                faqResponse.collectLatest {
                    handleResponse(it, { resp ->
                        val response = resp as GetFaqResponse
                        setUpRecyclerView(response.data)
                    })
                }
            }
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
        }
    }


    private fun setUpRecyclerView(data: List<Faq>) {


        faqAdapter = FaqAdapter(this, data, object : OnClick {
            override fun onClickPosition(position: String?) {

            }

            override fun showSubcate() {

            }
        })
        binding.faqRV.adapter = faqAdapter

    }

    private fun onClick() {
        binding.apply {
            customTool.ivBack.setOnClickListener {
                finish()
            }
        }

    }

}