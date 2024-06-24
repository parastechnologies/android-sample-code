package com.in2bliss.ui.activity.home.fragment.updateAffirmation.audio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.in2bliss.R
import com.in2bliss.base.BaseFragment
import com.in2bliss.databinding.FragmentAudioBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioFragment : BaseFragment<FragmentAudioBinding>(
    layoutInflater = FragmentAudioBinding::inflate
) {

    private val viewModel: AudioVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.data = viewModel
        binding.musicPlayerSlider.setCustomThumbDrawable(R.drawable.ic_player_playback_seekbar)
        onClick()
    }

    private fun onClick() {
    }

}

