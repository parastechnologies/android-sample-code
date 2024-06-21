package com.mindbyromanzanoni.view.activity.landscapeVideoPlay

import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityLandscapeVideoPlayBinding
import com.mindbyromanzanoni.databinding.ActivityShowImageBinding
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.finishActivityVideoZoom
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.videoOrAudioControls.MediaCache
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class ImageViewActivity : BaseActivity<ActivityShowImageBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_show_image
    }
    override fun initView() {
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        supportActionBar?.hide()
        getIntentData()
     }
    override fun viewModel(){}
    private fun getIntentData() {
        val intent = intent.extras?.getString(AppConstants.VIDEO_URL)
        binding.imageView.setImageFromUrl(intent, binding.progress)
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishActivity()
        }
    }
}