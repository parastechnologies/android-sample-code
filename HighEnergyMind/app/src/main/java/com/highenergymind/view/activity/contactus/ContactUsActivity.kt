package com.highenergymind.view.activity.contactus

import android.app.Dialog
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityContactUsBinding
import com.highenergymind.databinding.DialogRequestSentLayoutBinding
import com.highenergymind.utils.fullScreenStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactUsActivity : BaseActivity<ActivityContactUsBinding>() {
    val viewModel by viewModels<ContactUsViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_contact_us
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
                contactUsResponse.collectLatest {
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
        binding.apply {
            customTool.backIV.setOnClickListener {
                finish()
            }
            submitBtn.setOnClickListener {
                if (validator.contactUsValidator(binding)){
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.NAME]=etName.text?.trim()?.toString()!!
                        map[ApiConstant.EMAIL]=etEmail.text?.trim()?.toString()!!
                        map[ApiConstant.MSG]=etMessage.text?.trim()?.toString()!!
                        contactUsApi()
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