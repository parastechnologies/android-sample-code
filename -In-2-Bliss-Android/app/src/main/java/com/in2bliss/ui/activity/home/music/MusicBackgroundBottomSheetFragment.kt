package com.in2bliss.ui.activity.home.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.FragmentMusicBackgroundBottomSheetBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.player.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicBackgroundBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentMusicBackgroundBottomSheetBinding
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicBackgroundBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 3000
        viewModel.retryApiRequest(
            apiName = ApiConstant.MUSIC_CATEGORIES
        )
        observe()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.musicCategories.collectLatest {
                handleResponse(response = it,
                    context = requireActivity(),
                    showToast = false,
                    success = { response ->

                    },
                    errorBlock = {
                        viewModel.musicCategoriesData = null
                    },

                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }
    }
}