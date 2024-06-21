package com.mindbyromanzanoni.view.bottomsheet.registersuccessfully

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.databinding.BottomsheetRegistersuccessfullyBinding
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.view.activity.dashboard.DashboardActivity

class BottomSheetRegisterSuccessfully : BottomSheetDialogFragment(), View.OnClickListener {
    private var binding: BottomsheetRegistersuccessfullyBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_registersuccessfully, container, false) as BottomsheetRegistersuccessfullyBinding
        setPeekHeight()
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickListener()
    }

    private fun setPeekHeight() {
        dialog?.setOnShowListener {
            val dialogParent = binding?.layoutCoordinate?.parent as View
            BottomSheetBehavior.from(dialogParent).peekHeight =
                (binding?.layoutCoordinate?.height!! * 0.99).toInt()
            dialogParent.requestLayout()
        }
    }


    override fun getTheme(): Int {
        return R.style.CustomBottomSheetTheme
    }


    fun onClickListener() {
        binding?.btnGotoHome?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {

            binding?.btnGotoHome -> {
                requireActivity().launchActivity<DashboardActivity> { }
                requireActivity().finishAffinity()
            }

        }
    }
}