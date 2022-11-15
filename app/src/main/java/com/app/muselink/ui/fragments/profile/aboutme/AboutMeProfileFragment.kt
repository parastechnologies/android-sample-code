package com.app.muselink.ui.fragments.profile.aboutme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.FragmentProfileAboutmeBinding
import com.app.muselink.ui.activities.profile.editbiography.EditBiography_Activity
import com.app.muselink.ui.activities.profile.editgoals.EditGoalsActivity
import com.app.muselink.ui.base.fragment.BaseViewModelFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile_aboutme.*

@AndroidEntryPoint
class AboutMeProfileFragment :
    BaseViewModelFragment<FragmentProfileAboutmeBinding, AboutMeViewModal>() {


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        viewModel?.viewLifecycleOwner = this
        viewModel?.binding = binding
        viewModel?.setupObserversPersonalInterest()
        viewModel?.initRecyclerViewInterests(1)
        viewModel?.initRecyclerViewGoals(1)
        binding?.etBiography?.movementMethod = ScrollingMovementMethod()
        binding?.etBiography?.setOnTouchListener { v, event ->
            if (v.id == R.id.etBiography) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
        setListeners()
    }
    override fun onResume() {
        super.onResume()
        viewModel?.callApiGetInterests()
    }
    private fun setListeners() {
        npmCarrerGoals.setOnClickListener {
            val intent = Intent(requireActivity(),EditGoalsActivity::class.java)
            startActivity(intent)
        }
        npmBiography.setOnClickListener {
            val intent = Intent(requireActivity(),EditBiography_Activity::class.java)
            startActivity(intent)
        }
    }
    override fun getViewModelClass(): Class<AboutMeViewModal> {
        return AboutMeViewModal::class.java
    }

    override fun getLayout(): Int {
        return R.layout.fragment_profile_aboutme
    }

}