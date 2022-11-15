package com.app.muselink.ui.bottomsheets.changePhoneNumber

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.ehsanmashhadi.library.model.Country
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.ChangePhoneNumberBottomsheetBinding
import com.app.muselink.listener.CountrySelectionNavigator
import com.app.muselink.listener.OnUpdate
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.change_phone_number_bottomsheet.*

@AndroidEntryPoint
class ChangePhoneNumberBottomSheet(var listener: OnUpdate) :
    BaseViewModelDialogFragment<ChangePhoneNumberBottomsheetBinding, ChangePhoneNumberViewModel>() {

    /**
     * ChangePhoneNumber ViewModel class
     * */
    override fun getViewModelClass(): Class<ChangePhoneNumberViewModel> {
        return ChangePhoneNumberViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.change_phone_number_bottomsheet
    }

    /**
     * [onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        setupObservers()
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)
        imgClose.setOnClickListener {
            dismiss()
        }
        npmCountryPicker.setOnClickListener {
            viewModel?.openCountryPicker(requireActivity(), countrySelectionNavigator)
        }
    }


    /**
     * Observer change phone number Response
     * */
    private fun setupObservers() {
        viewModel?.changePhoneNumberResponse?.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)

                    if (it.data != null) {

                        if (it.data.status.equals("200")) {
                            SharedPrefs.save(AppConstants.PREFS_PHONE, viewModel?.phone)
                            listener.onPhoneUpdate()
                            this.dismiss()
                        } else {
                            showToast(
                                requireActivity(),
                                "Congratulations !your Phone Number has been changed"
                            )
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    showToast(requireActivity(), it.message)
                }
                Resource.Status.LOADING -> {
                    binding?.showloader = true
                    this.dialog?.setCanceledOnTouchOutside(false)
                    this.dialog?.setCancelable(false)
                }
            }
        })
    }

    /**
     * Country code picker listener
     * */
    var countrySelectionNavigator = object : CountrySelectionNavigator {
        override fun onSelectCountry(country: Country?) {
            (binding as ChangePhoneNumberBottomsheetBinding).tvPhoneCodeLogin.text =
                country?.dialCode
            viewModel?.phoneCode = country?.dialCode.toString()
        }
    }


}