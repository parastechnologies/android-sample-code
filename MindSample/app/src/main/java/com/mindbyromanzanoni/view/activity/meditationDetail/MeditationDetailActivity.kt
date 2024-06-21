package com.mindbyromanzanoni.view.activity.meditationDetail

import androidx.core.os.bundleOf
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeListResponse
import com.mindbyromanzanoni.databinding.ActivityMeditationDetailBinding
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.constant.AppConstants.MEDITATION_SCREEN
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.view.activity.nowPlaying.NowPlayingActivity

class MeditationDetailActivity : BaseActivity<ActivityMeditationDetailBinding>() {
    private var meditationDetails : MeditationTypeListResponse? = null
    private var getMeditationData = ""

    override fun getLayoutRes(): Int = R.layout.activity_meditation_detail

    override fun initView() {
        setToolbar()
        getIntentData()
        setOnClickListener()
    }

    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null){
            getMeditationData = intent.getString(AppConstants.MEDIATION_DETAILS).toString()
            meditationDetails = Gson().fromJson(getMeditationData, MeditationTypeListResponse::class.java)
            setUiData()
        }
    }

    private fun setUiData() {
        binding.apply {
            tvTitle.text = meditationDetails?.title
            tvDuration.text = meditationDetails?.duration
            tvDec.text = meditationDetails?.content
            appCompatImageView10.setImageFromUrl(R.drawable.placeholder_mind,meditationDetails?.videoThumbImage)
        }
    }

    override fun viewModel() {

    }

    private fun setOnClickListener() {
        binding.apply {
            btnPlay.setOnClickListener {
                val bundle = bundleOf(AppConstants.SCREEN_TYPE to MEDITATION_SCREEN,AppConstants.MEDIATION_DETAILS to getMeditationData)
                launchActivity<NowPlayingActivity>(0,bundle) {  }
            }
        }
    }


    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.meditation_details)
            ivBack.setOnClickListener{
                finishActivity()
            }
        }
    }
}