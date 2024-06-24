package com.in2bliss.ui.activity.auth.stepTwo

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.LogInResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityStepTwoBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.auth.appStatus.AppStatusActivity
import com.in2bliss.ui.activity.auth.stepFive.StepFiveActivity
import com.in2bliss.ui.activity.auth.stepFour.StepFourActivity
import com.in2bliss.ui.activity.auth.stepThree.StepThreeActivity
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.checkEmail
import com.in2bliss.utils.extension.getFcmToken
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.isValidPassword
import com.in2bliss.utils.extension.passwordVisibility
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepTwoActivity : BaseActivity<ActivityStepTwoBinding>(
    layout = R.layout.activity_step_two
) {

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val viewModel: StepTwoViewModel by viewModels()

    private lateinit var firebaseAuth: FirebaseAuth

    override fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        binding.data = viewModel
        bundle()
        onclick()
        setSpannable()
        observer()
        getFcmToken { token ->
            viewModel.deviceToken = token
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.signupSignInResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@StepTwoActivity,
                    success = { response ->
                        sharedPreference.userData = response

                        /** Initializing welcome screen model **/
                        val welcomeScreen = LogInResponse.Data.WelcomeScreen()
                        val userData = sharedPreference.userData
                        userData?.data?.welcomeScreen = welcomeScreen
                        sharedPreference.userData = userData

                        if (viewModel.isLogin) {
                            val destination =
                                when (sharedPreference.userData?.data?.profileStatus) {
                                    1 -> StepThreeActivity::class.java
                                    2 -> StepFourActivity::class.java
                                    3 -> StepFiveActivity::class.java
                                    4 -> AppStatusActivity::class.java
                                    else -> HomeActivity::class.java
                                }
                            intent(
                                destination = destination
                            )
                            if (destination == HomeActivity::class.java) finishAffinity()
                            return@handleResponse
                        }

                        userData?.data?.profileStatus = 1
                        sharedPreference.userData = userData

                        intent(
                            destination = StepThreeActivity::class.java
                        )
                        finishAffinity()
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

    private fun bundle() {

        intent.getBooleanExtra(AppConstant.IS_LOGIN, false).let { isLogin ->
            viewModel.isLogin = isLogin
            binding.login.visibility(viewModel.isLogin.not())
            if (viewModel.isLogin) binding.btnContinue.setText(R.string.login)
        }
        intent.getStringExtra(AppConstant.REASON_ID).let { id ->
            viewModel.reasonId = id
        }
        intent.getBooleanExtra(AppConstant.UNAUTHORISED, false).let { unAuthorised ->
            viewModel.unAuthorised = unAuthorised
            if (viewModel.unAuthorised) sharedPreference.userData = null
        }


        binding.tvLoginToStayOn.setText(
            if (viewModel.isLogin) {
                R.string.login_now_to_stay_on_track_and_unlock_exclusive_features
            } else R.string.sign_up_now_to_stay_on_track_and_unlock_exclusive_features
        )
    }

    private fun setSpannable() {
        val spannable =
            SpannableStringBuilder(getString(R.string.by_signing_up_you_agree_to_our_terms_of_use_and_privacy_policy))
        val termClick = object : ClickableSpan() {
            override fun onClick(p0: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val privacyPolicy = object : ClickableSpan() {
            override fun onClick(p0: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannable.setSpan(termClick, 32, 44, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(privacyPolicy, 49, 63, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTermsAndCondition.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTermsAndCondition.text = spannable
    }

    private val signInActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            viewModel.googleSignIn?.googleSignInResult(
                activityResult = activityResult,
                firebaseAuth = firebaseAuth,
                result = { result ->
                    viewModel.socialId = result?.user?.uid
                    viewModel.email.set(result?.user?.email)
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.SOCIAL_LOGIN
                    )
                }
            )
        }

    private fun onclick() {
        binding.clGoogle.setOnClickListener {
            if (viewModel.googleSignIn == null) viewModel.initializeGoogleSignIn()
            viewModel.googleSignIn?.signOut(
                firebaseAuth = firebaseAuth,
                context = this
            )
            val googleSignInClient = viewModel.googleSignIn?.googleSignIn(
                context = this
            )
            signInActivityResult.launch(googleSignInClient?.signInIntent)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvLogin.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.IS_LOGIN to true
            )
            intent(
                destination = StepTwoActivity::class.java,
                bundle = bundle
            )
        }

        binding.etEmail.doAfterTextChanged { text ->
            binding.ivCancelEmail.visibility((text?.length ?: "".length) > 0)
        }

        binding.ivCancelEmail.setOnClickListener {
            viewModel.email.set("")
        }

        binding.ivPasswordVisibility.setOnClickListener {
            binding.etPassword.passwordVisibility(
                isPasswordVisible = viewModel.passwordVisible,
                response = { drawable ->
                    binding.ivPasswordVisibility.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            drawable
                        )
                    )
                }
            )
            viewModel.passwordVisible = viewModel.passwordVisible.not()
        }

        binding.ivConfirmPasswordVisibility.setOnClickListener {
            binding.etConfirmPassword.passwordVisibility(
                isPasswordVisible = viewModel.confirmPasswordVisible,
                response = { drawable ->
                    binding.ivConfirmPasswordVisibility.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            drawable
                        )
                    )
                }
            )
            viewModel.confirmPasswordVisible = viewModel.confirmPasswordVisible.not()
        }

        binding.btnContinue.setOnClickListener {
            if (isValidate()) {
                if (viewModel.isLogin) {
                    viewModel.retryApiRequest(
                        apiName = ApiConstant.LOGIN
                    )
                    return@setOnClickListener
                }
                viewModel.retryApiRequest(
                    apiName = ApiConstant.SIGNUP
                )
            }
        }
    }

    /**
     * Validating input data
     * */
    private fun isValidate(): Boolean {
        return when {
            (viewModel.email.get()?.trim().isNullOrEmpty()) -> {
                binding.etEmail.requestFocus()
                binding.etEmail.setSelection(0)
                showToast(getString(R.string.please_enter_your_email_address))
                false
            }

            (!checkEmail(viewModel.email.get() ?: "")) -> {
                binding.etEmail.requestFocus()
                binding.etEmail.setSelection(0)
                showToast(message = getString(R.string.please_enter_valid_email_address))
                return false
            }

            (viewModel.password.get()?.trim().isNullOrEmpty()) -> {
                binding.etPassword.requestFocus()
                binding.etPassword.setSelection(0)
                showToast(
                    message = getString(R.string.please_enter_your_password)
                )
                false
            }

            ((viewModel.password.get()?.trim()?.length ?: 0) < 6) -> {
                showToast(
                    message = getString(R.string.password_must_be_greater_than_6)
                )
                false
            }

            (viewModel.password.get().isValidPassword().not()) -> {
                showToast(
                    message = getString(R.string.password_validation_with_uppercase_lowercase_special_character_number_and_limit_8_must_be_required_)
                )
                false
            }

            (viewModel.confirmPassword.get()?.trim().isNullOrEmpty() && !viewModel.isLogin) -> {
                binding.etConfirmPassword.requestFocus()
                binding.etConfirmPassword.setSelection(0)
                showToast(
                    message = getString(R.string.please_enter_your_confirm_password)
                )
                false
            }

            ((viewModel.password.get() != viewModel.confirmPassword.get()) && !viewModel.isLogin) -> {
                showToast(
                    message = getString(R.string.password_and_confirm_password_did_not_match)
                )
                false
            }

            else -> true
        }
    }
}
