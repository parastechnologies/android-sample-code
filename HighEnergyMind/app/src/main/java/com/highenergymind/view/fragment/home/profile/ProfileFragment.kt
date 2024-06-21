package com.highenergymind.view.fragment.home.profile

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.base.BaseFragment
import com.highenergymind.base.BaseResponse
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.FragmentProfileBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.aboutus.AboutUsActivity
import com.highenergymind.view.activity.changepassword.ChangePasswordActivity
import com.highenergymind.view.activity.contactus.ContactUsActivity
import com.highenergymind.view.activity.disclaimer.DisclaimerActivity
import com.highenergymind.view.activity.faq.FAQsActivity
import com.highenergymind.view.activity.favorite.FavoriteActivity
import com.highenergymind.view.activity.followus.FollowUsActivity
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.activity.imprint.ImPrintActivity
import com.highenergymind.view.activity.invite.InviteActivity
import com.highenergymind.view.activity.login.LoginActivity
import com.highenergymind.view.activity.privacypolicy.PrivacyPolicyActivity
import com.highenergymind.view.activity.profile.ProfileActivity
import com.highenergymind.view.activity.redemcode.RedeemCodeActivity
import com.highenergymind.view.activity.setReminder.SetReminderActivity
import com.highenergymind.view.activity.subscription.SubscriptionPlanActivity
import com.highenergymind.view.activity.suggestaffirmation.SuggestAffirmationActivity
import com.highenergymind.view.activity.termsconditions.TermsConditionsActivity
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity
import com.highenergymind.view.sheet.audiosettings.AudioSettingsSheet
import com.highenergymind.view.sheet.deleteaccount.DeleteAccountSheet
import com.highenergymind.view.sheet.logOut.LogOutSheet
import com.highenergymind.view.sheet.managecategories.ManageCategorySheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    val viewModel by viewModels<ProfileViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private lateinit var audioBottomSheet: AudioSettingsSheet
    private lateinit var manageCategorySheet: ManageCategorySheet
    private lateinit var deleteAccountSheet: DeleteAccountSheet

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun initViewWithData() {
        setCollectors()
        clicks()
        viewModel.getProfileApi()
    }

    private fun handlePremium() {
        mBinding.apply {
            val userData = sharedPrefs.getUserData()
            if (userData?.isSubscription.isNullOrEmpty()) {
                llSubscriptionPlan.gone()
                premiumLL.visible()
                (requireActivity() as HomeActivity).handlePremium()
            } else {
                llSubscriptionPlan.visible()
                premiumLL.gone()
                (requireActivity() as HomeActivity).handlePremium()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        setDataLocally()

    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                profileResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        response.data?.let { it1 -> sharedPrefs.saveUserData(it1) }
                        setDataLocally()
                    })
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                deleteAccountResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        requireContext().showToast(response.message)
                        sharedPrefs.clearPreference()
                        requireActivity().intentComponent(LoginActivity::class.java, null)
                        requireActivity().finishAffinity()
                    })
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                logOutResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BaseResponse
                        requireContext().showToast(response.message)
                        sharedPrefs.clearPreference()
                        requireActivity().intentComponent(LoginActivity::class.java, null)
                        requireActivity().finishAffinity()
                    })
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
        }
    }

    private fun setDataLocally() {
        val data = sharedPrefs.getUserData()
        mBinding.apply {
            handlePremium()
            ivUserImage.glideImage(data?.userImg)
            tvUserName.text = data?.firstName + " ${data?.lastName ?: ""}"
            tvUserEmail.text = data?.email
            if (data?.socialType == AppConstant.SOCIAL.GOOGLE.value) {
                changePasswordll.gone()
                vDividerPassword.gone()

            } else {
                changePasswordll.visible()
                vDividerPassword.visible()
            }
        }
    }

    private fun clicks() {
        mBinding.apply {
            llProfile.setOnClickListener {
                requireContext().intentComponent(ProfileActivity::class.java, null)
            }
            llSubscriptionPlan.setOnClickListener {
                requireContext().intentComponent(SubscriptionPlanActivity::class.java, null)
            }
            llFavorite.setOnClickListener {
                requireContext().intentComponent(FavoriteActivity::class.java, null)
            }
            llSubscriptionPlan.setOnClickListener {
                requireContext().intentComponent(SubscriptionPlanActivity::class.java, null)
            }
            premiumLL.setOnClickListener {
                requireContext().intentComponent(UnlockFeatureActivity::class.java, null)
            }
            inviteFriendLL.setOnClickListener {
                requireContext().intentComponent(InviteActivity::class.java, null)
            }
            redeemCodeLL.setOnClickListener {
                requireContext().intentComponent(RedeemCodeActivity::class.java, null)
            }
            reminderLL.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(AppConstant.SCREEN_FROM, R.id.profile)
                requireContext().intentComponent(SetReminderActivity::class.java, bundle)

            }

            manageCategoriesll.setOnClickListener {

                manageCategorySheet = ManageCategorySheet()

                requireActivity().supportFragmentManager.let {
                    manageCategorySheet.show(it, "")
                }

            }


            audioSettingll.setOnClickListener {
                audioBottomSheet = AudioSettingsSheet()

                requireActivity().supportFragmentManager.let {
                    audioBottomSheet.show(it, "")
                }
            }

            faqll.setOnClickListener {
                requireContext().intentComponent(FAQsActivity::class.java, null)
            }
            aboutUsll.setOnClickListener {
                requireContext().intentComponent(AboutUsActivity::class.java, null)
            }
            supportll.setOnClickListener {
                requireActivity().intentComponent(ContactUsActivity::class.java, null)
            }
            suggestAffirll.setOnClickListener {
                requireActivity().intentComponent(SuggestAffirmationActivity::class.java, null)
            }
            followUsll.setOnClickListener {
                requireActivity().intentComponent(FollowUsActivity::class.java, null)
            }
            privacyPolicyll.setOnClickListener {
                requireActivity().intentComponent(PrivacyPolicyActivity::class.java, null)
            }
            changePasswordll.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("", "")
                requireActivity().intentComponent(ChangePasswordActivity::class.java, null)
            }

            deleteAccountll.setOnClickListener {

                deleteAccountSheet = DeleteAccountSheet()
                requireActivity().supportFragmentManager.let {
                    deleteAccountSheet.callBack = { delete ->
                        if (delete) {
                            viewModel.deleteAccountApi()
                        }
                    }
                    deleteAccountSheet.show(it, "")
                }
            }

            termsCondionll.setOnClickListener {
                requireActivity().intentComponent(TermsConditionsActivity::class.java, null)
            }

            imPrintll.setOnClickListener {
                requireActivity().intentComponent(ImPrintActivity::class.java, null)
            }
            logOutll.setOnClickListener {
                LogOutSheet().also {
                    it.callBack = { yes ->

                        if (yes) {
                            viewModel.logOutApi()

                        }
                    }
                    it.show(childFragmentManager, "logOut")
                }
            }
            disclamerll.setOnClickListener {
                requireActivity().intentComponent(DisclaimerActivity::class.java, null)
            }
        }
    }

}