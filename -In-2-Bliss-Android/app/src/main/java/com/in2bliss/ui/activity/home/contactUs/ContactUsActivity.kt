package com.in2bliss.ui.activity.home.contactUs

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityContactUsBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.profileDetail.ProfileDetailViewModel
import com.in2bliss.utils.extension.checkEmail
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ContactUsActivity : BaseActivity<ActivityContactUsBinding>(R.layout.activity_contact_us) {

    val viewModel: ProfileDetailViewModel by viewModels()

    override fun init() {

        binding.tbToolbar.tvTitle.text = getString(R.string.contact_us)
        binding.tbToolbar.ivBack.setOnClickListener {
            finish()
        }
        onClickView()
        observe()
    }

    private fun onClickView() {
        binding.btnContinue.setOnClickListener {
            if (validateContactUs()) {
                viewModel.name = binding.etName.text.toString().trim()
                viewModel.email = binding.emailET.text.toString().trim()
                viewModel.message = binding.messagesET.text.toString().trim()
                viewModel.contactUs()
            }
        }
        binding.tvSkip.setOnClickListener {
            finish()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.contactResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ContactUsActivity,
                    success = { response ->
                        response.message?.let { it1 -> showToast(it1) }
                        finish()
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    private fun validateContactUs(): Boolean {
        return when {
            binding.etName.text.toString().isBlank() -> {
                showToast(getString(R.string.enter_your_name))
                false
            }

            binding.emailET.text.toString().isBlank() -> {
                showToast(getString(R.string.enter_email_address))
                false
            }

            (!checkEmail(binding.emailET.text.toString().trim())) -> {
                showToast(message = getString(R.string.please_enter_valid_email_address))
                false
            }

            binding.messagesET.text.toString().isBlank() -> {
                showToast(getString(R.string.enter_your_message))
                false
            }

            else -> {
                true
            }
        }
    }

}