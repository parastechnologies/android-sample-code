package com.app.muselink.ui.bottomsheets.login

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ehsanmashhadi.library.model.Country
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.LoginBottomSheetBinding
import com.app.muselink.listener.CountrySelectionNavigator
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.ui.bottomsheets.forgotpassword.ForgotPasswordBottomSheet
import com.app.muselink.ui.bottomsheets.otp.OtpBottomSheet
import com.app.muselink.ui.fragments.home.dashboard.DashboardViewModel.Companion.SUBSCRIPTION
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_phone_number_signup.*
import kotlinx.android.synthetic.main.login_bottom_sheet.*
import soup.neumorphism.NeumorphCardView

@AndroidEntryPoint
class LoginBottomSheet(var mcontext: Context) :
    BaseViewModelDialogFragment<LoginBottomSheetBinding, LoginViewModel>() {
    /**
     * ViewModel Class
     * */
    override fun getViewModelClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.login_bottom_sheet
    }

    /**
     * onViewCreated
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        setupObservers()
        val npmPhone = view.findViewById<NeumorphCardView>(R.id.npmPhone)
        val npmEmail = view.findViewById<NeumorphCardView>(R.id.npmEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        val llPhoneNumber = view.findViewById<LinearLayout>(R.id.llPhoneNumberLogin)
        val llEmail = view.findViewById<LinearLayout>(R.id.llEmail)
        val btnContinueSignUp = llEmail.findViewById<NeumorphCardView>(R.id.btnContinueSignUp)
        val btnSendCode = llPhoneNumber.findViewById<NeumorphCardView>(R.id.btnSendCode)
        val tvForgotPassword = llEmail.findViewById<TextView>(R.id.tvForgotPassword)
        tvForgotPassword.setOnClickListener {
            dismiss()
            ForgotPasswordBottomSheet().show(requireActivity().supportFragmentManager,"ForgotPassword")
        }
        btnContinueSignUp.setOnClickListener {
            viewModel?.callApiLogin()
        }
        btnSendCode.setOnClickListener {
            viewModel?.callApiLogin()
        }
        imgClose.setOnClickListener {
            this.dismiss()
        }
        npmEmail.setOnClickListener {
            viewModel?.authType = SyncConstants.AuthTypes.EMAIL.value
            selectedView(npmEmail, npmPhone)
            tvEmail.isSelected = true
            tvPhone.isSelected = false
            llPhoneNumberLogin.visibility = View.GONE
            llEmail.visibility = View.VISIBLE
        }
        npmCountryPicker.setOnClickListener {
            viewModel?.openCountryPicker(requireActivity(), countrySelectionNavigator)
        }
        npmPhone.setOnClickListener {
            viewModel?.authType = SyncConstants.AuthTypes.PHONE.value
            selectedView(npmPhone, npmEmail)
            tvEmail.isSelected = false
            tvPhone.isSelected = true
            llPhoneNumberLogin.visibility = View.VISIBLE
            llEmail.visibility = View.GONE
        }
        npmPhone.performClick()
        viewModel?.authType = SyncConstants.AuthTypes.PHONE.value
    }
    /**
     * Country select listener
     * */
    var countrySelectionNavigator = object : CountrySelectionNavigator {
        override fun onSelectCountry(country: Country?) {
            (binding as LoginBottomSheetBinding).llPhoneNumberLogin.tvPhoneCodeLogin.text =
                country?.dialCode
            viewModel?.phoneCode = country?.dialCode.toString()
        }
    }
    /**
     * Login Response Observer
     * */
    private fun setupObservers() {
        viewModel?.loginResponse?.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (viewModel?.authType.equals(SyncConstants.AuthTypes.PHONE.value)) {
                        binding?.showloaderPhone = false
                    } else {
                        binding?.showloader = false
                    }
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            dismiss()
                            SharedPrefs.saveUser(it?.data.data)
                            if (viewModel?.authType.equals(SyncConstants.AuthTypes.PHONE.value)) {
                                dismiss()
                                OtpBottomSheet(SyncConstants.CredentialTypes.LOGIN.value,viewModel?.phoneCode + viewModel?.phone.toString()).show(requireActivity().supportFragmentManager, "OtpScreen")
                            }else {
                                callLoginSuccess()
                                dismiss()
                            }
                            /**Call Subscription api*/
                            callSubscriptionApi()
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    if (viewModel?.authType.equals(SyncConstants.AuthTypes.PHONE.value)) {
                        binding?.showloaderPhone = false
                    } else {
                        binding?.showloader = false
                    }
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    showToast(requireActivity(), it.message)
                }
                Resource.Status.LOADING -> {
                    if (viewModel?.authType.equals(SyncConstants.AuthTypes.PHONE.value)) {
                        binding?.showloaderPhone = true
                    } else {
                        binding?.showloader = true
                    }
                    this.dialog?.setCanceledOnTouchOutside(false)
                    this.dialog?.setCancelable(false)
                }
            }
        }
    }
    private fun callLoginSuccess(){
        val intent= Intent("LOGIN")
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent)
    }
    private fun callSubscriptionApi(){
        val intent=Intent(SUBSCRIPTION)
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent)
    }
    private fun selectedView(cardSelected: NeumorphCardView, cardUnSelected: NeumorphCardView) {
        cardSelected.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorSky))
        cardUnSelected.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.transparent))
    }
}