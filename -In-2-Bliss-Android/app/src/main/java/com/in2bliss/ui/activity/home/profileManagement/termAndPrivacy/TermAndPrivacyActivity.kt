package com.in2bliss.ui.activity.home.profileManagement.termAndPrivacy

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityTermAndPrivacyBinding
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TermAndPrivacyActivity : BaseActivity<ActivityTermAndPrivacyBinding>(
    layout = R.layout.activity_term_and_privacy
) {
    private var terms=""

    override fun init() {
        getIntentData()
        ProgressDialog.showProgress(this)
        binding.toolBar.ivBack.setOnClickListener { finish() }
        recyclerView()
    }

    private fun getIntentData() {
        val intent = intent.extras
        terms = intent?.getString(AppConstant.TERMS_CONDITION).toString()
        when (terms) {
            AppConstant.TERMS_CONDITION -> {
                binding.toolBar.tvTitle.text = getString(R.string.terms_condition)
                binding.webview.loadUrl("https://in2bliss.com.au/terms")

            }
            AppConstant.PRIVACY_POLICY -> {
                binding.toolBar.tvTitle.text = getString(R.string.privacy_policy)
                binding.webview.loadUrl("https://in2bliss.com.au/privacy")
            }
            else -> {
                binding.clTerms.gone()
                binding.clJess.visible()
                binding.toolBar.tvTitle.text = getString(R.string.about_in2bliss)
                binding.webviewJess.loadUrl("https://in2bliss.com.au/about")
            }
        }

    }

    private fun recyclerView() {
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress == 100) {
                    ProgressDialog.hideProgress()
                }
            }
        }
        binding.webviewJess.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress == 100) {
                    ProgressDialog.hideProgress()
                }
            }
        }
    }


}

