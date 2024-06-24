package com.in2bliss.ui.activity.auth.stepOne

import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityStepOneBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.auth.stepTwo.StepTwoActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StepOneActivity : BaseActivity<ActivityStepOneBinding>(
    layout = R.layout.activity_step_one
) {

    private val viewModel: StepOneViewModel by viewModels()

    override fun init() {
        binding.letTakeAMoment.visible()
        settingRecyclerView()
        onclick()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.signupReasonsResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@StepOneActivity,
                    success = { response ->
                        viewModel.adapter.submitList(response.data)
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

    private fun onclick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.blueBackground.setOnClickListener {
            binding.letTakeAMoment.gone()
            viewModel.retryApiRequest(
                apiName = ApiConstant.SIGNUP_REASONS
            )
        }

        binding.btnContinue.setOnClickListener {
            gettingSelectedReasons()

            if (viewModel.reasonId != null) {
                val bundle = bundleOf(
                    AppConstant.REASON_ID to viewModel.reasonId
                )
                intent(
                    destination = StepTwoActivity::class.java,
                    bundle = bundle
                )
                return@setOnClickListener
            }
            showToast(getString(R.string.please_select_reason))
        }
    }

    private fun gettingSelectedReasons() {
        var selected = ""
        viewModel.adapter.currentList.forEach { data ->
            if (data.isSelected == true) {
                selected += "${data.id},"
            }
        }
        if (selected.contains(",")) {
            selected = selected.substring(0, selected.length - 1)
        }
        viewModel.reasonId = selected
    }

    private fun settingRecyclerView() {
//        val layoutManger = FlexboxLayoutManager(this)
//        layoutManger.flexDirection = FlexDirection.ROW
//        binding.rvCategory.layoutManager = layoutManger
        binding.rvCategory.adapter = viewModel.adapter
        (binding.rvCategory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }
}

