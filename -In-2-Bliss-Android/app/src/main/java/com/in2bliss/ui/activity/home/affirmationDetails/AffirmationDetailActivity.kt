package com.in2bliss.ui.activity.home.affirmationDetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityAffirmationDetailBinding
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.domain.RoomDataBaseInterface
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationDetails.customizeAffirmationBottomSheet.CustomiseAffirmationFragment
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.downloadWithWorkManager
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.gettingDownloadStatus
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.loadSvg
import com.in2bliss.utils.extension.showSnackBar
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.showToastLong
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AffirmationDetailActivity : BaseActivity<ActivityAffirmationDetailBinding>(
    layout = R.layout.activity_affirmation_detail
) {
    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var imageRequest: ImageRequest.Builder

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var downloadStatusListener: DownloadStatusListenerInterface

    @Inject
    lateinit var roomDataBaseInterface: RoomDataBaseInterface

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val viewModel: AffirmationDetailViewModel by viewModels()
    private var customiseAffirmationFragment: CustomiseAffirmationFragment? = null

    override fun init() {
        binding.data = viewModel
        binding.ivAffirmationBg.layoutParams.height =
            ((resources.displayMetrics.heightPixels.toDouble()) / 1.6).toInt()
        gettingBundle()
        onClick()
        backPressed()
        observer()
        gettingRoomDataBase()

    }

    /**
     * Checking if file already downloaded
     * */
    private fun gettingRoomDataBase() {
        lifecycleScope.launch {
            val downloadedMusicList = roomDataBaseInterface.getList()
            downloadedMusicList.forEach { downloadedMusic ->
                if (downloadedMusic.musicUrl == viewModel.musicDetails?.musicUrl &&
                    downloadedMusic.id == viewModel.musicDetails?.musicId &&
                    downloadedMusic.userId == (sharedPreference.userData?.data?.id ?: 0)
                ) {
                    viewModel.changeDownloadStatus(
                        downloadStatus = AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
                    )
                    return@launch
                }
            }
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    popUpWithFavResult()
                }
            })
    }

    private fun popUpWithFavResult() {
        val intent = Intent()
        intent.putExtra(
            AppConstant.FAVOURITE,
            viewModel.musicDetails?.musicFavouriteStatus == 1
        )
        intent.putExtra(
            AppConstant.MUSIC_CUSTOMIZE_DETAIL,
            viewModel.isCustomized
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.favouriteAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationDetailActivity,
                    success = { _ ->
                        viewModel.musicDetails?.musicFavouriteStatus =
                            if (viewModel.musicDetails?.musicFavouriteStatus == 0) 1 else 0
                        isFavourite()
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
            viewModel.shareUrl.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationDetailActivity,
                    success = { response ->
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("text/plain")
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        var shareMessage =
                            getString(R.string.this_is_an_awesome_app_it_will_help_you_to_feel_amazing_and_change_your_world_you_can_try_it_for_free_for_7_days_here_is_the_link)
                        shareMessage += "${response.url}"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))

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
            viewModel.customizeAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationDetailActivity,
                    success = { _ ->
                        viewModel.isCustomized = true
                    }, error = { message, apiName ->
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
            viewModel.downloadStatus.collectLatest { downloadStatus ->
                updatingDownloadingStatus(
                    downloadStatus = downloadStatus
                )
            }
        }

        lifecycleScope.launch {
            downloadStatusListener.getDownloadStatus().collectLatest { status ->
                lifecycleScope.launch {
                    try {

                        /** Not collecting download status if already downloaded */
                        if (viewModel.downloadStatus.value != AppConstant.DownloadStatus.DOWNLOAD_COMPLETE) {
                            val downloadStatus = gettingDownloadStatus(
                                downloadStatus = status,
                                downloadStarted = viewModel.isDownloadingStarted,
                                music = viewModel.musicDetails
                            )

                            /** Avoiding other download status if downloading in progress */
                            if (downloadStatus == AppConstant.DownloadStatus.DOWNLOADING) {
                                viewModel.isDownloadingStarted = true
                            }
                            if (downloadStatus == AppConstant.DownloadStatus.DOWNLOAD_COMPLETE) {
                                viewModel.isDownloadingStarted = false
                            }

                            viewModel.changeDownloadStatus(
                                downloadStatus = downloadStatus
                            )
                        }

                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        viewModel.changeDownloadStatus(
                            downloadStatus = AppConstant.DownloadStatus.NOT_DOWNLOAD
                        )
                    }
                }
            }
        }
    }

    private suspend fun updatingDownloadingStatus(
        downloadStatus: AppConstant.DownloadStatus
    ) {
        withContext(Dispatchers.Main) {
            binding.clDownloadingStatus.visibility(
                isVisible = downloadStatus == AppConstant.DownloadStatus.DOWNLOADING || downloadStatus ==
                        AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
            )
            binding.ivDownloadComplete.visibility(
                isVisible = downloadStatus ==
                        AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
            )
            binding.downloadProgress.visibility(
                isVisible = downloadStatus ==
                        AppConstant.DownloadStatus.DOWNLOADING
            )
        }
    }

    private fun gettingBundle() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryType = when (categoryName) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
                AppConstant.HomeCategory.MUSIC.name -> AppConstant.HomeCategory.MUSIC
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> AppConstant.HomeCategory.WISDOM_INSPIRATION
                else -> null
            }
        }

        intent.getStringExtra(AppConstant.MUSIC_DETAILS)?.let { musicDetails ->
            viewModel.musicDetails = Gson().fromJson(musicDetails, MusicDetails::class.java)
            setMusicDetails()
            Log.d("vsxvhah", "gettingBundle: ${viewModel.musicDetails}")
        }

        if (viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION) {
            binding.btnStartSession.setText(R.string.continue_)
        }

        binding.ivShare.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.CREATE_AFFIRMATION
        )
        binding.views.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.CREATE_AFFIRMATION
        )
        binding.cvCustomiseSession.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION
        )

    }

    private fun setMusicDetails() {

        viewModel.title.set(viewModel.musicDetails?.musicTitle)
        viewModel.description.set(viewModel.musicDetails?.musicDescription)
        viewModel.views.set(" ${viewModel.musicDetails?.musicViews ?: 0} views")

        val image = getImageUrl(
            category = viewModel.categoryType,
            image = viewModel.musicDetails?.musicThumbnail
        )

        if (viewModel.musicDetails?.musicThumbnail?.takeLast(3) == "svg") {
            binding.ivAffirmationBg.loadSvg(
                imageLoader = imageLoader,
                imageRequest = imageRequest,
                url = image,
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )
        } else {
            binding.ivAffirmationBg.glide(
                requestManager = requestManager,
                image = image,
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )
        }
        isFavourite()
    }

    private fun isFavourite() {
        binding.ivFavAffirmation.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (viewModel.musicDetails?.musicFavouriteStatus == 0) {
                    R.drawable.ic_red_aff_unlike
                } else R.drawable.ic_red_aff_like
            )
        )
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            popUpWithFavResult()
        }

        binding.ivShare.setOnClickListener {
            viewModel.shareId = viewModel.musicDetails?.musicId.toString()
            viewModel.shareType = when (viewModel.categoryType?.name) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.toString() -> ApiConstant.ExploreType.AFFIRMATION.value
                AppConstant.HomeCategory.GUIDED_MEDITATION.toString() -> ApiConstant.ExploreType.MEDITATION.value
                AppConstant.HomeCategory.MUSIC.toString() -> ApiConstant.ExploreType.MUSIC.value
                else -> "3"
            }
            viewModel.retryApiRequest(ApiConstant.SHARE_URL)
        }

        binding.cvCustomiseSession.setOnClickListener {

            /** Closed previous opened bottom sheet if opened multiple at once */
            if (customiseAffirmationFragment?.dialog?.isShowing == true) {
                return@setOnClickListener
            }
            customiseAffirmationFragment = CustomiseAffirmationFragment { data ->
                viewModel.musicDetails?.isCustomizationEnabled = true
                viewModel.musicDetails?.musicCustomizeDetail = data
                if (data?.isSaveForNextTime == true) {
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.CUSTOMIZE_AFFIRMATION
                    )
                }
            }.apply {
                arguments = bundleOf(
                    AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
                    AppConstant.MUSIC_CUSTOMIZE_DETAIL to Gson().toJson(viewModel.musicDetails?.musicCustomizeDetail)
                ).also {
                    if (intent.hasExtra(AppConstant.REAL_CATEGORY)) {
                        it.putString(
                            AppConstant.REAL_CATEGORY,
                            intent.getStringExtra(AppConstant.REAL_CATEGORY)
                        )
                    }
                }
                show(
                    supportFragmentManager, null
                )
            }
        }

        binding.ivFavAffirmation.setOnClickListener {
            viewModel.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }

        binding.btnStartSession.setOnClickListener {

            activityResult.launch(
                Intent(this, PlayerActivity::class.java).apply {
                    putExtras(createBundle())
                }
            )
        }

        binding.ivDownload.setOnClickListener {
            lifecycleScope.launch {
                var count = 0
                roomDataBaseInterface.getList().forEach { downloadedData ->
                    if (downloadedData.id == sharedPreference.userData?.data?.id) {
                        count += 1
                    }
                }
                if (count >= 10) {
                    showToast(
                        message = getString(R.string.download_limit_exceeded)
                    )
                    return@launch
                }



                if (viewModel.downloadStatus.value == AppConstant.DownloadStatus.NOT_DOWNLOAD) {
                    downloadWithWorkManager(
                        category = viewModel.categoryType,
                        musicDetails = viewModel.musicDetails,
                        activity = this@AffirmationDetailActivity
                    )
                    getString(R.string.original_music_will_be).showSnackBar(binding.root)

                }
            }
        }
    }

    private fun createBundle(): Bundle {
        return bundleOf(
            AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
            AppConstant.MUSIC_DETAILS to Gson().toJson(viewModel.musicDetails),

            ).also {
            if (intent.hasExtra(AppConstant.REAL_CATEGORY)) {
                it.putString(
                    AppConstant.REAL_CATEGORY,
                    intent.getStringExtra(AppConstant.REAL_CATEGORY)
                )
            }
        }


    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(AppConstant.FAVOURITE, false)?.let { isFav ->
                    viewModel.musicDetails?.musicFavouriteStatus = if (isFav) 1 else 0
                    isFavourite()
                }
            }
        }
}