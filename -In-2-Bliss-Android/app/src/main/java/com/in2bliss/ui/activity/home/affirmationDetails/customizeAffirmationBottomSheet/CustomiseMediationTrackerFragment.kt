package com.in2bliss.ui.activity.home.affirmationDetails.customizeAffirmationBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.meditationTracker.MediationTrackerModel
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.FragmentCustomiseMediationTrackerBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet.MusicCategoryBottomSheet
import com.in2bliss.ui.activity.home.meditationTrackerMeditate.session.StartSessionActivity
import com.in2bliss.ui.activity.home.player.PlayerViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CustomiseMediationTrackerFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var requestManager: RequestManager
    private val viewModel: PlayerViewModel by viewModels()
    lateinit var binding: FragmentCustomiseMediationTrackerBinding

    private var isSilentMeditation = false
    private var musicCategoriesBottomSheet: MusicCategoryBottomSheet? = null
    private var mediationTrackerModel: MediationTrackerModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomiseMediationTrackerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 3000

        mediationTrackerModel = MediationTrackerModel()
        getIntentData()
        onClick()
        settingNumberPicker()

        isBackgroundMusicSelected(
            isMusicSelected = false
        )
        observer()
    }

    private fun getIntentData() {
        arguments?.getString(AppConstant.MEDITATION_TYPE)?.let { type ->
            isSilentMeditation = type == AppConstant.SILENT_MEDITATION

            binding.clMusicDuringAffirmation.visibility(
                isVisible = isSilentMeditation.not()
            )

            if (isSilentMeditation) {
                binding.tvTitle.text = getString(R.string.silent_meditation)
            }
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.musicCategories.collectLatest {
                handleResponse(response = it,
                    context = requireActivity(),
                    showToast = false,
                    success = { response ->
                        viewModel.musicCategoriesData = response
                        musicCategoriesBottomSheet()
                    },
                    errorBlock = {
                        viewModel.musicCategoriesData = null
                        musicCategoriesBottomSheet()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.musicList.collectLatest {
                handleResponse(response = it, context = requireActivity(), success = { response ->
                    musicListBottomSheet(response)
                }, showToast = false, errorBlock = {
                    musicListBottomSheet(null)
                }, error = { message, apiName ->
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

    private fun onClick() {
        binding.MinutePicker.setOnValueChangedListener { _, _, p2 ->
            mediationTrackerModel?.min = p2
            setMusicTime()
        }

        binding.hourPicker.setOnValueChangedListener { _, _, p2 ->
            mediationTrackerModel?.hours = p2
            setMusicTime()
        }

        binding.btnStartSession.setOnClickListener {
            val bundle = Bundle()
            if (binding.tvCustomizeYourMusicTime.text.isEmpty() && isSilentMeditation) {
                requireActivity().showToast("please select time")
                return@setOnClickListener
            }
            if (binding.tvCustomizeYourMusicTime.text.isEmpty() && isSilentMeditation.not()) {
                requireActivity().showToast("please select time")
                return@setOnClickListener
            }
            if (mediationTrackerModel?.musicUrl.isNullOrEmpty() && isSilentMeditation.not()) {
                requireActivity().showToast("please select background music")
                return@setOnClickListener
            }

            bundle.putString(AppConstant.PLAY_MUSIC_MEDIATION, Gson().toJson(mediationTrackerModel))
            requireActivity().intent(
                destination = StartSessionActivity::class.java, bundle
            )
            requireActivity().finish()
            dismiss()
        }

        binding.toggleEndBell.setOnCheckedChangeListener { _, isChecked ->
            mediationTrackerModel?.playEndBell = isChecked
        }

        binding.tvBackgroundMusicTitle.setOnClickListener {
            musicCategories()
        }

        binding.tvNoBackgroundMusicSelected.setOnClickListener {
            musicCategories()
        }
    }

    private fun settingNumberPicker() {
        binding.MinutePicker.maxValue = 59
        binding.MinutePicker.minValue = 0
        binding.hourPicker.maxValue = 11
        binding.hourPicker.minValue = 0
    }

    private fun setMusicTime() {
        val customMusicHours = String.format("%02d", mediationTrackerModel?.hours)
        val customMusicMin = String.format("%02d", mediationTrackerModel?.min)
        lifecycleScope.launch(Dispatchers.Main) {
            val selectedMusicTime = "$customMusicHours:$customMusicMin"
            binding.tvCustomizeYourMusicTime.text = selectedMusicTime
        }
    }

    private fun musicCategories() {
        if (viewModel.musicCategoriesData == null) {
            viewModel.retryApiRequest(
                apiName = ApiConstant.MUSIC_CATEGORIES
            )
            return
        }
        musicCategoriesBottomSheet()
    }

    /**
     * Music list bottom sheet and setting the selected background music and its details
     * */
    private fun musicListBottomSheet(data: MusicList?) {
        MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_LIST_DATA to if (data == null) null else Gson().toJson(data),
                AppConstant.IS_MUSIC_CATEGORY to false,
                AppConstant.CUSTOMIZE to true,
                AppConstant.REAL_CATEGORY to this@CustomiseMediationTrackerFragment.arguments?.getString(AppConstant.MEDITATION_TYPE)
            )
            backgroundMusicUrl = { selectedBackgroundMusic ->

                isBackgroundMusicSelected(
                    isMusicSelected = true
                )
                mediationTrackerModel?.musicUrl = selectedBackgroundMusic?.audio
                mediationTrackerModel?.musicId = selectedBackgroundMusic?.id
                mediationTrackerModel?.musicTitle = selectedBackgroundMusic?.audioName
                mediationTrackerModel?.musicImage = selectedBackgroundMusic?.thumbnail

                settingBackgroundMusicData(
                    title = selectedBackgroundMusic?.audioName,
                    image = selectedBackgroundMusic?.thumbnail
                )
                musicCategoriesBottomSheet?.dismiss()
            }
        }.also {
            it.show(
                childFragmentManager, null
            )
        }
    }

    private fun settingBackgroundMusicData(
        title: String?,
        image: String?
    ) {
        binding.tvBackgroundMusicTitle.text = title
        binding.ivBackgroundImage.glide(
            requestManager = requestManager,
            image = BuildConfig.MUSIC_BASE_URL.plus(image),
            placeholder = R.drawable.ic_error_place_holder,
            error = R.drawable.ic_error_place_holder
        )
    }

    private fun isBackgroundMusicSelected(
        isMusicSelected: Boolean
    ) {
        binding.tvNoBackgroundMusicSelected.visibility(
            isVisible = isMusicSelected.not()

        )
        binding.backgroundMusic.visibility(
            isVisible = isMusicSelected
        )
    }

    private fun musicCategoriesBottomSheet() {
        musicCategoriesBottomSheet = MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_CATEGORIES_DATA to Gson().toJson(viewModel.musicCategoriesData),
                AppConstant.IS_MUSIC_CATEGORY to true,
                AppConstant.CUSTOMIZE to true

            )

            categoryId = { categoryId ->
                viewModel.categoryId = categoryId
                viewModel.retryApiRequest(
                    apiName = ApiConstant.MUSIC_LIST
                )
            }
        }
        musicCategoriesBottomSheet?.show(
            childFragmentManager, null
        )
    }
}