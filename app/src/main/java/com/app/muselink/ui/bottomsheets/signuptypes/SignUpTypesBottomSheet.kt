package com.app.muselink.ui.bottomsheets.signuptypes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.databinding.SignupBottomsheetBinding
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.activities.settings.privacypolicy.ActivityPrivacyPolicy
import com.app.muselink.ui.activities.settings.termofuse.ActivityTermOfUse
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.ui.bottomsheets.SuccessBottomSheet
import com.app.muselink.ui.bottomsheets.logintypes.LoginTypesBottomSheet
import com.app.muselink.ui.bottomsheets.signup.SignUpBottomSheet
import com.app.muselink.util.intentComponent
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import soup.neumorphism.NeumorphCardView

@AndroidEntryPoint
class SignUpTypesBottomSheet :
    BaseViewModelDialogFragment<SignupBottomsheetBinding, SignupTypesViewModel>() {
    /**
     * View Model Class
     * */

    override fun getViewModelClass(): Class<SignupTypesViewModel> {
        return SignupTypesViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.signup_bottomsheet
    }
    /**
     * OnViewCreated
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vmSignUp = viewModel
        binding?.bottomSheetDialog = this
        setUpObserver()
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        val npmSignupWithEmail = view.findViewById<NeumorphCardView>(R.id.npmSignupWithEmail)
        val tvLogin = view.findViewById<TextView>(R.id.tvLogin)
        val termCondition = view.findViewById<TextView>(R.id.termCondition)

        tvLogin.setOnClickListener {
            this.dismiss()
            LoginTypesBottomSheet().show(requireActivity().supportFragmentManager, "Login")
        }

        npmSignupWithEmail.setOnClickListener {
            this.dismiss()
            SignUpBottomSheet().show(requireActivity().supportFragmentManager, "OtpScreen")
        }
        termCondition.spannableString(Pair("Terms of Use", View.OnClickListener {
            requireActivity().intentComponent(ActivityTermOfUse::class.java, null)
        }),
            Pair("Privacy Policy", View.OnClickListener {
                requireActivity().intentComponent(ActivityPrivacyPolicy::class.java, null)
            })
        )

        imgClose.setOnClickListener {
            this.dismiss()
        }
    }

    /**
     * Response Observer
     * */
    private fun setUpObserver() {
        viewModel?.signUpResponse?.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            dismiss()
                            SharedPrefs.saveUser(it?.data.data)
                            SuccessBottomSheet(requireActivity())
                                .show(requireActivity().supportFragmentManager, "SuccessScreen")
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    showToast(requireActivity(), it.message)
                }
                Resource.Status.LOADING -> {
                    this.dialog?.setCanceledOnTouchOutside(false)
                    this.dialog?.setCancelable(false)
                }
            }
        }

    }

    /**
     * Social Login Results
     * */
    fun instagramResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel?.onActivityResult(requestCode, resultCode, data)
    }

    fun facebookResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel?.onActivityResult(requestCode, resultCode, data)
    }

    private fun TextView.spannableString(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }

                override fun updateDrawState(textPaint: TextPaint) {
                    super.updateDrawState(textPaint)
                    textPaint.color = Color.parseColor("#73CADC")
                    textPaint.isUnderlineText = true
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
        this.highlightColor = Color.TRANSPARENT
    }
}