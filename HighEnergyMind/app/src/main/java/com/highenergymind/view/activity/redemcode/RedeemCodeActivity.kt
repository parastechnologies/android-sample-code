package com.highenergymind.view.activity.redemcode

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.ActivityRedeemCodeBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.invite.InviteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RedeemCodeActivity : BaseActivity<ActivityRedeemCodeBinding>() {
    val viewModel by viewModels<RedeemCodeViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_redeem_code
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        onClick()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                redeemCodeResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        sharedPrefs.saveUserData(response.data!!)
                        finish()
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
        binding.customtool.backIV.setOnClickListener {
            finish()
        }
        binding.redemCodeBtn.setOnClickListener {
            binding.apply {
                if (codeEdt.text?.trim()?.toString().isNullOrEmpty()) {
                    showToast(getString(R.string.please_enter_redeem_code))
                } else {
                    viewModel.apply {
                        map.clear()
                        map[ApiConstant.CODE] = codeEdt.text?.trim()?.toString() ?: ""
                        redeemCodeApi()
                    }
                }
            }
        }
        binding.invitefriendTV.setOnClickListener {
            intentComponent(InviteActivity::class.java, null)
        }
    }


}