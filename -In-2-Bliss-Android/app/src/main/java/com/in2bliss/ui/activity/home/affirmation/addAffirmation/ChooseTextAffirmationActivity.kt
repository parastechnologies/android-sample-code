package com.in2bliss.ui.activity.home.affirmation.addAffirmation

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityChooseTextAffirmationBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.ChooseBackgroundViewModel
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.affirmationCreated.AffirmationCreatedActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseTextAffirmationActivity : BaseActivity<ActivityChooseTextAffirmationBinding>(
    layout = R.layout.activity_choose_text_affirmation
) {

    private val viewModel: ChooseBackgroundViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.choose_a_background)
        binding.toolBar.tvSkip.gone()
        settingRecyclerView()
        viewModel.retryApiRequest(ApiConstant.AFFIRMATION_BACKGROUND_IMAGES)
        onClick()
        observer()
    }


    private fun observer() {
        lifecycleScope.launch {
            viewModel.adapterChooseBackground.addLoadStateListener {
                when (it.refresh) {
                    is LoadState.Error -> {
                        binding.tvNoDatFound.visibility(
                            isVisible = true
                        )
                    }

                    is LoadState.Loading -> {
                        binding.pbProgress.visible()
                    }

                    is LoadState.NotLoading -> {
                        binding.tvNoDatFound.visibility(
                            isVisible = viewModel.adapterChooseBackground.snapshot().isEmpty()
                        )
                        binding.pbProgress.gone()
                    }
                }
            }

            lifecycleScope.launch {
                viewModel.getAffirmationBackground().collectLatest { textAffirmationList ->
                    lifecycleScope.launch {
                        viewModel.adapterChooseBackground.submitData(textAffirmationList)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uploadData.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ChooseTextAffirmationActivity,
                    success = { response ->
                        viewModel.selectedImage = response.fileName
                        navigate()
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }
    }

    private fun navigate(
        isBackgroundImage: Boolean = true
    ) {
        viewModel.initialSelectedImage = viewModel.createAffirmation?.affirmationBackground
        viewModel.createAffirmation?.affirmationBackground =
            if (isBackgroundImage) viewModel.selectedImage else {
                if (viewModel.createAffirmation?.isEdit == true) viewModel.initialSelectedImage else null
            }
        viewModel.createAffirmation?.isGalleryImage = viewModel.isGalleryImageSelected
        val bundle = bundleOf(
            AppConstant.CREATE_AFFIRMATION to Gson().toJson(viewModel.createAffirmation)
        )
        intent(
            destination = AffirmationCreatedActivity::class.java,
            bundle = bundle
        )
    }

    private fun settingRecyclerView() {
        val layoutManger = GridLayoutManager(this, 2)
        binding.rvChooseBackground.layoutManager = layoutManger
        binding.rvChooseBackground.itemAnimator = null
        binding.rvChooseBackground.adapter = viewModel.adapterChooseBackground
        viewModel.adapterChooseBackground.callBack = { image ->
            viewModel.isGalleryImageSelected = false
            viewModel.selectedImage = image
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (viewModel.selectedImage == null) {
                showToast(
                    message = getString(R.string.please_select_image)
                )
                return@setOnClickListener
            } else {
                val intent = Intent()
                intent.putExtra(AppConstant.IMAGE_STRING, viewModel.selectedImage)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

}