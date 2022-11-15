package com.app.muselink.ui.bottomsheets.otp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.retrofit.Resource
import com.app.muselink.databinding.OtpBottomsheetBinding
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.ui.bottomsheets.SuccessBottomSheet
import com.app.muselink.util.hideKeyboard
import com.app.muselink.util.showKeyboard
import com.app.muselink.util.showToast
import com.facebook.share.Share
import dagger.hilt.android.AndroidEntryPoint
import soup.neumorphism.NeumorphCardView


@AndroidEntryPoint
class OtpBottomSheet(private val credentialType: String, private val phoneNumber: String) :
    BaseViewModelDialogFragment<OtpBottomsheetBinding, OtpViewModel>() {

    /**
     * ViewModel Class
     * */

    override fun getViewModelClass(): Class<OtpViewModel> {
        return OtpViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.otp_bottomsheet
    }


    /**
     * onCreate
     * */
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    /**
     * OnView Created
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupObserversLoginOtp()
        setupObserversSignUpOtp()
      // detail_otp
       //

        binding?.vm = viewModel
        viewModel?.credentialType = credentialType
        viewModel?.phoneNumber = phoneNumber

        binding?.tvMobile?.text=context?.getString(R.string.detail_otp)+" "+SharedPrefs.getString(AppConstants.MOBILE_NUM)

        binding?.etOne?.addTextChangedListener(
            GenericTextWatcherNew(
                binding!!.etOne,
                binding!!.etTwo,
                requireActivity()
            )
        )
        binding?.etTwo?.addTextChangedListener(
            GenericTextWatcherNew(
                binding!!.etTwo,
                binding!!.etThree,
                requireActivity()
            )
        )
        binding?.etThree?.addTextChangedListener(
            GenericTextWatcherNew(
                binding!!.etThree,
                binding!!.etFour,
                requireActivity()
            )
        )
        binding?.etFour?.addTextChangedListener(
            GenericTextWatcherNew(
                binding!!.etFour,
                null,
                requireActivity()
            )
        )

        binding!!.etOne.setOnKeyListener(GenericKeyEvent(binding!!.etOne, null))
        binding!!.etTwo.setOnKeyListener(GenericKeyEvent(binding!!.etTwo, binding!!.etOne))
        binding!!.etThree.setOnKeyListener(GenericKeyEvent(binding!!.etThree, binding!!.etTwo))
        binding!!.etFour.setOnKeyListener(GenericKeyEvent(binding!!.etFour, binding!!.etThree))

        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().showKeyboard(binding!!.etOne)
        }, 300)

        val npmBackBtn = view.findViewById<NeumorphCardView>(R.id.npmBackBtn)

        npmBackBtn.setOnClickListener {
            this.dismiss()
        }

    }

    class GenericKeyEvent internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.et_one && currentView.text.isEmpty()) {
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }
    }

    class GenericTextWatcherNew internal constructor(
        private val currentView: View,
        private val nextView: View?,
        val activity: Activity
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.et_one -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_two -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_three -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_four -> activity.hideKeyboard()
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    /**
     * Observer SignUp With Otp Response
     * */
    private fun setupObserversSignUpOtp() {
        viewModel?.signUpResponse?.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            showToast(requireActivity(), getString(R.string.otp_sent_successfully))
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
     * Observer Login With Otp Response
     * */
    private fun setupObserversLoginOtp() {
        viewModel?.loginResponse?.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            showToast(requireActivity(), getString(R.string.otp_sent_successfully))
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
     * Observer Verify otp Response
     * */
    private fun setupObservers() {
        viewModel?.verifyOtpResponse?.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding?.showloader = false
                    this.dialog?.setCanceledOnTouchOutside(true)
                    this.dialog?.setCancelable(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            SharedPrefs.saveUser(it.data.data)
                            this.dismiss()
                            callLoginSuccess()
                            SuccessBottomSheet(requireActivity()).show(requireActivity().supportFragmentManager, "SuccessScreen")
                        } else {
                            showToast(requireActivity(), it.data.message)
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
        }
    }
    private fun callLoginSuccess(){
        val intent= Intent("LOGIN")
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent)
    }
}