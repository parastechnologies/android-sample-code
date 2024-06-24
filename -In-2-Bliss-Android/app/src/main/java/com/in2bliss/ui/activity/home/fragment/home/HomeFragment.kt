package com.in2bliss.ui.activity.home.fragment.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.FragmentHomeBinding
import com.in2bliss.ui.activity.MyApp
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.ui.activity.home.affirmation.TextAffirmationListActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListActivity
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.home.affirmationExplore.AffirmationExploreActivity
import com.in2bliss.ui.activity.home.journal.journalStreak.JournalStreakActivity
import com.in2bliss.ui.activity.home.meditationTrackerMeditate.MeditationTrackerActivity
import com.in2bliss.ui.activity.home.music.MusicActivity
import com.in2bliss.ui.activity.home.notification.NotificationActivity
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.ui.activity.home.profileManagement.favourites.FavouritesVM
import com.in2bliss.ui.activity.home.profileManagement.manageSubscription.ManageSubscriptionVM
import com.in2bliss.ui.activity.home.quote.QuotesActivity
import com.in2bliss.ui.activity.home.reminder.ReminderActivity
import com.in2bliss.ui.activity.home.sleep.SleepTimerActivity
import com.in2bliss.ui.activity.home.welcome.WelcomeActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.getRealCategoryType
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.inVisible
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    layoutInflater = FragmentHomeBinding::inflate
) {

    @Inject
    lateinit var sharedPreferences: SharedPreference
    private val viewModel: HomeViewModel by viewModels()
    private val subscriptionViewModel: ManageSubscriptionVM by viewModels()
    private val viewModelFavourites: FavouritesVM by viewModels()
    var id: String? = null

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel
        getIntentData()
        setProfile()
        settingRecyclerView()
        onClick()
        observer()
        viewModel.retryApiRequest(ApiConstant.Home)
        checkForSubscription()
        observeShareResponse()
        handleUpdateItem()

    }



    private fun setProfile() {
        binding.ivProfile.glide(
            requestManager = requestManager,
            image = BuildConfig.PROFILE_BASE_URL.plus(sharedPreference.userData?.data?.profilePicture),
            placeholder = R.drawable.ic_user_placholder,
            error = R.drawable.ic_user_placholder
        )
    }


    private fun getIntentData() {
        val na = (requireActivity() as HomeActivity).bundle.id
        val type = (requireActivity() as HomeActivity).bundle.type
        if (na.isNullOrEmpty().not()&& type=="4" ) {
            viewModelFavourites.id = MyApp.getInstance()?.textAddAffirmation?.id
            viewModel.shareId = MyApp.getInstance()?.textAddAffirmation?.id.toString()
            val intent = Intent(requireContext(), TextAffirmationListActivity::class.java)
            val options = ViewCompat.getTransitionName(binding.cvBackground)?.let { it1 ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), binding.cvBackground, it1
                )
            }
            startActivity(intent, options?.toBundle())
        }
    }


    private fun observer() {
        lifecycleScope.launch {
            subscriptionViewModel.subscriptionStatus.collectLatest {
                handleResponse(response = it,
                    context = requireActivity(),
                    showToast = false,
                    success = {
                        val userData = sharedPreferences.userData
                        userData?.data?.isSubscriptionApiHit = true
                        userData?.data?.purchaseToken = null
                        userData?.data?.planType = null
                        sharedPreferences.userData = userData
                    },
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            subscriptionViewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }
        lifecycleScope.launch {
            viewModel.homeData.collectLatest {
                handleResponse(response = it,
                    context = requireActivity(),
                    showToast = false,
                    success = { response ->
                        if (response.textAffirmation != null) {
                            showAffirmation()
                            MyApp.getInstance()?.textAddAffirmation = response.textAffirmation
                            setHomeScreenData()
                        } else {
                            noAffirmation()
                        }
                        viewModel.musicListAdapter.submitList(response.data)
                    },
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }
    }


    private fun noAffirmation() {
        viewModel.affirmation.set("No Affirmation Found")
        binding.ivExpand.gone()
        binding.ivGallery.gone()
        binding.ivDownload.gone()
        binding.ivLike.gone()
        binding.cvCategory.inVisible()
        binding.cvCategoryChange.visible()
        binding.ivBackgroundImage.setImageResource(R.drawable.ic_background_temp)
    }

    private fun showAffirmation() {
        binding.ivExpand.visible()
        binding.ivGallery.visible()
        binding.ivDownload.visible()
        binding.ivLike.visible()
        binding.cvCategory.visible()
        binding.cvCategoryChange.gone()
    }

    private fun observeShareResponse() {
        lifecycleScope.launch {
            viewModel.shareUrl.collectLatest {
                handleResponse(response = it, context = requireActivity(), success = { response ->
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.setType("text/plain")
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name))
                    var shareMessage =
                        getString(R.string.this_is_an_awesome_app_it_will_help_you_to_feel_amazing_and_change_your_world_you_can_try_it_for_free_for_7_days_here_is_the_link)
                    shareMessage += "${response.url}"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                }, error = { message, apiName ->
                    requireActivity().alertDialogBox(
                        message = message
                    ) {
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    }
                })
            }
        }
    }

    private fun checkForSubscription() {
        val userData = sharedPreferences.userData?.data
        if (userData?.isSubscriptionApiHit == false && userData.planType.isNullOrEmpty()
                .not() && userData.purchaseToken.isNullOrEmpty().not()
        ) {
            subscriptionViewModel.planType = userData.planType
            subscriptionViewModel.transactionID = userData.purchaseToken
            subscriptionViewModel.retryApiRequest(
                apiName = ApiConstant.SUBSCRIBE
            )
        }
    }

    private fun onClick() {
        binding.ivExpand.setOnClickListener {
//            TextAffirmationListActivity second
//            val intent = Intent(requireContext(), AffirmationActivity::class.java)
            val intent = Intent(requireContext(), TextAffirmationListActivity::class.java)
            val options = ViewCompat.getTransitionName(binding.cvBackground)?.let { it1 ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), binding.cvBackground, it1
                )
            }
            startActivity(intent, options?.toBundle())
        }
        binding.cvCategoryChange.setOnClickListener {
            val intent = Intent(requireActivity(), AffirmationCategoriesActivity::class.java)
            intent.putExtra(AppConstant.IS_AFFIRMATION_CATEGORIES_FILTER, true)
            resultLauncher.launch(intent)
        }
        binding.ivNotification.setOnClickListener {
            requireActivity().intent(NotificationActivity::class.java)
        }
        binding.ivAdd.setOnClickListener {
            requireActivity().intent(
                destination = AffirmationListActivity::class.java
            )
        }
    }

    private fun settingRecyclerView() {
        lifecycleScope.launch {
            awaitAll(async {
                /** Setting get started adapter */
                val layoutManger = GridLayoutManager(requireContext(), 3)
                binding.rvGettingStarted.layoutManager = layoutManger
                (binding.rvGettingStarted.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false
                binding.rvGettingStarted.adapter = viewModel.gettingStartedAdapter
                viewModel.gettingStartedAdapter.submitList(viewModel.gettingStartedList)
                viewModel.gettingStartedAdapter.listener = { position ->
                    val category = when (position) {
                        0 -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                        1 -> AppConstant.HomeCategory.GUIDED_SLEEP
                        2 -> AppConstant.HomeCategory.GUIDED_MEDITATION
                        3 -> AppConstant.HomeCategory.QUOTES
                        4 -> AppConstant.HomeCategory.MUSIC
                        5 -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                        6 -> AppConstant.HomeCategory.WISDOM_INSPIRATION
                        7 -> AppConstant.HomeCategory.MEDITATION_TRACKER
                        else -> AppConstant.HomeCategory.JOURNAL
                    }
                    when (category) {
                        AppConstant.HomeCategory.GUIDED_SLEEP -> {
                            val bundle = bundleOf(AppConstant.CATEGORY_NAME to category.name,
                                AppConstant.IS_GUIDED to true)
                            val destination =
                                if (sharedPreferences.userData?.data?.sleepStatus == 1) {
                                    AffirmationExploreActivity::class.java
                                } else SleepTimerActivity::class.java

                            requireActivity().intent(
                                destination = destination, bundle = bundle
                            )
                        }

                        AppConstant.HomeCategory.MUSIC -> {
                            val bundle = bundleOf(AppConstant.CATEGORY_NAME to category.name)
                            requireActivity().intent(
                                destination = MusicActivity::class.java, bundle = bundle
                            )
                        }

                        AppConstant.HomeCategory.MEDITATION_TRACKER -> {

                            val bundle = bundleOf(AppConstant.CATEGORY_NAME to category.name)
                            requireActivity().intent(
                                destination = MeditationTrackerActivity::class.java, bundle = bundle
                            )
                        }

                        AppConstant.HomeCategory.QUOTES -> {
                            val destination =
                                if (sharedPreferences.userData?.data?.quoteStatus == 1 || sharedPreferences.userData?.data?.quotesReminderSkip == 1) {
                                    QuotesActivity::class.java
                                } else ReminderActivity::class.java

                            val bundle = bundleOf(AppConstant.CATEGORY_NAME to category.name)
                            requireActivity().intent(
                                destination = destination, bundle = bundle
                            )
                        }

                        AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
                            val bundle = bundleOf(AppConstant.CATEGORY_NAME to category.name)
                            requireActivity().intent(
                                destination = AffirmationCategoriesActivity::class.java,
                                bundle = bundle
                            )
                        }

                        else -> {
                            val userData = sharedPreferences.userData?.data?.welcomeScreen
                            val bundle = bundleOf(AppConstant.CATEGORY_NAME to category.name)

                            val destination = when (category.name) {
                                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> {
                                    if (userData?.guidedAffirmation == true) {
                                        AffirmationCategoriesActivity::class.java
                                    } else WelcomeActivity::class.java
                                }

                                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> {
                                    if (userData?.guidedMeditation == true) {
                                        AffirmationCategoriesActivity::class.java
                                    } else WelcomeActivity::class.java
                                }

                                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> {
                                    if (userData?.createAffirmation == true) {
                                        AffirmationExploreActivity::class.java
                                    } else
                                        WelcomeActivity::class.java
                                }

                                AppConstant.HomeCategory.JOURNAL.name -> {
                                    if (userData?.gratitudeJournal == true) {
                                        if (sharedPreferences.userData?.data?.journalReminderSkip == 1 || sharedPreferences.userData?.data?.journalStatus == 1) {
                                            JournalStreakActivity::class.java
                                        } else ReminderActivity::class.java
                                    } else WelcomeActivity::class.java
                                }

                                else -> {
                                    WelcomeActivity::class.java
                                }
                            }
                            requireActivity().intent(
                                destination = destination, bundle = bundle
                            )
                        }
                    }
                }

            })
        }
        binding.rvMusicList.adapter = viewModel.musicListAdapter
        viewModel.musicListAdapter.listener = { type, dataType, data ->
            viewModel.type = type
            val category = if (dataType == "3") {
                AppConstant.HomeCategory.GUIDED_SLEEP
            } else viewModel.getCategoryType()

            val typeForSleep = if (viewModel.type.isNullOrBlank()) {
                1
            } else {
                (viewModel.type ?: "0").toInt()
            }


            val musicDetail = getDataForPlayer(
                category = category, data = data, type = typeForSleep
            )

            val categoryType = categoryType(
                categoryName = category, type = typeForSleep
            )

            val destination = when (categoryType) {
                AppConstant.HomeCategory.MUSIC -> PlayerActivity::class.java
                else -> AffirmationDetailActivity::class.java
            }

            val bundle = bundleOf(
                AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
                AppConstant.CATEGORY_NAME to categoryType?.name,
                AppConstant.REAL_CATEGORY to  getRealCategoryType(
                    categoryName = category, type = typeForSleep
                )?.name
            )

            requireActivity().intent(
                destination = destination, bundle = bundle
            )
        }
        viewModel.musicListAdapter.favourite = { id, isFav, type, dataType ->
            /**
             * type is changing for is affirmation,mediation,music and wisdom
             * */
            when (type) {
                "0" -> {
                    viewModelFavourites.categoryType = ApiConstant.ExploreType.AFFIRMATION
                }

                "1" -> {
                    viewModelFavourites.categoryType = ApiConstant.ExploreType.MEDITATION
                }

                "2" -> {
                    viewModelFavourites.categoryType = ApiConstant.ExploreType.MUSIC
                }

                else -> {
                    viewModelFavourites.categoryType = ApiConstant.ExploreType.MUSIC
                }
            }

            /**
             * data type  is changing for is affirmation[text,my,guided,sleep],mediation [normal,sleep],music [normal,sleep],wisdom
             * */
            viewModelFavourites.favouriteType = dataType
            viewModelFavourites.id = id
            viewModelFavourites.isFav = isFav.toInt()
            viewModelFavourites.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
        }
    }

    private fun handleUpdateItem() {
        MyApp.getInstance()?.setUpdatedData = { textAddAffirmation, isData ->
            if (isData) {
                showAffirmation()
                MyApp.getInstance()?.textAddAffirmation = textAddAffirmation
                setHomeScreenData()
            } else {
                noAffirmation()
            }
        }
    }


    private fun setHomeScreenData() {
        /** Getting the category */
        viewModel.categoryName.set(MyApp.getInstance()?.textAddAffirmation?.categoryName)
        viewModel.date.set(MyApp.getInstance()?.textAddAffirmation?.createdAt?.let {
            formatDate(
                it, "yyyy-MM-dd HH:mm:ss", "dd MMMM"
            )
        })
        viewModel.affirmation.set(MyApp.getInstance()?.textAddAffirmation?.description)
        MyApp.getInstance()?.textAddAffirmation?.categoryIcon.let { image ->
            binding.ivCategory.glide(
                requestManager = requestManager,
                image = BuildConfig.IMAGE_BASE_URL.plus(image),
                error = R.color.prime_purple_5F46F4,
                placeholder = R.color.prime_purple_5F46F4
            )
        }
        val backgroundImage =
            BuildConfig.AFFIRMATION_BASE_URL.plus(MyApp.getInstance()?.textAddAffirmation?.background)
        binding.ivBackgroundImage.glide(
            requestManager = requestManager,
            image = backgroundImage,
            error = R.color.black,
            placeholder = R.color.black
        )

        if (MyApp.getInstance()?.textAddAffirmation?.favouriteStatus == 1) {
            binding.ivLike.setImageResource(R.drawable.ic_player_fav_like)
        } else {
            binding.ivLike.setImageResource(R.drawable.ic_like)
        }

        binding.ivLike.setOnClickListener {
            viewModelFavourites.favouriteType = "0"
            viewModelFavourites.id = MyApp.getInstance()?.textAddAffirmation?.id
            viewModelFavourites.isFav = MyApp.getInstance()?.textAddAffirmation?.favouriteStatus
            viewModelFavourites.retryApiRequest(
                apiName = ApiConstant.FAVOURITE_AFFIRMATION
            )
            if (MyApp.getInstance()?.textAddAffirmation?.favouriteStatus == 1) {
                MyApp.getInstance()?.textAddAffirmation?.favouriteStatus = 0
                binding.ivLike.setImageResource(R.drawable.ic_like)
            } else {
                MyApp.getInstance()?.textAddAffirmation?.favouriteStatus = 1
                binding.ivLike.setImageResource(R.drawable.ic_player_fav_like)
            }
        }

        binding.ivDownload.setOnClickListener {
            /**
             * ShareType 0 for only text Affirmation
             * */
            viewModel.shareId = MyApp.getInstance()?.textAddAffirmation?.id.toString()
            viewModel.shareType = "0"
            viewModel.retryApiRequest(ApiConstant.SHARE_URL)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.retryApiRequest(ApiConstant.Home)
            }
        }
}
