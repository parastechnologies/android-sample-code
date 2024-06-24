package com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.databinding.FragmentMusicCategoryBottomSheetBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicCategoryBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: MusicCateGoryViewModel by viewModels()
    private lateinit var binding: FragmentMusicCategoryBottomSheetBinding

    var backgroundMusicUrl: ((backgroundMusic: MusicList.Data?) -> Unit)? = null
    var categoryId: ((id: Int) -> Unit)? = null
    var closed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicCategoryBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sliderVolume.setCustomThumbDrawable(R.drawable.ic_music_volume_seekbar)
        binding.data = viewModel
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 3000

        gettingBundleData()
        settingRecyclerView()
        onClick()
    }

    private fun settingRecyclerView() {
        if (viewModel.isMusicCategory) {
            binding.rvMusicCategory.adapter = viewModel.categoryAdapter
            (binding.rvMusicCategory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            viewModel.categoryAdapter.submitList(viewModel.musicCategories?.data) {
                binding.tvNoDatFound.visibility(
                    isVisible = viewModel.categoryAdapter.currentList.isEmpty()
                )
            }
            viewModel.categoryAdapter.listener = { id ->
                this.categoryId?.invoke(id)
            }
            return
        }

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMusicSubCategory.adapter = viewModel.musicListAdapter
        binding.rvMusicSubCategory.layoutManager = layoutManager
        (binding.rvMusicSubCategory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        viewModel.musicListAdapter.submitList(viewModel.musicList?.data) {
            binding.tvNoDatFound.visibility(
                isVisible = viewModel.musicListAdapter.currentList.isEmpty()
            )
        }
        viewModel.musicListAdapter.selectedMusic = { selectedMusic ->
            viewModel.selectedMusic = selectedMusic

            /** Playing the music for testing */
            if (viewModel.musicPlayer == null) {
                viewModel.initializeMusicPlayer(
                    context = requireContext()
                )
            }
            viewModel.musicPlayer?.addBackgroundMedia(
                mediaUri = (BuildConfig.MUSIC_BASE_URL.plus(selectedMusic.audio)).toUri(),
                playWhenReady = true
            )
            viewModel.musicPlayer?.changeRepeatMode(
                isRepeat = true
            )
        }
    }

    private fun onClick() {
        binding.ivClose.setOnClickListener {
            closed?.invoke()
            dismiss()
        }

        binding.btnSaveChanges.setOnClickListener {
            if (viewModel.selectedMusic == null) {
                if (viewModel.musicListAdapter.currentList.isEmpty()) {
                    requireActivity().showToast(
                        message = getString(R.string.there_is_no_music_in_this_category_so_please_select_another_category_)
                    )
                    return@setOnClickListener
                }
                requireActivity().showToast(
                    message = getString(R.string.please_select_music)
                )
                return@setOnClickListener
            }
            backgroundMusicUrl?.invoke(viewModel.selectedMusic)
            dismiss()
        }
    }

    private fun gettingBundleData() {

        arguments?.getBoolean(AppConstant.IS_MUSIC_CATEGORY)?.let { isMusicCategory ->
            viewModel.isMusicCategory = isMusicCategory
        }


        arguments?.getString(AppConstant.MUSIC_CATEGORIES_DATA)?.let { data ->
            viewModel.musicCategories = Gson().fromJson(data, MusicCategories::class.java)
        }

        arguments?.getString(AppConstant.MUSIC_LIST_DATA)?.let { data ->
            viewModel.musicList = Gson().fromJson(data, MusicList::class.java)
        }



        binding.musicCategory.visibility(isVisible = viewModel.isMusicCategory)

        binding.musicSubCategory.visibility(
            isVisible = viewModel.isMusicCategory.not()
        )

        binding.tvTitle.setText(R.string.choose_music)

        binding.apply {
            if (arguments?.containsKey(AppConstant.CUSTOMIZE) == true && viewModel.isMusicCategory) {
                binding.groupNote.gone()
            }
            when (arguments?.getString(AppConstant.REAL_CATEGORY)) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> {
                    if (arguments?.containsKey(AppConstant.CUSTOMIZE) == true && !viewModel.isMusicCategory) {
                        tvNote.text =
                            getString(R.string.guided_affirmation_background_category_desc)

                        setSpan()
                    }
                    else if (viewModel.isMusicCategory) {
                        groupNote.gone()
                    }else {
                        tvNote.text =
                            getString(R.string.guided_affirmation_background_music_plus_category_desc)
                        setSpan()
                    }
                }

                AppConstant.HomeCategory.SLEEP_AFFIRMATION.name -> {
                    if (arguments?.containsKey(AppConstant.CUSTOMIZE) == true && !viewModel.isMusicCategory) {
                        tvNote.text = getString(R.string.guided_sleep_background_category_desc)
                        setSpan()
                    } else {
                        tvNote.text =
                            getString(R.string.guided_sleep_background_music_plus_category_desc)
                        setSpan()
                    }
                }

                AppConstant.HomeCategory.SLEEP_MEDIATION.name -> {
                    if (viewModel.isMusicCategory) {
                        groupNote.gone()
                    } else {
                        tvNote.text =
                            getString(R.string.guided_sleep_meditation_background_category_desc)
                        setSpan()
                    }

                }

                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> {
                    if (arguments?.containsKey(AppConstant.CUSTOMIZE) == true && !viewModel.isMusicCategory) {
                        tvNote.text =
                            getString(R.string.create_affirmation_background_category_desc)
                        setSpan()
                    } else if (viewModel.isMusicCategory) {
                        groupNote.gone()
                    } else {
                        tvNote.text =
                            getString(R.string.create_affirmation_background_music_plus_category_desc)
                        setSpan()
                    }
                }

                AppConstant.MUSIC_MEDITATION -> {
                    tvNote.text =
                        getString(R.string.music_meditate_note_desc)
                    setSpan()
                }

                else -> {
                    groupNote.gone()
                }
            }

        }
    }

    private fun setSpan() {
        binding.apply {

             val text = tvNote.text.toString()
            if (text.contains("filIc")) {
                val spanString = SpannableStringBuilder(text)
                val drawable =  ContextCompat.getDrawable(requireContext(), R.drawable.ic_filter_im)
                drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val imageSpan = ImageSpan(
                    drawable!!,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ImageSpan.ALIGN_CENTER else ImageSpan.ALIGN_BOTTOM
                )
                spanString.setSpan(
                    imageSpan,
                    text.indexOf("filIc"),
                    text.indexOf("filIc") + 5,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvNote.text = spanString
            }

        }
    }
}

