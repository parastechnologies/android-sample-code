package com.highenergymind.view.fragment.home.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.AffDay
import com.highenergymind.data.Affirmation
import com.highenergymind.data.HomeDashboardResponse
import com.highenergymind.data.HomeData
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.FragmentHomeBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.checkCameraPermission
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.toAffirmationModel
import com.highenergymind.utils.toDateFormat
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.ScreenShotActivity
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.activity.notification.NotificationActivity
import com.highenergymind.view.activity.profile.ProfileActivity
import com.highenergymind.view.activity.seeAllTrack.SeeAllTrackActivity
import com.highenergymind.view.adapter.HomeAffirmationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    val viewModel by viewModels<HomeFragmentViewModel>()
    private val lastAdapter by lazy { HomeAffirmationAdapter() }
    private val recommendationAdapter by lazy { HomeAffirmationAdapter() }
    private val playListMonthAdapter by lazy { HomeAffirmationAdapter() }
    private val popularAdapter by lazy { HomeAffirmationAdapter() }
    private var homeData: HomeData? = null
    override fun getLayoutRes()=R.layout.fragment_home


    override fun initViewWithData() {
        setCollectors()
        setLocalData()
        clicks()
        setUpViews()
        if (recommendationAdapter.currentList.isEmpty()) {
            viewModel.getHomeDashboardApi()
        }else{
            mBinding.apply {
            shimmerTopAff.gone()
            shimmerMonth.gone()
            shimmerPopular.gone()
            shimmerRecommendation.gone()
            shimmerLastPlayed.gone()
            }
        }
    }

    private fun setUpViews() {
        mBinding.apply {
            if (homeData?.affDay == null) cvAffirmation.gone() else cvAffirmation.visible()
            setAffirmOfDayData()

            rvLastTracks.adapter = lastAdapter.also {
                it.callBack = { item, pos, type ->
                    markFavApi(item)
                }
            }
            rvRecommendation.adapter = recommendationAdapter.also {
                it.callBack = { item, pos, type ->
                    markFavApi(item)
                }
            }
            rvPlayListMonth.adapter = playListMonthAdapter.also {
                it.callBack = { item, _, type ->
                    markFavApi(item)
                }
            }
            rvPopularTrack.adapter = popularAdapter.also {
                it.callBack = { item, pos, type ->
                    markFavApi(item)
                }
            }
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                markFavResponse.collectLatest {
                    handleResponse(it, {})
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    mBinding.swipeToRefresh.isRefreshing = it
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                homeResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as HomeDashboardResponse
//                        viewLifecycleOwner.lifecycleScope.launch {
//                            delay(5000)
                            homeData = response.data
                            setAdapter()
//                        }
                    })
                }
            }
        }
    }

    private fun setLocalData() {
        val userData = viewModel.sharedPrefs.getUserData()
        mBinding.apply {
            tvName.text = getString(R.string.welcome,userData?.firstName ?: "")
            ivImage.glideImage(userData?.userImg)
        }
    }

    private fun clicks() {


        mBinding.apply {
            ivShare.setOnClickListener {
                if (requireActivity().checkCameraPermission()) {
                    takeScreenShot(homeData?.affDay)
                } else {
                    requestPermissionCam.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            cameraPermissionHigherArray
                        } else {
                            cameraPermissionLowerArray
                        }
                    )
                }
            }
            cvBooks.setOnClickListener {
                val url = "https://lp.highenergymind.com/publishing-app".toUri()
                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(intent)
            }
            ivFavAffirmation.setOnClickListener {

                markAffirmFav()
            }
            swipeToRefresh.setOnRefreshListener {
                viewModel.getHomeDashboardApi()
            }
            cvImage.setOnClickListener {
                requireContext().intentComponent(ProfileActivity::class.java)
            }
            tvSeeAllLastTrack.setOnClickListener {
                requireContext().intentComponent(
                    SeeAllTrackActivity::class.java,
                    Bundle().also {
                        it.putString(
                            getString(R.string.see_all),
                            tvLastTrack.text.toString()
                        )
                    })
            }
            tvSeeAllRecommendation.setOnClickListener {
                requireContext().intentComponent(
                    SeeAllTrackActivity::class.java,
                    Bundle().also {
                        it.putString(
                            getString(R.string.see_all),
                            tvRecommendation.text.toString()
                        )
                    })
            }
            tvSeeAllPlaylistOfMonth.setOnClickListener {
                requireContext().intentComponent(
                    SeeAllTrackActivity::class.java,
                    Bundle().also {
                        it.putString(
                            getString(R.string.see_all),
                            tvPlayListMonth.text.toString()
                        )
                    })
            }
            tvSeeAllPopular.setOnClickListener {
                requireContext().intentComponent(
                    SeeAllTrackActivity::class.java,
                    Bundle().also {
                        it.putString(
                            getString(R.string.see_all),
                            tvPopularTrack.text.toString()
                        )
                    })
            }
            ivExpand.setOnClickListener {
                (requireActivity() as HomeActivity).also { home ->
                    home.binding.bottomNav.selectedItemId = R.id.affirmation

                }
            }

            tvAffirmationText.setOnClickListener {
                (requireActivity() as HomeActivity).also { home ->
                    home.binding.bottomNav.selectedItemId = R.id.affirmation

                }
            }
            ivNotification.setOnClickListener {
                requireContext().intentComponent(NotificationActivity::class.java)
            }
        }
    }

    private fun setAdapter() {
        homeData?.let { response ->
            mBinding.apply {
                shimmerTopAff.gone()
                shimmerMonth.gone()
                shimmerPopular.gone()
                shimmerRecommendation.gone()
                shimmerLastPlayed.gone()
                setAffirmOfDayData()

                if (response.lastTrack.isEmpty()) {
                    llLastPlayed.gone()
                } else {
                    llLastPlayed.visible()
                }

                if (response.recommendation.isEmpty()) {
                    llRecommendation.gone()
                } else {
                    llRecommendation.visible()
                }
                if (response.playlist.isEmpty()) {
                    llPlayList.gone()
                } else {
                    llPlayList.visible()
                }

                if (response.popular.isEmpty()) {
                    llPopular.gone()
                } else {
                    llPopular.visible()
                }
            }

            lastAdapter.submitList(response.lastTrack)
            recommendationAdapter.submitList(response.recommendation)
            playListMonthAdapter.submitList(response.playlist)
            popularAdapter.submitList(response.popular)
        }
    }

    private fun setAffirmOfDayData() {
        mBinding.apply {
            homeData?.affDay?.let { af ->
                cvAffirmation.visible()
                ivAffirmationImage.glideImage(homeData?.backgroundAffImg)
                tvAffirmationText.text = if (ApplicationClass.isEnglishSelected) af.affirmationTextEnglish else af.affirmationTextGerman
                tvAffDayDate.text = af.todayDate?.toDateFormat("yyyy-MM-dd HH:mm:ss", if (ApplicationClass.isEnglishSelected) "dd MMMM" else "dd. MMMM")
                mBinding.ivFavAffirmation.setImageResource(
                    if ((af.isFav
                            ?: 0) == 1
                    ) R.drawable.ic_fill_heart else R.drawable.ic_un_fill_heart
                )

            }
        }
    }

    private fun markFavApi(item: TrackOb) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.ID] = item.id
            map[ApiConstant.FAVOURITE] = item.isFav ?: false
            map[ApiConstant.TYPE] = AppConstant.TYPE_TRACK
            markFav()
        }
    }

    private fun markAffirmFav() {
        viewModel.apply {
            homeData?.let {
                it.affDay?.isFav = if (it.affDay?.isFav == 0) 1 else 0
                mBinding.ivFavAffirmation.setImageResource(
                    if ((it.affDay?.isFav
                            ?: 0) == 1
                    ) R.drawable.ic_fill_heart else R.drawable.ic_un_fill_heart
                )
                map.clear()
                map[ApiConstant.ID] = it.affDay?.id.toString()
                map[ApiConstant.FAVOURITE] = it.affDay?.isFav ?: false
                map[ApiConstant.TYPE] = AppConstant.TYPE_AFFIRMATION
                markFav()
            }
        }
    }
    private fun takeScreenShot(aff: AffDay?) {
        mBinding.apply {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)?.path.let {
                aff?.createdAt = homeData?.backgroundAffImg.toString()
                val intent = Intent(requireContext(), ScreenShotActivity::class.java)
                intent.putExtra(AppConstant.TRACK_DATA, Gson().toJson(aff?.toAffirmationModel()))
                activityResult.launch(intent)
            }
        }

    }


    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val img = it.data?.getStringExtra(ApiConstant.IMG)
                img?.let { it1 -> shareImageIntent(it1) }
            }
        }
    private fun shareImageIntent(s: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.setType("image/jpeg")
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(s))
        startActivity(Intent.createChooser(share, "Share Affirmation"))
    }

    private val cameraPermissionLowerArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val cameraPermissionHigherArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    private val requestPermissionCam =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (it[Manifest.permission.CAMERA] == true && it[Manifest.permission.READ_MEDIA_IMAGES] == true && it[Manifest.permission.READ_MEDIA_VIDEO] == true) {
                    mBinding.ivShare.performClick()
                }
            } else {
                if (it[Manifest.permission.CAMERA] == true && it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true && it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                    mBinding.ivShare.performClick()
                }
            }
        }


}