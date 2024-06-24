package com.in2bliss.ui.activity.home.affirmation.chooseBackground.affirmationCreated

import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityAffirmationCreatedBinding
import com.in2bliss.domain.DownloadFIleInInternalStorageInterface
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.greatJob.GreatJobActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getFileFromUri
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AffirmationCreatedActivity : BaseActivity<ActivityAffirmationCreatedBinding>(
    layout = R.layout.activity_affirmation_created
) {

    @Inject
    lateinit var downloadFIleInInternalStorageInterface: DownloadFIleInInternalStorageInterface

    @Inject
    lateinit var requestManager: RequestManager

    private val viewModel: AffirmationCreatedViewModel by viewModels()

    override fun init() {
        binding.ivAffirmationBg.layoutParams.height =
            ((resources.displayMetrics.heightPixels.toDouble()) / 1.6).toInt()
        binding.data = viewModel

        gettingBundleData()
        onClick()
        observer()
    }

    private fun gettingBundleData() {
        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { createAffirmation ->
            viewModel.createAffirmation =
                Gson().fromJson(createAffirmation, CreateAffirmation::class.java)
            viewModel.title.set(viewModel.createAffirmation?.affirmationTitle)
            viewModel.description.set(viewModel.createAffirmation?.affirmationDetail)

            binding.ivAffirmationBg.glide(
                requestManager = requestManager,
                image = BuildConfig.AFFIRMATION_BASE_URL.plus(viewModel.createAffirmation?.affirmationBackground.orEmpty()),
                placeholder = R.drawable.ic_error_place_holder,
                error = R.drawable.ic_error_place_holder
            )
        }
    }

    private fun observer() {

        lifecycleScope.launch {
            viewModel.createAffirmationResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationCreatedActivity,
                    success = {

                        /** Deleting converted file */
                        if (viewModel.createAffirmation?.convertedFileName != null) {
                            downloadFIleInInternalStorageInterface.deleteFile(
                                fileName = viewModel.createAffirmation?.convertedFileName ?: "",
                                context = this@AffirmationCreatedActivity,
                                isMusicDirectory = true
                            ) {

                            }
                        }

                        if (viewModel.createAffirmation?.isEdit == true) {

                            intent(
                                destination = HomeActivity::class.java,
                                bundle = bundleOf(
                                    AppConstant.CREATE_AFFIRMATION to viewModel.createAffirmation?.screenType,
                                    AppConstant.TYPE to viewModel.createAffirmation?.type,
                                    AppConstant.SCREEN_TITLE to viewModel.createAffirmation?.screenName
                                )
                            )
                            finishAffinity()
                            return@handleResponse
                        }
                        intent(
                            destination = GreatJobActivity::class.java
                        )
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

        lifecycleScope.launch {
            viewModel.uploadData.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationCreatedActivity,
                    success = { response ->
                        viewModel.audioUrl = response.fileName
                        if (viewModel.createAffirmation?.isEdit == true) {
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.AFFIRMATION_UPDATE
                            )
                            return@handleResponse
                        }
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.ADD_AFFIRMATION
                        )
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

    private fun onClick() {
        binding.btnContinue.setOnClickListener {

            if (viewModel.createAffirmation?.isEdit == true &&
                viewModel.createAffirmation?.audioFileStringUri?.contains("content://") == false
            ) {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.AFFIRMATION_UPDATE
                )
                return@setOnClickListener
            }

            viewModel.createAffirmation?.audioFileStringUri?.let { audio ->
                getFileFromUri(
                    context = this,
                    uri = audio.toUri(),
                    file = { file ->
                        lifecycleScope.launch {
                            viewModel.audioFile = file
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.UPLOAD
                            )
                        }
                    }
                )
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}