package com.in2bliss.ui.activity.home.meditationTrackerMeditate.session

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.meditationTracker.MediationTrackerModel
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityStartSessionBinding
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.meditationTrackerMeditate.MeditationTrackerActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class StartSessionActivity :
    BaseActivity<ActivityStartSessionBinding>(layout = R.layout.activity_start_session) {

    @Inject
    lateinit var requestManager: RequestManager
    private val viewModel: StartSessionVM by viewModels()

    override fun init() {
        binding.data = viewModel
        observer()

        getIntentData()
        onClick()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest {
                if (it) {
                    ProgressDialog.showProgress(this@StartSessionActivity)
                } else {
                    ProgressDialog.hideProgress()
                }
            }
        }
        viewModel.time.addOnPropertyChangedCallback(object :
            androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(
                sender: androidx.databinding.Observable?,
                propertyId: Int
            ) {
                if (viewModel.time.get() == "00:00:10") {
                    val vol = (viewModel.musicPlayer?.getPlayerVolume() ?: 0f) * 100
                    val minus = vol / 15
                    fadeSoundSlowly(minus)
                }
            }
        })
        lifecycleScope.launch {
            viewModel.selfMeditationSessionResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@StartSessionActivity,
                    success = { response ->
                        showToast(response.message.toString())
                        intent(MeditationTrackerActivity::class.java, null)
                        finish()
                    }, error = { message, apiName ->
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

    private fun fadeSoundSlowly(minus: Float) {
        viewModel.apply {
            lifecycleScope.launch {
                val changeValue =
                    (((musicPlayer?.getPlayerVolume() ?: 0f) * 100) - minus).roundToInt()
                musicPlayer?.changePlayerVolume(changeValue)
                delay(1000)
                if (viewModel.time.get() != "00:00:00") {
                    fadeSoundSlowly(minus)
                }
            }
        }
    }

    /**
     * get data from intent [getIntentData]
     * */
    private fun getIntentData() {
        intent.getStringExtra(AppConstant.PLAY_MUSIC_MEDIATION)?.let { mediationData ->

            viewModel.meditation = Gson().fromJson(mediationData, MediationTrackerModel::class.java)

            binding.cvMusicTitle.visibility(
                isVisible = viewModel.meditation?.musicUrl != null
            )
            viewModel.subTitle.set(viewModel.meditation?.musicTitle)

            binding.ivAffirmationProfileBg.glide(
                requestManager = requestManager,
                image = BuildConfig.MUSIC_BASE_URL.plus(viewModel.meditation?.musicImage),
                placeholder = R.drawable.ic_error_place_holder,
                error = R.drawable.ic_error_place_holder
            )
            viewModel.setSilentMeditation(
                context = this
            )
        }
    }

    /**
     * handling on click [onClick]
     * */
    private fun onClick() {
        binding.apply {
            ivBack.setOnClickListener {
                intent(
                    destination = MeditationTrackerActivity::class.java
                )
                finish()
            }
        }
    }
}

