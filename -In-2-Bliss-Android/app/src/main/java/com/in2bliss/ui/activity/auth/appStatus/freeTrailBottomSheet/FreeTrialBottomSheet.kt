package com.in2bliss.ui.activity.auth.appStatus.freeTrailBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.in2bliss.databinding.LayoutFreeTrialBinding
import com.in2bliss.ui.activity.auth.appStatus.AppStatusViewModel
import com.in2bliss.ui.activity.home.welcome.WelcomeActivity
import com.in2bliss.utils.extension.intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreeTrialBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: AppStatusViewModel by viewModels()
    private lateinit var binding: LayoutFreeTrialBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFreeTrialBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 1800
        onClick()
    }

    private fun onClick() {
//        binding.tvMayBeLater.setOnClickListener {
//            requireActivity().intent(
//                destination = HomeActivity::class.java
//            )
//            dismiss()
//        }

        binding.btnRemindMe.setOnClickListener {
            requireActivity().intent(
                destination = WelcomeActivity::class.java
            )
            dismiss()
        }
    }
}