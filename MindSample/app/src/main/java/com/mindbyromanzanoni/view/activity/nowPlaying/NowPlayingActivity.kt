package com.mindbyromanzanoni.view.activity.nowPlaying

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeListResponse
import com.mindbyromanzanoni.data.response.resource.ResourceTypeList
import com.mindbyromanzanoni.databinding.ActivityNowPlayingBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NowPlayingActivity : BaseActivity<ActivityNowPlayingBinding>() {
    override fun getLayoutRes(): Int = R.layout.activity_now_playing
    private val viewModal: HomeViewModel by viewModels()
    override fun initView() {
        getIntentData()
        setToolbar()
        observeDataFromViewModal()
        setOnClickListener()
        setSpinnerSpeed()
    }
    private fun setSpinnerSpeed() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.speed_video, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.tvSpeed.adapter = adapter
        binding.tvSpeed.setSelection(3)
    }
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            when (val screenType = intent.getString(AppConstants.SCREEN_TYPE).toString()) {
                AppConstants.MEDITATION_SCREEN -> {
                    val getMeditationData = intent.getString(AppConstants.MEDIATION_DETAILS).toString()
                    viewModal.meditationDetails = Gson().fromJson(getMeditationData, MeditationTypeListResponse::class.java)

                    viewModal.initializingPlayers(context = this,viewModal.meditationDetails?.title ?: "", viewModal.meditationDetails?.videoName ?: "", viewModal.meditationDetails?.videoThumbImage ?: "")
                    setUiData(screenType)
                }
                AppConstants.RESOURCE_SCREEN -> {
                    val getResourceData = intent.getString(AppConstants.RESOURCE_DETAILS).toString()
                    viewModal.resourceDetails = Gson().fromJson(getResourceData, ResourceTypeList::class.java)
                    viewModal.initializingPlayers(context = this, viewModal.resourceDetails?.title ?: "", viewModal.resourceDetails?.audioName ?: "", viewModal.resourceDetails?.imageName ?: "")
                    setUiData(screenType)
                }
                AppConstants.HOME_SCREEN -> {
                    val eventId = intent.getInt(AppConstants.EVENT_ID)
                    binding.scrollView.gone()
                    viewModal.initializingPlayers(context = this, viewModal.resourceDetails?.title ?: "", viewModal.resourceDetails?.audioName ?: "", viewModal.resourceDetails?.imageName ?: "")
                    RunInScope.ioThread {
                        delay(1000)
                        viewModal.responseOnTheBasesOfCategory(eventId = eventId.toString(), "2")
                    }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setUiData(screenType: String) {
        binding.apply {
            if (screenType == AppConstants.MEDITATION_SCREEN) {
                viewModal.meditationDetails?.apply {
                    appCompatTextView4.text = title
                    appCompatTextView4.isSelected = true
                    tvEndTime.text = duration
                    tvDec.text = getString(R.string.meditation_tracker)
                    cvImg.setImageFromUrl(R.drawable.placeholder_mind, videoThumbImage)
                }
            } else if (screenType == AppConstants.RESOURCE_SCREEN) {
                viewModal.resourceDetails?.apply {
                    appCompatTextView4.text = title
                    appCompatTextView4.isSelected = true
                    tvEndTime.text = duration
                    tvDec.text = getString(R.string.meditation_tracker)
                    cvImg.setImageFromUrl(R.drawable.placeholder_mind, imageName)
                }
            } else {
                viewModal.meditationDetails?.apply {
                    appCompatTextView4.text = title
                    appCompatTextView4.isSelected = true
                    tvEndTime.text = duration
                    tvDec.text = getString(R.string.meditation_tracker)
                    cvImg.setImageFromUrl(R.drawable.placeholder_mind, videoThumbImage)
                    scrollView.visible()
                }
            }
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            appCompatImageView9.setOnClickListener {
                /** Play button working in customization */
                viewModal.mediaController?.playOrPauseMusic()
            }
            ivDecrease10sec.setOnClickListener {
                /** Play button working in customization */
                val progress = binding.rangeSlider.progress
                viewModel?.mediaController?.changeMusicProgress(
                    progress = progress.toLong() - 10000L
                )
            }
            ivIncrease10sec.setOnClickListener {
                /** Play button working in customization */
                val progress = binding.rangeSlider.progress
                viewModel?.mediaController?.changeMusicProgress(
                    progress = progress.toLong() + 10000L
                )
            }
            rangeSlider.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean) {
                    if (fromUser){
                        viewModal.mediaController?.seekChange(position = progress.toLong())
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            tvSpeed.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    /** Speed fast */
                    when (position) {
                        0 -> {
                            viewModal.mediaController?.fastSpeed(0.25F)
                        }

                        1 -> {
                            viewModal.mediaController?.fastSpeed(0.5F)
                        }

                        2 -> {
                            viewModal.mediaController?.fastSpeed(0.75F)
                        }

                        3 -> {
                            viewModal.mediaController?.fastSpeed(1f)
                        }

                        4 -> {
                            viewModal.mediaController?.fastSpeed(1.25F)
                        }

                        5 -> {
                            viewModal.mediaController?.fastSpeed(1.5F)
                        }

                        6 -> {
                            viewModal.mediaController?.fastSpeed(2f)
                        }
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    binding.tvSpeed.setSelection(3)
                }
            }
        }
    }



    override fun viewModel() {
        binding.viewModel = viewModal
    }


    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.now_playing)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            RunInScope.mainThread {
                viewModal.mediaController?.musicPLayingState()?.collectLatest { isPlaying ->
                    binding.appCompatImageView9.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            if (isPlaying) R.drawable.ic_pause_icon else R.drawable.ic_play_icon
                        )
                    )
                }
            }
        }
        /** Buffering */
        lifecycleScope.launch {
            RunInScope.mainThread {
                viewModal.mediaController?.musicBuffering()?.collectLatest { isBuffering ->
                    binding.apply {
                        if (isBuffering) {
                            buffering.visible()
                        } else {
                            buffering.gone()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModal.allTypeResponse.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val responseData = isResponse.data
                        if (responseData?.isSuccess() == true) {
                            val model = MeditationTypeListResponse(
                                title = responseData.data?.title ?: "",
                                videoName = responseData.data?.videoName ?: "",
                                videoThumbImage = responseData.data?.videoThumbImage ?: ""
                            )
                            viewModal.meditationDetails = model
                            setUiData(AppConstants.HOME_SCREEN)
                            synchronized(this@NowPlayingActivity) {
                                viewModal.initializingPlayers(
                                    context = this@NowPlayingActivity,
                                    viewModal.meditationDetails?.title ?: "",
                                    viewModal.meditationDetails?.videoName ?: "",
                                    viewModal.meditationDetails?.videoThumbImage ?: ""
                                )
                                viewModal.mediaController?.playOrPauseMusic()
                            }
                        } else {
                            finishActivity()
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(this@NowPlayingActivity, msg)
                        }
                    }
                }
            }
        }


        /** Seek bar progress */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModal.mediaController?.musicProgress()?.collectLatest { mediaProgress ->

                viewModal.musicStart.set(mediaProgress.musicProgress)
                binding.rangeSlider.progress = mediaProgress.seekBarProgress.toInt()
            }
        }

        /** Music end time */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModal.mediaController?.musicEndTime()?.collectLatest { mediaProgress ->
                viewModal.musicEnd.set(mediaProgress.musicProgress)
                binding.rangeSlider.max = mediaProgress.seekBarProgress.toInt()
            }
        }
        viewModal.showLoading.observe(this) {
            if (it) {
                MyProgressBar.showProgress(this)
            } else {
                MyProgressBar.hideProgress()
            }
        }
    }

    override fun finishAffinity() {
        super.finishAffinity()
        viewModal.mediaController?.releaseMediaController()
    }

    override fun finish() {
        super.finish()
        viewModal.mediaController?.releaseMediaController()
    }
}
