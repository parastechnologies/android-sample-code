package com.highenergymind.view.activity.invite

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.FragmentInviteBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.copyToClipBoard
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.shareUrl
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.redemcode.RedeemCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint

class InviteActivity : BaseActivity<FragmentInviteBinding>() {
    val viewModel by viewModels<RedeemCodeViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.fragment_invite
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        setData()
        setUptool()
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


    private fun setData() {
        binding.apply {
            tvInviteCode.text = sharedPrefs.getUserData()?.inviteCode
        }
    }


    private fun setUptool() {
        binding.customTool.titleTV.visibility = View.VISIBLE
        binding.customTool.titleTV.text = getString(R.string.invite_friends)
    }

    private fun onClick() {
        binding.apply {
            customTool.backIV.setOnClickListener {
                finish()
            }
            enterCode.setOnEditorActionListener { _, actionId, _ ->

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.apply {
                        if (enterCode.text?.trim()?.toString().isNullOrEmpty()) {
                            showToast(getString(R.string.please_enter_redeem_code))
                        } else {
                            viewModel.apply {
                                map.clear()
                                map[ApiConstant.CODE] = enterCode.text?.trim()?.toString() ?: ""
                                redeemCodeApi()
                            }
                        }
                    }
                    true
                }
                false
            }
            conPaslockk.setOnClickListener {
                tvInviteCode.text?.trim()?.toString()?.copyToClipBoard(this@InviteActivity)
            }
            tvInviteCode.setOnClickListener {
                tvInviteCode.text?.trim()?.toString()?.copyToClipBoard(this@InviteActivity)
            }

            shareCodeBtn.setOnClickListener {
                val str =
                    getString(R.string.mail_subject_do_you_know_highenergymind_hi_there_i_found_this_app_and_think_you_might_like_it_www_highenergymind_app_there_you_can_listen_to_many_positive_affirmations_that_can_change_your_subconscious_mind_ina_positive_way_there_is_also_beautiful_music_to_relax_and_meditate_to_i_have_a_gift_for_you_if_you_enter_my_personal_code_after_you_sign_up_we_can_both_listen_to_the_premium_content_for_free_for_7_days_you_don_t_have_to_subscribe_it_s_a_real_gift_my_code_abcdefg_best_first_name,AppConstant.DOWNLOAD_APP_URL,sharedPrefs.getUserData()?.inviteCode,sharedPrefs.getUserData()?.firstName?.plus(" ${sharedPrefs.getUserData()?.lastName?:""}"),"******************")
                shareUrl(str, getString(R.string.do_you_know_highenergymind))

            }
        }
    }


}