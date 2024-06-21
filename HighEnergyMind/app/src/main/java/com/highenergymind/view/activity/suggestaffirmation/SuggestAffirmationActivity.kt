package com.highenergymind.view.activity.suggestaffirmation

import android.app.Dialog
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivitySuggestAffirmationBinding
import com.highenergymind.databinding.DialogRequestSentLayoutBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.activity.contactus.ContactUsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SuggestAffirmationActivity : BaseActivity<ActivitySuggestAffirmationBinding>() {
    val viewModel by viewModels<ContactUsViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_suggest_affirmation
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        setLocalData()

        onClick()
    }
    private fun setLocalData() {
        val user = viewModel.sharedPrefs.getUserData()!!
        binding.apply {
            etName.setText(user.firstName.plus(" ${user.lastName ?: ""}"))
            etEmail.setText(user.email)
        }
    }


    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                suggestAffirmResponse.collectLatest {
                    handleResponse(it, {
                        binding.etMessage.text?.clear()
                        showAlertDialogSentRequest()
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
    private fun onClick() {
        binding.customTool.backIV.setOnClickListener {
            finish()
        }
        binding.submitBtn.setOnClickListener {

            binding.apply {
                if (validator.suggestUsValidator(binding)) {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.NAME] = etName.text?.trim()?.toString()!!
                        map[ApiConstant.EMAIL] = etEmail.text?.trim()?.toString()!!
                        map[ApiConstant.MSG] = etMessage.text?.trim()?.toString()!!
                        suggestAffirmationApi()
                    }
                }
            }
        }
    }
    private fun showAlertDialogSentRequest() {
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        val binding: DialogRequestSentLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(dialog.context),
            R.layout.dialog_request_sent_layout,
            null,
            false
        )
        binding.submitBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(binding.root)
        dialog.show()

    }


}