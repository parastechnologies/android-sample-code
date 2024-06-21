package com.highenergymind.view.activity.signUpProcess

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityContinueExploreBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getAndroidID
import com.highenergymind.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpProcessActivity : BaseActivity<ActivityContinueExploreBinding>() {
    val viewModel by viewModels<SignUpProcessViewModel>()

    override fun getLayoutRes(): Int {
        return R.layout.activity_continue_explore
    }

    override fun initView() {
        fullScreenStatusBar()
        getBundleData()
        clicks()
    }

    private fun getBundleData() {

        if (intent.hasExtra(getString(R.string.email_verification))) {
            binding.apply {
                val navFrag =
                    supportFragmentManager.findFragmentById(binding.navHostFragmentContainer.id) as NavHostFragment
                val navGraph =
                    navFrag.navController.navInflater.inflate(R.navigation.signup_nav_graph)
                navGraph.setStartDestination(R.id.emailVerificationFragment)
                navFrag.navController.graph = navGraph
            }
        }
    }

    private fun clicks() {
        binding.apply {
            ivBack.setOnClickListener {
                handleBackPress()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun handleBackPress() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        when (navController.currentBackStack.value.last().destination.id) {
            R.id.continueExploreFragment -> {
                finish()
            }

            R.id.emailVerificationFragment -> {
                if (navController.graph.startDestinationId == R.id.emailVerificationFragment) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }

            else -> {
                navController.navigateUp()
            }
        }
    }

    fun setProgressMeter(pixelSize: Int) {
        binding.apply {
            val anim = ResizeWidthAnimation(
                progress,
                resources.getDimensionPixelSize(pixelSize)
            )
            anim.duration = 1000
            progress.startAnimation(anim)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            viewModel.REQ_ONE_TAP -> {
                try {
                    val credential = viewModel.oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken

                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            viewModel.showLoader(true)
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            Firebase.auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->

                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(
                                            "TAG",
                                            "signInWithCredential:success  domain->${Firebase.auth.customAuthDomain}"
                                        )
                                        val user = Firebase.auth.currentUser
//                                        viewModel.showLoader(false)
                                        socialLoginApi(user)

                                        Log.e("googleLogin", "${user}")
                                        Firebase.auth.signOut()
                                        viewModel.oneTapClient.signOut()

                                    } else {
                                        viewModel.showLoader(false)
                                        // If sign in fails, display a message to the user.
                                        showToast(task.exception?.localizedMessage)
                                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                                    }
                                }
                            Log.d("TAG", "Got ID token. $idToken")
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d("TAG", "No ID token or password!")
                        }
                    }
                    // ...
                } catch (e: ApiException) {
                    viewModel.showLoader(false)

                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d("TAG", "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                        }

                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d("TAG", "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }

                        else -> {
                            Log.d(
                                "TAG",
                                "Couldn't get credential from result." + " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun socialLoginApi(user: FirebaseUser?) {
        viewModel.apply {
            map[ApiConstant.EMAIL] = user?.email ?: ""
            map[ApiConstant.SOCIAL_ID] = user?.uid ?: ""
            map[ApiConstant.SOCIAL_TYPE] = AppConstant.SOCIAL.GOOGLE.value
            map[ApiConstant.DEV_ID] = getAndroidID() ?: ""
            map[ApiConstant.DEV_TYPE] = "android"
            map[ApiConstant.DEV_TOKEN] = fireToken
            socialSignUp()
        }
    }
}

class ResizeWidthAnimation(private val mView: View, private val mWidth: Int) : Animation() {
    private val mStartWidth: Int = mView.width

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        mView.layoutParams.width = mStartWidth + ((mWidth - mStartWidth) * interpolatedTime).toInt()
        mView.requestLayout()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

}