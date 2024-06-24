package com.in2bliss.ui.activity.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityHomeBinding
import com.in2bliss.ui.activity.MyApp
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.TextAffirmationListActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationList.practiceAffirmation.PracticeAllAffirmationActivity
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.home.affirmationExplore.AffirmationExploreActivity
import com.in2bliss.ui.activity.home.myAffirmation.MyAffirmationActivity
import com.in2bliss.ui.activity.home.player.PlayerActivity
import com.in2bliss.ui.activity.home.quote.QuotesActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.constants.AppConstant.NOTIFICATION_TYPE
import com.in2bliss.utils.extension.categoryType
import com.in2bliss.utils.extension.checkForNotificationPermission
import com.in2bliss.utils.extension.getDataForPlayer
import com.in2bliss.utils.extension.getRealCategoryType
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.notification.NotificationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val viewModel: HomeViewModel by viewModels()

    var bundle = NotificationModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        shareLinkNavigation()
        bottomNavigation()

        /**
        Checking for notification permission
         **/
        checkForNotificationPermission(context = this,
            isPermissionGranted = {},
            isPermissionNotGranted = { permission ->
                permissionRequest.launch(permission)
            })
        observer()
    }


    private fun observer() {
        lifecycleScope.launch {
            /**
             * collect getShare url api response
             * */
            viewModel.sharedData.collectLatest {
                handleResponse(
                    response = it,
                    context = this@HomeActivity,
                    success = { response ->
                        val data = response.data
                        /**
                         * this check handle if the type [0= AppConstant.HomeCategory.TEXT_AFFIRMATION]
                         * createdBy=0 ->   this TEXT_AFFIRMATION created by Admin
                         * in this case we need to redirect user to [TextAffirmationListActivity::class.java] screen
                         * */
                        if (data?.type == 0 && data.createdBy == 0) {
                            val bundle = Bundle()
                            bundle.putString(AppConstant.AFFIRMATION, data.id.toString())
                            intent(TextAffirmationListActivity::class.java, bundle)
                        } else {
                            val musicListData = MusicList.Data.Data(
                                audio = response.data?.audio,
                                affirmation = data?.affirmation,
                                introAffirmation = data?.introAffirmation,
                                customise = data?.customise,
                                title = data?.title,
                                description = data?.description,
                                id = data?.id,
                                views = data?.views,
                                favouriteStatus = data?.favouriteStatus,
                                thumbnail = data?.thumbnail,
                                duration = data?.duration,
                                audioName = data?.audioName,
                                background = data?.background
                            )

                            val category = if (data?.type == 3) {
                                AppConstant.HomeCategory.GUIDED_SLEEP
                            } else viewModel.getCategoryType()

                            val typeForSleep = if (viewModel.type.isNullOrBlank()) {
                                1
                            } else viewModel.decodeBase64(viewModel.type ?: "").toInt()

                            val musicDetail = getDataForPlayer(
                                category = category,
                                data = musicListData,
                                type = typeForSleep
                            )

                            val categoryType = categoryType(
                                categoryName = category,
                                type = typeForSleep
                            )

                            val bundle = bundleOf(
                                AppConstant.MUSIC_DETAILS to Gson().toJson(musicDetail),
                                AppConstant.CATEGORY_NAME to categoryType?.name,
                                AppConstant.REAL_CATEGORY to getRealCategoryType(
                                    categoryName = category,
                                    type = typeForSleep
                                )?.name

                            )
                            val destination = when (categoryType) {
                                AppConstant.HomeCategory.MUSIC -> PlayerActivity::class.java
                                AppConstant.HomeCategory.TEXT_AFFIRMATION -> {
                                    if (data?.createdBy == 1) {
                                        bundle.putString(
                                            AppConstant.JOURNAL_DATA, Gson().toJson(
                                                JournalDetail(
                                                    date = data.createdAt ?: "",
                                                    description = data.description ?: "",
                                                    backgroundImage = data.background ?: "",
                                                    id = data.id.toString()
                                                )
                                            )
                                        )
                                        PracticeAllAffirmationActivity::class.java
                                    } else {
                                        bundle.putString(
                                            AppConstant.AFFIRMATION,
                                            data?.id.toString()
                                        )
                                        TextAffirmationListActivity::class.java
                                    }
                                }

                                else -> AffirmationDetailActivity::class.java
                            }
                            intent(
                                destination = destination,
                                bundle = bundle
                            )
                        }
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

    private fun shareLinkNavigation() {
        val id = intent.getStringExtra(AppConstant.ID)
        val type = intent.getStringExtra(AppConstant.TYPE)
        if (id != null && type != null) {
            MyApp.getInstance()?.isDeepLinkData = true
            viewModel.id = id
            viewModel.type = type
            viewModel.retryApiRequest(
                apiName = ApiConstant.SHARE_DATA
            )
        }
    }

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    /** Setting the bottom navigation view */
    private fun bottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(navigation, navHostFragment.navController)
        if (intent.hasExtra(NOTIFICATION_TYPE)) {
            Gson().fromJson(
                intent.getStringExtra(NOTIFICATION_TYPE),
                NotificationModel::class.java
            ).let {
                bundle = it
                when (bundle.type) {
                    "3" -> {
                        val intent = Intent(this, QuotesActivity::class.java)
                        intent.putExtra(NOTIFICATION_TYPE, Gson().toJson(bundle))
                        startActivity(intent)
                    }

                    "4" -> {
                        Log.i("taggggggg", "bottomNavigation: ${bundle.type}")
//                        val intent = Intent(this, AdminAffirmationActivity::class.java)
//                        intent.putExtra(NOTIFICATION_TYPE, Gson().toJson(bundle))
//                        startActivity(intent)
                    }

                    "0" -> {
                        if (bundle.dataId != null) {
                            intent(TextAffirmationListActivity::class.java, Bundle().apply {
                                putString(AppConstant.AFFIRMATION, bundle.dataId)
                            })
                        } else {

                        }
                    }

                    else -> {}
                }
            }
        }


        /**
         * Navigating for the update created affirmation
         **/


        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { type ->
            val seeALlType = intent.getIntExtra(AppConstant.TYPE, -1)
            val seeAllTitle = intent.getStringExtra(AppConstant.SCREEN_TITLE)
            val bundle = Bundle()
            val destination = when (type) {
                AppConstant.CreatedAffirmationEdit.MY_AFFIRMATION.name -> {
                    navigation.selectedItemId = R.id.profile
                    bundle.putString(
                        AppConstant.CREATE_AFFIRMATION,
                        AppConstant.CreatedAffirmationEdit.MY_AFFIRMATION.name
                    )
                    MyAffirmationActivity::class.java
                }

                AppConstant.CreatedAffirmationEdit.SEE_ALL_AFFIRMATION.name -> {
                    bundle.putString(
                        AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.CREATE_AFFIRMATION.name
                    )
                    bundle.putString(

                        AppConstant.SCREEN_TITLE, seeAllTitle
                    )
                    bundle.putInt(
                        AppConstant.TYPE, seeALlType
                    )
                    AffirmationExploreActivity::class.java
                }

                else -> {
                    bundle.putString(
                        AppConstant.CATEGORY_NAME,
                        AppConstant.HomeCategory.CREATE_AFFIRMATION.name
                    )
                    AffirmationExploreActivity::class.java
                }
            }
            intent(
                destination = destination,
                bundle = bundle
            )
        }
    }

    override fun onStop() {
        super.onStop()
        val data = sharedPreference.userData
        data?.data?.isFirstTime = 0
        sharedPreference.userData = data
    }
}

