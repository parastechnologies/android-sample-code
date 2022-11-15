package com.app.muselink.ui.bottomsheets.logintypes

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.LoginTypesBottomSheetBinding
import com.app.muselink.ui.activities.settings.privacypolicy.ActivityPrivacyPolicy
import com.app.muselink.ui.activities.settings.termofuse.ActivityTermOfUse
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.ui.bottomsheets.SuccessBottomSheet
import com.app.muselink.ui.bottomsheets.login.LoginBottomSheet
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.util.intentComponent
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import soup.neumorphism.NeumorphCardView
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@AndroidEntryPoint
class
LoginTypesBottomSheet :
    BaseViewModelDialogFragment<LoginTypesBottomSheetBinding, LoginTypesViewModel>() {

    /**
     * ViewModel Class
     * */
    override fun getViewModelClass(): Class<LoginTypesViewModel> {
        return LoginTypesViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.login_types_bottom_sheet
    }

    /**
     * [onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        printHashKey(requireContext())
        binding?.vmLogin = viewModel
        binding?.bottomSheetDialog = this

        setUpObserver()
        socialLoginObserver()

        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        val npmLoginWithEmail = view.findViewById<NeumorphCardView>(R.id.npmLoginWithEmail)
        val tvSignUp = view.findViewById<TextView>(R.id.tvSignUp)
        val termCondition = view.findViewById<TextView>(R.id.termCondition)

        imgClose.setOnClickListener {
            dismiss()
        }

        npmLoginWithEmail.setOnClickListener {
            dismiss()
            LoginBottomSheet(
                requireActivity()
            ).show(requireActivity().supportFragmentManager, "LoginBottomSheet")
        }
        termCondition.spannableString( Pair("Terms of Use", View.OnClickListener {
            requireActivity().intentComponent(ActivityTermOfUse::class.java,null)
        }),
            Pair("Privacy Policy", View.OnClickListener {
                requireActivity().intentComponent(ActivityPrivacyPolicy::class.java,null)
            }))


        tvSignUp.setOnClickListener {
            dismiss()
            SignUpTypesBottomSheet().show(requireActivity().supportFragmentManager, "SignUp")
        }

    }

    /**
     * Observer OnResponse
     * */
    private fun setUpObserver() {
        viewModel?.loginResponse?.observe(viewLifecycleOwner) {
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

                Resource.Status.LOADING
                -> {
                    this.dialog?.setCanceledOnTouchOutside(false)
                    this.dialog?.setCancelable(false)
                }
            }
        }

    }

    /**
     * Social Login Observer
     * */
    private fun socialLoginObserver() {
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

                Resource.Status.LOADING
                -> {
                    this.dialog?.setCanceledOnTouchOutside(false)
                    this.dialog?.setCancelable(false)
                }
            }
        }

    }

    /**
     * Social result
     * */
    fun instagramResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel?.onActivityResult(requestCode, resultCode, data)
    }

    fun facebookResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel?.onActivityResult(requestCode, resultCode, data)
    }
    fun TextView.spannableString(vararg links: Pair<String, View.OnClickListener>) {
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

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager()
                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("xcsadsdffsd", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("xcsadsdffsd", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("xcsadsdffsd", "printHashKey()", e)
        }
    }
}