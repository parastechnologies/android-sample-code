package com.in2bliss.ui.activity.home.affirmation

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.TextAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityTextAffirmationListBinding
import com.in2bliss.ui.activity.MyApp
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListViewModel
import com.in2bliss.ui.activity.home.profileManagement.favourites.FavouritesVM
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.constants.AppConstant.IS_AFFIRMATION_CATEGORIES_FILTER
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TextAffirmationListActivity : BaseActivity<ActivityTextAffirmationListBinding>(
    layout = R.layout.activity_text_affirmation_list
) {

    @Inject
    lateinit var requestManager: RequestManager

    private var isHome = true

    var musicId: String? = null
    var favStatus: Int? = null


    var textAffirmation: TextAffirmation? = null
    private val viewModel: AffirmationListViewModel by viewModels()

    private val viewModelFavourites: FavouritesVM by viewModels()

    override fun init() {
        getIntentData()
        observer()
        onClickView()
        settingRecyclerView()
        observeShareResponse()
    }


    private fun observeShareResponse() {
        lifecycleScope.launch {
            viewModel.shareUrl.collectLatest {
                handleResponse(
                    response = it,
                    context = this@TextAffirmationListActivity,
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


    }

    private fun getIntentData() {
        if (intent.extras?.containsKey(AppConstant.AFFIRMATION) == true) {
            isHome = false
            viewModel.id = intent.extras?.getString(AppConstant.AFFIRMATION) ?: ""
        } else {
            setHomeScreenData()
        }
    }

    private fun setHomeScreenData() {
        /** Getting the category */
        viewModel.id = MyApp.getInstance()?.textAddAffirmation?.id.toString()
        binding.tvCategory.text = MyApp.getInstance()?.textAddAffirmation?.categoryName
            ?: getString(R.string.go_to_my_affirmation)
        binding.tvAffirmation.text = MyApp.getInstance()?.textAddAffirmation?.description
        MyApp.getInstance()?.textAddAffirmation?.categoryIcon.let { image ->
            binding.ivCategory.glide(
                requestManager = requestManager,
                image = BuildConfig.IMAGE_BASE_URL.plus(image),
                error = R.drawable.ic_user_heart,
                placeholder = R.drawable.ic_user_heart
            )
        }
        binding.tvDate.text = MyApp.getInstance()?.textAddAffirmation?.createdAt?.let {
            formatDate(
                it,
                "yyyy-MM-dd HH:mm:ss",
                "dd MMMM"
            )
        }

        if (MyApp.getInstance()?.textAddAffirmation?.favouriteStatus == 1) {
            binding.ivAffirmationFav.setImageResource(R.drawable.ic_red_fav)
        } else {
            binding.ivAffirmationFav.setImageResource(R.drawable.ic_affirmation_fav)
        }
        val backgroundImage =
            BuildConfig.AFFIRMATION_BASE_URL.plus(MyApp.getInstance()?.textAddAffirmation?.background)
        binding.ivAffirmationBg.glide(
            requestManager = requestManager,
            image = backgroundImage,
            error = R.color.black,
            placeholder = R.color.black
        )
        binding.ivAffirmationFav.setOnClickListener {
            viewModelFavourites.favouriteType = "0"
            viewModelFavourites.id = MyApp.getInstance()?.textAddAffirmation?.id
            viewModelFavourites.isFav = MyApp.getInstance()?.textAddAffirmation?.favouriteStatus
            viewModelFavourites.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
            if (MyApp.getInstance()?.textAddAffirmation?.favouriteStatus == 1) {
                MyApp.getInstance()?.textAddAffirmation?.favouriteStatus = 0
                binding.ivAffirmationFav.setImageResource(R.drawable.ic_like)
            } else {
                MyApp.getInstance()?.textAddAffirmation?.favouriteStatus = 1
                binding.ivAffirmationFav.setImageResource(R.drawable.ic_player_fav_like)
            }
        }


        binding.ivAddAffirmationAdd.setOnClickListener {
            intent(
                destination = AffirmationListActivity::class.java
            )
        }

    }

    private fun observer() {
        when (viewModel.categoryType) {
            AppConstant.HomeCategory.JOURNAL -> {}
            else -> {
                viewModel.adapterText.addLoadStateListener {
                    when (it.refresh) {
                        is LoadState.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                        }

                        is LoadState.Loading -> {
                            binding.swipeRefresh.isRefreshing = true
                        }

                        is LoadState.NotLoading -> {
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }
                getAffirmationData()
            }
        }
    }


    private fun getAffirmationData() {
        lifecycleScope.launch {
            viewModel.getAdminAffirmationList {
                MyApp.getInstance()?.setUpdatedData?.invoke(null, false)
                binding.constCL.visible()
                binding.clNoAffirmation.visible()
                binding.swipeRefresh.gone()
            }.collectLatest { textAffirmationList ->
                lifecycleScope.launch {
                    viewModel.adapterText.submitData(textAffirmationList)
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        if (viewModel.adapterText.snapshot().isNotEmpty()) {
                            binding.constCL.gone()
                            binding.clNoAffirmation.gone()
                            binding.swipeGP.visible()
                        } else {
                            MyApp.getInstance()?.setUpdatedData?.invoke(null, false)
                            binding.constCL.visible()
                            binding.clNoAffirmation.visible()
                            binding.swipeRefresh.gone()
                        }
                    }
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.swipeRefresh.visible()
                getAffirmationData()
            }
        }


    private fun settingRecyclerView() {
        binding.rvRecyclerView.adapter = viewModel.adapterText
        viewModel.adapterText.listener = { it ->
            if (isHome) {
                setUiScreenData(it)
                MyApp.getInstance()?.setUpdatedData?.invoke(it, true)
                binding.constCL.visible()
                onBackPressedDispatcher.onBackPressed()
            } else {
                finish()
            }
        }
        viewModel.adapterText.onClickMiniMise = { it ->
            if (isHome) {
                setUiScreenData(it)
                MyApp.getInstance()?.setUpdatedData?.invoke(it, true)
                binding.constCL.visible()
                onBackPressedDispatcher.onBackPressed()
            } else {
                finish()
            }
        }
        viewModel.adapterText.onClickTextAffirmationList = {
            val intent = Intent(this, AffirmationCategoriesActivity::class.java)
            intent.putExtra(IS_AFFIRMATION_CATEGORIES_FILTER, true)
            resultLauncher.launch(intent)
        }
        viewModel.adapterText.onClickAdd = {
            intent(
                destination = AffirmationListActivity::class.java
            )
        }
        viewModel.adapterText.onClickShareAffirmation = { musicDetails ->
            viewModel.shareId = musicDetails.id.toString()
            viewModel.shareType = when (viewModel.categoryType?.name) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.toString() -> ApiConstant.ExploreType.AFFIRMATION.value
                AppConstant.HomeCategory.GUIDED_MEDITATION.toString() -> ApiConstant.ExploreType.MEDITATION.value
                AppConstant.HomeCategory.MUSIC.toString() -> ApiConstant.ExploreType.MUSIC.value
                else -> "3"
            }
            viewModel.retryApiRequest(ApiConstant.SHARE_URL)
        }

        viewModel.adapterText.onClickFavAffirmation = { id, favStatus ->
            viewModelFavourites.favouriteType = "0"
            viewModelFavourites.id = id
            viewModelFavourites.isFav = favStatus
            viewModelFavourites.retryApiRequest(ApiConstant.FAVOURITE_AFFIRMATION)
        }

    }

    private fun onClickView() {
        binding.ivAffirmationGallery.setOnClickListener {
            intent(
                destination = AffirmationListActivity::class.java
            )
        }
        binding.clChange.setOnClickListener {
            val intent = Intent(this, AffirmationCategoriesActivity::class.java)
            intent.putExtra(IS_AFFIRMATION_CATEGORIES_FILTER, true)
            resultLauncher.launch(intent)
        }
        binding.rvRecyclerView.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                textAffirmation = viewModel.adapterText.snapshot().items[position]
                musicId = viewModel.adapterText.snapshot().items[position].id.toString()
                favStatus = viewModel.adapterText.snapshot().items[position].favouriteStatus
                if (viewModel.adapterText.snapshot().items[position].favouriteStatus == 1) {
                    binding.ivAffirmationRedFav1.setImageResource(R.drawable.ic_red_fav)
                } else {
                    binding.ivAffirmationRedFav1.setImageResource(R.drawable.ic_affirmation_fav)
                }
            }
        })

        binding.ivBackTwo.setOnClickListener {
            finish()
        }
        binding.ivBack1.setOnClickListener {
            finish()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivAffirmationRedFav1.setOnClickListener {
            viewModelFavourites.favouriteType = "0"
            viewModelFavourites.id = musicId?.toInt()
            viewModelFavourites.isFav = favStatus
            viewModelFavourites.retryApiRequest(ApiConstant.FAVOURITE_AFFIRMATION)
            if (favStatus == 1) {
                favStatus = 0
                binding.ivAffirmationRedFav1.setImageResource(R.drawable.ic_affirmation_fav)
            } else {
                favStatus = 1
                binding.ivAffirmationRedFav1.setImageResource(R.drawable.ic_red_fav)
            }
        }
        binding.ivMenu1.setOnClickListener {
            val intent = Intent(this, AffirmationCategoriesActivity::class.java)
            intent.putExtra(IS_AFFIRMATION_CATEGORIES_FILTER, true)
            resultLauncher.launch(intent)
        }

        binding.ivAffirmationShrink1.setOnClickListener {
            if (isHome) {
                textAffirmation?.let { it1 -> setUiScreenData(it1) }
                MyApp.getInstance()?.setUpdatedData?.invoke(textAffirmation, true)
                binding.constCL.visible()
                onBackPressedDispatcher.onBackPressed()
            } else {
                finish()
            }
        }

        binding.ivAffirmationShare1.setOnClickListener {
            viewModel.shareId = musicId.toString()
            viewModel.shareType = when (viewModel.categoryType?.name) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.toString() -> ApiConstant.ExploreType.AFFIRMATION.value
                AppConstant.HomeCategory.GUIDED_MEDITATION.toString() -> ApiConstant.ExploreType.MEDITATION.value
                AppConstant.HomeCategory.MUSIC.toString() -> ApiConstant.ExploreType.MUSIC.value
                else -> "3"
            }
            viewModel.retryApiRequest(ApiConstant.SHARE_URL)
        }

        binding.ivMountain.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.addMoreIV1.setOnClickListener {
            intent(
                destination = AffirmationListActivity::class.java
            )
        }
    }

    private fun setUiScreenData(textAffirmation: TextAffirmation) {
        /** Getting the category */
        binding.tvCategory.text = textAffirmation.categoryName
        binding.tvAffirmation.text = textAffirmation.description
        textAffirmation.categoryIcon.let { image ->
            binding.ivCategory.glide(
                requestManager = requestManager,
                image = BuildConfig.IMAGE_BASE_URL.plus(image),
                error = R.color.prime_purple_5F46F4,
                placeholder = R.color.prime_purple_5F46F4
            )
        }
        val backgroundImage =
            BuildConfig.AFFIRMATION_BASE_URL.plus(textAffirmation.background)
        binding.ivAffirmationGradient.glide(
            requestManager = requestManager,
            image = backgroundImage,
            error = R.color.black,
            placeholder = R.color.black
        )
    }
}