package com.highenergymind.view.activity.musicdetails

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.BackAudios
import com.highenergymind.data.MusicDetailResponse
import com.highenergymind.databinding.ActivityMusicDetailsBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.shareUrl
import com.highenergymind.view.activity.playmusic.PlayMusicActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicDetailsActivity : BaseActivity<ActivityMusicDetailsBinding>() {
    private var backAudios: BackAudios? = null
    val viewModel by viewModels<MusicDetailViewModel>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_music_details
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        getIntentData()
        clicks()
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            lifecycleScope.launch {
                musicDetailResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as MusicDetailResponse
                        backAudios = response.data
                        setDataOnScreen()
                    })
                }
            }
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra(AppConstant.MUSIC_DATA)) {
            backAudios = Gson().fromJson(
                intent.getStringExtra(AppConstant.MUSIC_DATA),
                BackAudios::class.java
            )
            setDataOnScreen()
        }
        if (intent.hasExtra(ApiConstant.MUSIC_ID)) {
            viewModel.apply {
                map.clear()
                map[ApiConstant.AUDIO_ID] = intent.getStringExtra(ApiConstant.MUSIC_ID)!!
                getMusicDetailApi()
            }
        }
    }

    private fun setDataOnScreen() {
        binding.apply {
            ivBackground.glideImage(backAudios?.backTrackImg)
            tvTitle.text = backAudios?.backgroundTitle
            tvDescription.text = backAudios?.backTrackDesc
            tvDescription.movementMethod = ScrollingMovementMethod()
        }
    }


    private fun clicks() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            ivShare.setOnClickListener {
                val url = AppConstant.SHARE_MUSIC_DEEP_LINK.plus(backAudios?.id)
                shareUrl(url, getString(R.string.share_music))
            }

            playBtn.setOnClickListener {
                intentComponent(PlayMusicActivity::class.java, Bundle().also { bnd ->
                    bnd.putString(AppConstant.MUSIC_DATA, Gson().toJson(backAudios))
                })
            }
        }
    }

}