package com.highenergymind.view.activity.trackDetail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.base.BaseActivity
import com.highenergymind.base.BaseResponse
import com.highenergymind.data.AffirmationData
import com.highenergymind.data.GetTrackAffirmationResponse
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.ActivityAffirmationDetailBinding
import com.highenergymind.playerServices.BackPlayer
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.shareUrl
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.trackplay.PlayTrackActivity
import com.highenergymind.view.adapter.TrackAffirmationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TrackDetailActivity : BaseActivity<ActivityAffirmationDetailBinding>() {
    val viewModel by viewModels<TrackDetailViewModel>()
    val adapter by lazy {
        TrackAffirmationAdapter()
    }

    var trackDetail: TrackOb? = null
    override fun getLayoutRes(): Int {
        return R.layout.activity_affirmation_detail
    }

    override fun initView() {
        fullScreenStatusBar()
        setCollectors()
        getBundleData()
        clicks()
    }

    private fun setCollectors() {

        viewModel.apply {
            lifecycleScope.launch {
                markFavResponse.collectLatest {
                    handleResponse(it, {})
                }
            }
            lifecycleScope.launch {
                addRecentResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                    })
                }
            }
            lifecycleScope.launch {
                affirmationListResponse.collectLatest {
                    handleResponse(it, { resp ->
                        val response = resp as GetTrackAffirmationResponse
                        if (trackDetail == null) {
                            trackDetail = response.trackDetails
                            setDataOnScreen(trackDetail)
                        }
                        trackDetail?.affirmationList = response.data
                        setAdapter(response.data)
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

    private fun getBundleData() {
        if (intent.hasExtra(ApiEndPoint.GET_AFFIRMATION_BY_TRACK_ID)) {
            trackDetail = Gson().fromJson(
                intent.getStringExtra(ApiEndPoint.GET_AFFIRMATION_BY_TRACK_ID),
                TrackOb::class.java
            )
            setDataOnScreen(trackDetail)
            viewModel.apply {
                map.clear()
                map[ApiConstant.TRACK_ID] = trackDetail?.id.toString()
                getTrackAffirmations()
            }
        }
        if (intent.hasExtra(ApiConstant.TRACK_ID)) {
            viewModel.apply {
                map.clear()
                map[ApiConstant.TRACK_ID] = intent.getStringExtra(ApiConstant.TRACK_ID).toString()
                getTrackAffirmations()
            }
        }
    }

    private fun setDataOnScreen(trackDetail: TrackOb?) {
        binding.apply {
            BackPlayer.initializeBackground(
                this@TrackDetailActivity,
                trackDetail?.backgroundTrackMusic ?: ""
            )

            ivTrackImage.glideImage(trackDetail?.trackThumbnail)
            tvTrackCategory.text = trackDetail?.categoryName
            trackDetail?.totalTrackDuration?.let {
                tvTrackDuration.text = String.format("%02d", it / 60)
                    .plus(" ${root.context.getString(R.string.mins)}")
            }
            tvTrackTitle.text = trackDetail?.trackTitle
            tvTrackDesc.text = trackDetail?.trackDesc
            if (trackDetail?.isFav == 1) {
                ivFav.setImageResource(R.drawable.ic_fill_heart_bg)
            } else {
                ivFav.setImageResource(R.drawable.ic_un_fill_heart_bg)
            }


        }
    }

    private fun clicks() {
        binding.apply {
            ivShare.setOnClickListener {
                val link = AppConstant.SHARE_AFFIRMATION_DEEP_LINK.plus(trackDetail?.id)
                shareUrl(link)
            }
            ivBack.setOnClickListener {
                finish()
            }
            affirplayBtn.setOnClickListener {
                if (trackDetail?.affirmationList.isNullOrEmpty().not()) {
                    intentComponent(PlayTrackActivity::class.java, Bundle().also {
                        it.putString(AppConstant.TRACK_DATA, Gson().toJson(trackDetail))
                    })
                } else {
                    showToast(getString(R.string.no_affirmation_to_play))
                }
            }
            ivFav.setOnClickListener {
                if (trackDetail == null) return@setOnClickListener
                trackDetail?.isFav = if (trackDetail?.isFav == 1) 0 else 1
                if (trackDetail?.isFav == 1) {
                    ivFav.setImageResource(R.drawable.ic_fill_heart_bg)
                } else {
                    ivFav.setImageResource(R.drawable.ic_un_fill_heart_bg)
                }
                markFavApi(trackDetail!!)
            }
        }
    }


    private fun setAdapter(data: List<AffirmationData>) {
        binding.apply {
            rvTrack.adapter = adapter
            adapter.submitList(data)
        }
    }

    private fun markFavApi(item: TrackOb) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.ID] = item.id
            map[ApiConstant.FAVOURITE] = item.isFav ?: 0
            map[ApiConstant.TYPE] = AppConstant.TYPE_TRACK
            markFav()
        }
    }

}