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
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.data.model.musicDetails.MusicCustomizeDetails
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.FragmentCustomizeAffirmationBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet.MusicCategoryBottomSheet
import com.in2bliss.ui.activity.home.player.PlayerViewModel
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.convertTimeToMilliseconds
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CustomiseAffirmationFragment(
    private var onSubmitClick: (musicCustomizeDetails: MusicCustomizeDetails?) -> Unit
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var requestManager: RequestManager
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var binding: FragmentCustomizeAffirmationBinding
    private var categoryName: AppConstant.HomeCategory? = null
    private var isFromPlayer: Boolean = false
    private var musicCustomizeDetails: MusicCustomizeDetails? = null
    private var affirmationTimePicker = false
    private var musicTimePicker = false
    private var musicCategoriesBottomSheet: MusicCategoryBottomSheet? = null
    private var musicListBottomSheet: MusicCategoryBottomSheet? = null
    private var isCustomizationEnabled: Boolean = false
    private var du: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomizeAffirmationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 3000
        gettingBundle()
        binding.darkMode.visibility(
            isVisible = musicCustomizeDetails?.isSleep ?: false
        )
        settingNumberPicker()
        onClick()
        setAffirmationTime()
        setMusicTime()
        observer()
        if (categoryName == AppConstant.HomeCategory.WISDOM_INSPIRATION || categoryName == AppConstant.HomeCategory.MUSIC) {
            binding.tvDefaultMusic.gone()
            binding.defaultMusicMode.gone()
        } else if (categoryName == AppConstant.HomeCategory.GUIDED_MEDITATION) {
            binding.tvDefaultMusic.gone()
            binding.defaultMusicMode.gone()
            binding.clTimePicker.gone()
            binding.clCustomizeYourSession.gone()
            binding.tvCustomizeYourSession.gone()
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.musicCategories.collectLatest {
                handleResponse(
                    response = it,
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
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.musicList.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = { response ->
                        musicListBottomSheet(response)
                    },
                    showToast = false,
                    errorBlock = {
                        musicListBottomSheet(null)
                    },
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }
    }

    private fun setAffirmationTime() {
        val customAffirmationHours = if (musicCustomizeDetails?.affirmationHour != null) {
            String.format("%02d", musicCustomizeDetails?.affirmationHour)
        } else "00"
        val customAffirmationMin = if (musicCustomizeDetails?.affirmationMinute != null) {
            String.format("%02d", musicCustomizeDetails?.affirmationMinute)
        } else "00"
        lifecycleScope.launch(Dispatchers.Main) {
            val selectedAffirmationTime = "$customAffirmationHours:$customAffirmationMin"
            binding.tvcustomizeYourSessionTime.text = selectedAffirmationTime
        }
    }

    private fun setMusicTime() {
        val customMusicHours = if (musicCustomizeDetails?.backgroundMusicHour != null) {
            String.format("%02d", musicCustomizeDetails?.backgroundMusicHour)
        } else "00"
        val customMusicMin = if (musicCustomizeDetails?.backgroundMusicMinute != null) {
            String.format("%02d", musicCustomizeDetails?.backgroundMusicMinute)
        } else "00"
        lifecycleScope.launch(Dispatchers.Main) {
            val selectedMusicTime = "$customMusicHours:$customMusicMin"
            binding.tvCustomizeMusicLengthTime.text = selectedMusicTime
        }
    }

    private fun settingNumberPicker() {
        binding.MinutePicker.maxValue = 59
        binding.MinutePicker.minValue = 0
        binding.hourPicker.maxValue = 11
        binding.hourPicker.minValue = 0

        binding.minutePickerMusic.maxValue = 59
        binding.minutePickerMusic.minValue = 0
        binding.hourPickerMusic.maxValue = 11
        binding.hourPickerMusic.minValue = 0
    }

    private fun onClick() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.ivClose2.setOnClickListener {
            dismiss()
        }

        binding.btnStartSession.setOnClickListener {
            selectedDuration()
        }

        binding.btnSelectTime.setOnClickListener {
            selectedDuration()
        }

        binding.MinutePicker.setOnValueChangedListener { _, _, p2 ->
            musicCustomizeDetails?.affirmationMinute = p2
            setAffirmationTime()
        }
        binding.hourPicker.setOnValueChangedListener { _, _, p2 ->
            musicCustomizeDetails?.affirmationHour = p2
            setAffirmationTime()
        }
        binding.minutePickerMusic.setOnValueChangedListener { _, _, p2 ->
            musicCustomizeDetails?.backgroundMusicMinute = p2
            setMusicTime()
        }
        binding.hourPickerMusic.setOnValueChangedListener { _, _, p2 ->
            musicCustomizeDetails?.backgroundMusicHour = p2
            setMusicTime()
        }

        binding.clMusicLength.setOnClickListener {
            musicTimePicker = musicTimePicker.not()
            binding.clTimePickerMusic.visibility(
                isVisible = musicTimePicker
            )
        }
        binding.clCustomizeYourSession.setOnClickListener {
            affirmationTimePicker = affirmationTimePicker.not()
            binding.clTimePicker.visibility(
                isVisible = affirmationTimePicker
            )
        }
        binding.switchMusicEnabled.setOnClickListener {
            musicCustomizeDetails?.isBackgroundMusicEnabled = binding.switchMusicEnabled.isChecked
        }
        binding.switchNextTime.setOnClickListener {
            musicCustomizeDetails?.isSaveForNextTime = binding.switchNextTime.isChecked
        }

        binding.tvBackgroundMusicTitle.setOnClickListener {
            musicCategories()
        }

        binding.tvNoBackgroundMusicSelected.setOnClickListener {
            musicCategories()
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
        if (musicListBottomSheet?.dialog?.isShowing == true) {
            return
        }

        musicListBottomSheet = MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_LIST_DATA to if (data == null) null else Gson().toJson(data),
                AppConstant.IS_MUSIC_CATEGORY to false,
                AppConstant.CUSTOMIZE to true
            ).also {
                AppConstant.REAL_CATEGORY.let {key->
                    if (this@CustomiseAffirmationFragment.arguments?.containsKey(key) == true){
                        it.putString(key,this@CustomiseAffirmationFragment.arguments?.getString(key))
                    }
                }
            }
            backgroundMusicUrl = { selectedBackgroundMusic ->

                musicCustomizeDetails?.musicCategoryId = selectedBackgroundMusic?.cID
                musicCustomizeDetails?.backgroundMusicId = selectedBackgroundMusic?.id
                musicCustomizeDetails?.backgroundMusicImage = selectedBackgroundMusic?.thumbnail
                musicCustomizeDetails?.backgroundMusicTitle = selectedBackgroundMusic?.audioName
                musicCustomizeDetails?.backgroundMusicUrl = selectedBackgroundMusic?.audio
                isBackgroundMusicSelected()

                settingBackgroundMusicData()
                musicCategoriesBottomSheet?.dismiss()
            }
        }.also {
            it.show(
                childFragmentManager, null
            )
        }
    }

    private fun settingBackgroundMusicData() {
        binding.tvBackgroundMusicTitle.text = musicCustomizeDetails?.backgroundMusicTitle

        binding.ivBackgroundImage.glide(
            requestManager = requestManager,
            image = BuildConfig.MUSIC_BASE_URL.plus(musicCustomizeDetails?.backgroundMusicImage.orEmpty()),
            placeholder = R.drawable.ic_error_place_holder,
            error = R.drawable.ic_error_place_holder
        )
    }

    private fun isBackgroundMusicSelected() {
        binding.tvNoBackgroundMusicSelected.visibility(
            isVisible = musicCustomizeDetails?.backgroundMusicUrl.isNullOrEmpty()
        )
        binding.backgroundMusic.visibility(
            isVisible = musicCustomizeDetails?.backgroundMusicUrl.isNullOrEmpty().not()
        )
    }

    private fun musicCategoriesBottomSheet() {

        /** Closed previous opened bottom sheet if opened multiple at once */
        if (musicCategoriesBottomSheet?.dialog?.isShowing == true) {
            return
        }

        musicCategoriesBottomSheet = MusicCategoryBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.MUSIC_CATEGORIES_DATA to Gson().toJson(viewModel.musicCategoriesData),
                AppConstant.IS_MUSIC_CATEGORY to true,
                AppConstant.CUSTOMIZE to true
            ).also {
                AppConstant.REAL_CATEGORY.let {key->
                    if (this@CustomiseAffirmationFragment.arguments?.containsKey(key) == true){
                        it.putString(key,this@CustomiseAffirmationFragment.arguments?.getString(key))
                    }
                }
            }

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

    private fun selectedDuration() {
        if (binding.minutePickerMusic.value == 0 && binding.MinutePicker.value == 0 && binding.hourPicker.value == 0 && binding.hourPickerMusic.value == 0) {
            activity?.showToast("Please select valid time")
        } else {
            if (binding.defaultMusicMode.isChecked) {
                musicCustomizeDetails?.defaultMusicUrl = true
            }
            musicCustomizeDetails?.affirmationMinute = binding.MinutePicker.value
            musicCustomizeDetails?.affirmationHour = binding.hourPicker.value
            musicCustomizeDetails?.backgroundMusicMinute = binding.minutePickerMusic.value
            musicCustomizeDetails?.backgroundMusicHour = binding.hourPickerMusic.value
            musicCustomizeDetails?.isBackgroundMusicEnabled = binding.switchMusicEnabled.isChecked
            val totalTime =
                convertTimeToMilliseconds(binding.hourPicker.value, binding.MinutePicker.value)
            if ((musicCustomizeDetails?.affirmationDuration ?: 0) <= totalTime) {
                musicCustomizeDetails?.darkMode = binding.switchNightMode.isChecked
                onSubmitClick.invoke(
                    musicCustomizeDetails
                )
                dismiss()
                return
            }
        }

    }

    private fun gettingBundle() {
        arguments?.getString(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            this.categoryName = when (categoryName) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
                AppConstant.HomeCategory.MUSIC.name -> AppConstant.HomeCategory.MUSIC
                else -> null
            }
        }
        arguments?.getBoolean(AppConstant.PLAYER, false)?.let { isPlayer ->
            isFromPlayer = isPlayer
        }
        arguments?.getString(AppConstant.DU)?.let { duration ->
            du = duration
        }
        arguments?.getString(AppConstant.MUSIC_CUSTOMIZE_DETAIL)?.let { data ->
            musicCustomizeDetails = Gson().fromJson(data, MusicCustomizeDetails::class.java)
        }

        arguments?.getBoolean(AppConstant.CUSTOMIZE, false)?.let { customize ->
            isCustomizationEnabled = customize
        }

        lifecycleScope.launch(Dispatchers.Main) {
            binding.hourPicker.value = musicCustomizeDetails?.affirmationHour ?: 0
            binding.MinutePicker.value = musicCustomizeDetails?.affirmationMinute ?: 0
            binding.hourPickerMusic.value = musicCustomizeDetails?.backgroundMusicHour ?: 0
            binding.minutePickerMusic.value = musicCustomizeDetails?.backgroundMusicMinute ?: 0
            binding.switchNightMode.isChecked = musicCustomizeDetails?.darkMode ?: false
            binding.switchMusicEnabled.isChecked =
                musicCustomizeDetails?.isBackgroundMusicEnabled ?: false
            settingBackgroundMusicData()
            isBackgroundMusicSelected()
        }

        binding.tvTitle.text =
            when {
                (isFromPlayer) -> getString(R.string.customize_your_session)
                ((categoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                        categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) &&
                        !isFromPlayer) -> getString(
                    R.string.customize_session
                )

                else -> ""
            }

        binding.guidedAffirmation.visibility(
            isVisible = (categoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) && !isFromPlayer
        )
        binding.ivClose.visibility(
            isVisible = (categoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) && !isFromPlayer
        )
        binding.ivClose2.visibility(
            isVisible = (categoryName == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) && isFromPlayer
        )
        binding.btnSelectTime.visibility(
            isVisible = isFromPlayer
        )
        binding.btnStartSession.visibility(
            isVisible = isFromPlayer.not()
        )

        val backgroundCustomization = if (isFromPlayer && isCustomizationEnabled) {
            musicCustomizeDetails?.isBackgroundMusicEnabled == true
        } else true

        binding.customiseBackgroundMusic.visibility(
            isVisible = backgroundCustomization && categoryName != AppConstant.HomeCategory.MUSIC
        )
        if (categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) {
            binding.defaultMusicMode.gone()
            binding.tvDefaultMusic.gone()
        }

        if (categoryName == AppConstant.HomeCategory.MUSIC) {
            binding.tvCustomizeYourSession.setText(R.string.customise_music_length)
        }
    }
}