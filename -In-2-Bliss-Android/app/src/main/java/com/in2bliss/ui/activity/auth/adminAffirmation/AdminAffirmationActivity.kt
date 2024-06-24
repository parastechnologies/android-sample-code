package com.in2bliss.ui.activity.auth.adminAffirmation

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityAdminAffirmationBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.affirmationList.AffirmationListViewModel
import com.in2bliss.ui.activity.home.notification.NotificationVM
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.notification.NotificationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AdminAffirmationActivity :
    BaseActivity<ActivityAdminAffirmationBinding>(R.layout.activity_admin_affirmation) {

    private val viewModel: AffirmationListViewModel by viewModels()
    private val viewModelNotification: NotificationVM by viewModels()

    var bundle = NotificationModel()
    private var bundleFavourites: MusicDetails? = null


    @Inject
    lateinit var requestManager: RequestManager


    override fun init() {
        showToast("ADMIN SCREEN ")
        getIntentData()
        observer()
        onClick()
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun getIntentData() {
        try {
            Gson().fromJson(
                intent.getStringExtra(AppConstant.NOTIFICATION_TYPE),
                NotificationModel::class.java
            ).let {
                bundle = it
                Log.d("dssdvjsdvdsvd", "getIntentData: $bundle")
            }
        } catch (e: Exception) {
            println(e)
        }

        try {
            if (intent?.hasExtra(AppConstant.MUSIC_DETAILS) == true) {
                Gson().fromJson(
                    intent.getStringExtra(AppConstant.MUSIC_DETAILS).toString(),
                    MusicDetails::class.java
                ).let { data ->

                    bundleFavourites = data
                    setData()
                }
            } else {
            }
        } catch (e: Exception) {
            Log.i("andkajsdfsdjfhsjd", "getIntentData: $e")
            println(e)
        }

        if (bundle.dataId?.isEmpty()?.not() == true) {
            viewModel.notificationAdminId = bundle.dataId?:"0"
            viewModel.retryApiRequest(
                apiName = ApiConstant.ADMIN_AFFIRMATION
            )
        }
        if (bundle.id.isNullOrEmpty().not()) {
            viewModelNotification.notificationId = bundle.id?:"0"
            viewModel.retryApiRequest(
                apiName = ApiConstant.NOTIFICATION_READ
            )
        }
    }

    private fun setData() {
        binding.tvAffirmation.text = bundleFavourites?.musicDescription
//
//        if (bundleFavourites?.category.isNullOrEmpty().not()) {
//            binding.tvCategory.text = bundleFavourites?.category?.get(0)?.cName
//            bundleFavourites?.category?.get(0)?.cIcon.let { image ->
//                binding.ivCategory.glide(
//                    requestManager = requestManager,
//                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
//                    error = R.color.prime_purple_5F46F4,
//                    placeholder = R.color.prime_purple_5F46F4
//                )
//            }
//        }

        bundleFavourites?.musicBackground?.let { image ->
            binding.ivAffirmationBg.glide(
                requestManager = requestManager,
                image = BuildConfig.AFFIRMATION_BASE_URL.plus(image),
                error = R.color.black,
                placeholder = R.color.black
            )
        }


    }

    fun observer() {
        lifecycleScope.launch {
            viewModel.adminAffirmation.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AdminAffirmationActivity,
                    success = { response ->

                        binding.tvAffirmation.text = response.data?.description
                        binding.tvCategory.text = response.data?.cName


                        response.data?.thumbnail?.let { image ->
                            binding.ivAffirmationBg.glide(
                                requestManager = requestManager,
                                image = BuildConfig.AFFIRMATION_BASE_URL.plus(image),
                                error = R.color.black,
                                placeholder = R.color.black
                            )
                        }

                        response.data?.cIcon.let { image ->
                            binding.ivCategory.glide(
                                requestManager = requestManager,
                                image = BuildConfig.IMAGE_BASE_URL.plus(image),
                                error = R.color.prime_purple_5F46F4,
                                placeholder = R.color.prime_purple_5F46F4
                            )
                        }
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}