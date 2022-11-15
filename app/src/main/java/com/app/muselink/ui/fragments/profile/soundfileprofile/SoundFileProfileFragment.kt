package com.app.muselink.ui.fragments.profile.soundfileprofile

import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.FragmentSoundfileProfileBinding
import com.app.muselink.ui.base.fragment.BaseViewModelFragment

class SoundFileProfileFragment : BaseViewModelFragment<FragmentSoundfileProfileBinding, SoundFileProfileViewModel>() {
    /**
     * Sound File Profile ViewModel
     * */
    override fun getViewModelClass(): Class<SoundFileProfileViewModel> {
        return SoundFileProfileViewModel::class.java
    }
    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.fragment_soundfile_profile
    }
    /**
     * [onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        viewModel?.viewLifecycleOwner = this
        viewModel?.binding = binding
        viewModel?.setupObserversSoundFileProfile()
        viewModel?.setupObserversDeletAudio()
        viewModel?.setupObserversChnageStatus()
        viewModel?.initRecyclerView()
    }
    override fun onResume() {
        super.onResume()
        viewModel?.callApiGetSoundFiles()
    }




}