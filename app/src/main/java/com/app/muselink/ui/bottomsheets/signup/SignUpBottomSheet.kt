package com.app.muselink.ui.bottomsheets.signup

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ehsanmashhadi.library.model.Country
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.SignupPhoneEmailBottomSheetBinding
import com.app.muselink.listener.CountrySelectionNavigator
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.ui.bottomsheets.SuccessBottomSheet
import com.app.muselink.ui.bottomsheets.otp.OtpBottomSheet
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast
import kotlinx.android.synthetic.main.layout_phone_number_signup.*
import soup.neumorphism.NeumorphCardView
class SignUpBottomSheet :
    BaseViewModelDialogFragment<SignupPhoneEmailBottomSheetBinding, SignupViewModel>() {
    /**
     *SignUp ViewModel
     */
    override fun getViewModelClass(): Class<SignupViewModel> {
        return SignupViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.signup_phone_email_bottom_sheet
    }

    /**
     * OnCreate Dialog
     * */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog

    }

    /**
     * onViewCreated
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vmSignUp = viewModel
        setUpObserver()
        val npmPhone = view.findViewById<NeumorphCardView>(R.id.npmPhone)
        val npmEmail = view.findViewById<NeumorphCardView>(R.id.npmEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        val llPhoneNumber = view.findViewById<LinearLayout>(R.id.llPhoneNumber)
        val llEmail = view.findViewById<LinearLayout>(R.id.llEmail)
        val btnContinueSignUp = llEmail.findViewById<NeumorphCardView>(R.id.btnContinueSignUp)
        val btnSendCode = llPhoneNumber.findViewById<NeumorphCardView>(R.id.btnSendCode)

        btnContinueSignUp.setOnClickListener {
            viewModel?.callApiSignUp()

        }
        btnSendCode.setOnClickListener {
            viewModel?.callApiSignUp()
        }
        imgClose.setOnClickListener {
            this.dismiss()
        }
        npmCountryPicker.setOnClickListener {
            viewModel?.openCountryPicker(requireActivity(), countrySelectionNavigator)
        }
        npmEmail.setOnClickListener {
            viewModel?.authType = SyncConstants.AuthTypes.EMAIL.value
            selectedView(npmEmail, npmPhone)
            tvEmail.isSelected = true
            tvPhone.isSelected = false
            llPhoneNumber.visibility = View.GONE
            llEmail.visibility = View.VISIBLE
        }
        npmPhone.setOnClickListener {
            viewModel?.authType = SyncConstants.AuthTypes.PHONE.value
            selectedView(npmPhone, npmEmail)
            tvEmail.isSelected = false
            tvPhone.isSelected = true
            llPhoneNumber.visibility = View.VISIBLE
            llEmail.visibility = View.GONE
        }
        npmPhone.performClick()
        viewModel?.authType = SyncConstants.AuthTypes.PHONE.value
    }

    var countrySelectionNavigator = object : CountrySelectionNavigator {
        override fun onSelectCountry(country: Country?) {
            binding?.llPhoneNumber?.tvPhoneCode?.text = country?.dialCode
            viewModel?.phoneCode = country?.dialCode.toString()
        }
    }

    /**
     * Response Observer
     * */
    private fun setUpObserver() {
        viewModel?.signUpResponse?.observe(viewLifecycleOwner) {
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
                                OtpBottomSheet(
                                    SyncConstants.CredentialTypes.SIGN_UP.value,
                                    viewModel?.phoneCode + viewModel?.phone.toString()
                                )
                                    .show(requireActivity().supportFragmentManager, "OtpScreen")
                            } else {
                                SuccessBottomSheet(requireActivity())
                                    .show(requireActivity().supportFragmentManager, "SuccessScreen")
                            }
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

                Resource.Status.LOADING
                -> {
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

    /**
     * OnView Selected
     * */
    private fun selectedView(cardSelected: NeumorphCardView, cardUnSelected: NeumorphCardView) {
        cardSelected.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorSky))
        cardUnSelected.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.transparent
            )
        )
    }


}