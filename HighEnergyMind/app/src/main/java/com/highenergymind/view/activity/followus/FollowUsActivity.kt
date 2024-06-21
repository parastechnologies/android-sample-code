package com.highenergymind.view.activity.followus

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.FollowUsData
import com.highenergymind.data.FollowUsResponse
import com.highenergymind.databinding.ActivityFollowUsBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.shareUrl
import com.highenergymind.view.adapter.FollowUsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FollowUsActivity : BaseActivity<ActivityFollowUsBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    val viewModel by viewModels<FollowUsViewModel>()
    val adapter by lazy {
        FollowUsAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_follow_us
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        setTool()
        onClick()
        viewModel.getFollowUs()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            lifecycleScope.launch {
                followUsResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as FollowUsResponse
                        setDataOnUi(response.data)
                    })
                }
            }
        }
    }

    private fun setDataOnUi(data: List<FollowUsData>) {

        binding.apply {
            rvFollow.adapter = adapter
            adapter.submitList(data)

        }
    }

    private fun setTool() {
        binding.customTool.apply {
            tvTitle.text = getString(R.string.follow_us)
        }
    }

    private fun onClick() {
        binding.customTool.ivBack.setOnClickListener {
            finish()
        }
        binding.apply {
            llContainer.setOnClickListener {
                val str =
                    getString(R.string.mail_subject_do_you_know_highenergymind_hi_there_i_found_this_app_and_think_you_might_like_it_www_highenergymind_app_there_you_can_listen_to_many_positive_affirmations_that_can_change_your_subconscious_mind_ina_positive_way_there_is_also_beautiful_music_to_relax_and_meditate_to_i_have_a_gift_for_you_if_you_enter_my_personal_code_after_you_sign_up_we_can_both_listen_to_the_premium_content_for_free_for_7_days_you_don_t_have_to_subscribe_it_s_a_real_gift_my_code_abcdefg_best_first_name,
                        AppConstant.DOWNLOAD_APP_URL,sharedPrefs.getUserData()?.inviteCode,sharedPrefs.getUserData()?.firstName?.plus(" ${sharedPrefs.getUserData()?.lastName?:""}"),"******************")
                shareUrl(str, getString(R.string.do_you_know_highenergymind))
            }
        }

    }
}