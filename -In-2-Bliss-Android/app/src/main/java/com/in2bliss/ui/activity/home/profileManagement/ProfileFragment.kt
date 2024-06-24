package com.in2bliss.ui.activity.home.profileManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.FragmentMainProfileBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.auth.stepTwo.StepTwoActivity
import com.in2bliss.ui.activity.deleteAccount
import com.in2bliss.ui.activity.home.contactUs.ContactUsActivity
import com.in2bliss.ui.activity.home.myAffirmation.MyAffirmationActivity
import com.in2bliss.ui.activity.home.profileDetail.ProfileDetailActivity
import com.in2bliss.ui.activity.home.profileDetail.ProfileDetailViewModel
import com.in2bliss.ui.activity.home.profileManagement.download.DownloadActivity
import com.in2bliss.ui.activity.home.profileManagement.manageNotification.ManageNotificationActivity
import com.in2bliss.ui.activity.home.profileManagement.manageSubscription.ManageSubscriptionActivity
import com.in2bliss.ui.activity.home.profileManagement.termAndPrivacy.TermAndPrivacyActivity
import com.in2bliss.ui.activity.profileDetails
import com.in2bliss.ui.activity.splash.splash_step.SplashStep
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.intentComponentShareTourCode
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentMainProfileBinding>(
    layoutInflater = FragmentMainProfileBinding::inflate
) {

    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var requestManager: RequestManager

    private val viewModel: MainProfileVM by viewModels()

    private val viewModelProfileDetail: ProfileDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel
        onClick()
        if (sharedPreference.userData?.data?.isFirstTime == 0) {
            viewModelProfileDetail.retryApiRequest(ApiConstant.PROFILE_DETAIL)
        } else {
            getDataFromSharedPreference()
        }
        initRecycler()
        observer()
    }

    private fun getDataFromSharedPreference() {
        binding.tvName.text = sharedPreference.userData?.data?.fullName.toString()
        binding.ivProfile.glide(
            requestManager = requestManager,
            image = BuildConfig.PROFILE_BASE_URL.plus(sharedPreference.userData?.data?.profilePicture),
            placeholder = R.drawable.ic_user_placholder,
            error = R.drawable.ic_user_placholder
        )
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.logoutResponse.collectLatest {
                handleResponse(response = it, context = requireActivity(), success = { _ ->
                    sharedPreference.userData = null
                    requireActivity().intent(
                        destination = SplashStep::class.java
                    )
                    requireActivity().finishAffinity()
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

        lifecycleScope.launch {
            viewModel.accountDeleteResponse.collectLatest {
                handleResponse(response = it, context = requireActivity(), success = { response ->
                    sharedPreference.userData = null
                    response.message?.let { it1 -> requireActivity().showToast(it1) }
                    requireActivity().intent(StepTwoActivity::class.java)
                    requireActivity().finishAffinity()

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

        lifecycleScope.launch {
            viewModelProfileDetail.profileDetailResponse.collectLatest {
                handleResponse(response = it, context = requireActivity(), success = { response ->
                    binding.tvName.text = response.data?.fullName
                    binding.ivProfile.glide(
                        requestManager = requestManager,
                        image = BuildConfig.PROFILE_BASE_URL.plus(response.data?.profilePicture),
                        placeholder = R.drawable.ic_user_placholder,
                        error = R.drawable.ic_user_placholder
                    )
                    val data = sharedPreference.userData
                    data?.data?.profilePicture = response.data?.profilePicture
                    data?.data?.fullName = response.data?.fullName
                    data?.data?.isFirstTime = 1
                    sharedPreference.userData = data
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

    private fun initRecycler() {
        binding.rvNotification.adapter = viewModel.mainProfileAdapter
        viewModel.mainProfileAdapter.submitList(viewModel.mainProfileList)
        viewModel.mainProfileAdapter.listener = { position ->
            when (position) {
//                0 -> {
//                    requireActivity().intent(FavouritesActivity::class.java)
//                }
                0 -> {
                    // Favourites
                    requireActivity().intent(DownloadActivity::class.java)
                }

                1 -> {
                    // Downloads
                    requireActivity().intent(MyAffirmationActivity::class.java)
                }

                2 -> {
                    // My affirmations
                    profileDetails(
                        activity = requireActivity(),
                        name = sharedPreference.userData?.data?.fullName.toString(),
                        email = sharedPreference.userData?.data?.email.toString(),
                        image = sharedPreference.userData?.data?.profilePicture.toString(),
                        requestManager = requestManager
                    )
                }

                3 -> {
                    requireActivity().intent(ManageNotificationActivity::class.java)
                }

                4 -> {
                    requireActivity().intent(ManageSubscriptionActivity::class.java)
                }

                5 -> {
                    requireActivity().intent(ContactUsActivity::class.java)
                }

                6 -> {
                    val bundle = Bundle()
                    bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.ABOUT_US)
                    requireActivity().intent(TermAndPrivacyActivity::class.java, bundle)
                }

                7 -> {
                    getAppReview()
                }

                8 -> {
                    requireActivity().intentComponentShareTourCode()
                }

                9 -> {
                    val bundle = Bundle()
                    bundle.putString(AppConstant.TERMS_CONDITION, AppConstant.PRIVACY_POLICY)
                    requireActivity().intent(TermAndPrivacyActivity::class.java, bundle)
                }

                10 -> {
                    val bundle = Bundle()
                    bundle.putString(AppConstant.TERMS_CONDITION, AppConstant.TERMS_CONDITION)
                    requireActivity().intent(TermAndPrivacyActivity::class.java, bundle)
                }

                11 -> {
                    deleteAccount(activity = requireActivity(), delete = {
                        viewModel.retryApiRequest(ApiConstant.ACCOUNT_DELETE)
                    })
                }
            }
        }
    }

    private fun getAppReview() {
        val manager = FakeReviewManager(requireContext())
//        val manager = ReviewManagerFactory.create(requireContext())

        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {

                }
            } else {
                @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
            }
        }

    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.ivLogout.setOnClickListener {
            viewModel.retryApiRequest(
                apiName = ApiConstant.LOGOUT
            )
        }
        binding.ivEdit.setOnClickListener {
            val intent = Intent(requireContext(), ProfileDetailActivity::class.java)
            callFormProfileDetail.launch(intent)
        }
    }

    private val callFormProfileDetail =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.tvName.text = sharedPreference.userData?.data?.fullName
                binding.ivProfile.glide(
                    requestManager = requestManager,
                    image = BuildConfig.PROFILE_BASE_URL.plus(sharedPreference.userData?.data?.profilePicture),
                    placeholder = R.drawable.ic_user_placholder,
                    error = R.drawable.ic_user_placholder
                )
            }
        }
}

