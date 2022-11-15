package com.app.muselink.ui.fragments.home.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.databinding.FragmentDashboardBinding
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.retrofit.Resource
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.adapter.DashboardSliderAdapter
import com.app.muselink.ui.base.fragment.BaseViewModelFragment
import com.app.muselink.ui.bottomsheets.BottomSheetDirectMessage
import com.app.muselink.ui.bottomsheets.reportaudio.BottomSheetReport
import com.app.muselink.ui.bottomsheets.BottomSheetViewallComments
import com.app.muselink.ui.bottomsheets.musiclinkpro.MuseLinkProBottomsheet
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.ui.fragments.home.dashboard.DashboardViewModel.Companion.DM_COUNT
import com.app.muselink.ui.fragments.home.dashboard.DashboardViewModel.Companion.UPLOADED_AUDIO_COUNT
import com.app.muselink.ui.fragments.home.soundfile.SoundFileViewPagerFragment
import com.app.muselink.ui.fragments.home.viewpager.SoundFileFragment
import com.app.muselink.ui.fragments.home.viewpager.SoundFileProFragment
import com.app.muselink.ui.fragments.home.viewpager.comments.CommentsFragment
import com.app.muselink.util.showToast
import com.app.muselink.widgets.GradientProgressBar
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.layout_share.*
import soup.neumorphism.ShapeType
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DashBoardFragment : BaseViewModelFragment<FragmentDashboardBinding, DashboardViewModel>() {

    private var adapterDashBoard: DashboardSliderAdapter? = null

    private var progressView: GradientProgressBar? = null

    /**
     * [DashboardViewModel]
     * */
    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    /**
     * [getLayout]
     * */
    override fun getLayout(): Int {
        return R.layout.fragment_dashboard
    }

    /**
     * Show description view
     * */
    fun showHideViewForDesc(IsVisible: Boolean) {
        if (IsVisible) {
            viewpagerDots.visibility = View.VISIBLE
            llViewPlayerController.visibility = View.VISIBLE
            rlViewPlayerController.visibility = View.VISIBLE
        } else {
            viewpagerDots.visibility = View.GONE
            llViewPlayerController.visibility = View.GONE
            rlViewPlayerController.visibility = View.GONE
        }
    }

    /**
     * Favourite Audio
     * */
    fun updateFavButton() {
        val modalAudio = SingletonInstances.currentAudioFilePlay
        if (modalAudio != null) {
            if (modalAudio.favoriteAudio == 1) {
                npmStar?.setShapeType(ShapeType.PRESSED)
            } else {
                npmStar?.setShapeType(ShapeType.FLAT)
            }
        }
    }

    /**
     * [onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.lifecycleOwner = this
        viewModel?.setObserverFavUser()
        binding?.vm = viewModel
        viewModel?.binding = binding
        progressView = view.findViewById(R.id.progressView)
        setUpObserver()
        setListeners()
    }

    var currentFragment: Fragment? = null

    private fun setViewpager() {
        viewPagerHome.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(pos: Int) {
                currentFragment = adapterDashBoard?.getCurrentFragment()
                if (pos == 0) {
                    showHideViewForDesc(true)
                    adapterDashBoard?.getSoundFileFragment()?.onStop()
                } else if (pos == 1) {
                    if (currentFragment is SoundFileFragment) {
                        (currentFragment as SoundFileFragment).setToDefaultSettings()
                    }
                    adapterDashBoard?.getSoundFileProFragment()?.onStop()
                }
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        selectedImage(image1, image2, image3)
                        val fragment = adapterDashBoard?.getSoundFileProFragment()
                        if (fragment is SoundFileProFragment) {
                            if (ExoPlayerAudio.previousSongPlayPos != ExoPlayerAudio.currentSongPlayPos) {
                                fragment.intilizeChnagedSong()
                            }
                        }
                        updateFavButton()
                    }
                    1 -> {
                        selectedImage(image2, image1, image3)
                        val fragment = adapterDashBoard?.getSoundFileViewPagerFragment()
                        if (fragment is SoundFileViewPagerFragment) {
                            if (ExoPlayerAudio.previousSongPlayPos != ExoPlayerAudio.currentSongPlayPos) {
                                fragment.gotoParticularSelectedSong()
                            }
                        }
                    }
                    2 -> {
                        selectedImage(image3, image2, image1)
                        val fragment = adapterDashBoard?.getCommentsFragment()
                        if (fragment is CommentsFragment) {
                            fragment.setToDefaultSettings()
                        }
                    }
                }
            }
        })
        adapterDashBoard = DashboardSliderAdapter(childFragmentManager, this)
        viewPagerHome.adapter = adapterDashBoard
        viewPagerHome.offscreenPageLimit = 3
        viewPagerHome.currentItem = 1
    }
    /**
     * Response Observer
     * */
    private fun setUpObserver() {
        viewModel?.subscriptionResponse?.observe(requireActivity()) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data?.subscriptionStatus != null) {
                                SharedPrefs.save(
                                    AppConstants.PREFS_SUBSCRIPTION_STATUS,
                                    it.data.data.subscriptionStatus!!
                                )
                            }
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.data?.message)
                    }
                }
                Resource.Status.ERROR -> {}
                Resource.Status.LOADING -> {}
            }
        }

        viewModel?.checkUploadLimitResponse?.observe(requireActivity()) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    UPLOADED_AUDIO_COUNT = if (it.data?.status != "200") {
                        5
                    } else {
                        it.data.data
                    }
                }
                Resource.Status.ERROR -> {}
                Resource.Status.LOADING -> {}
            }
        }
        viewModel?.uploadedAudioResponse?.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            SharedPrefs.save(AppConstants.PREFS_SUBSCRIPTION_STATUS,it.data.subscriptionStatus)
                            if (it.data.data.isNullOrEmpty().not()) {
                                DM_COUNT = it.data.DMCount
                                val arrayList: ArrayList<ModalAudioFile> = ArrayList()
                                arrayList.addAll(it.data.data!!)
                                arrayList.reverse()
                                SingletonInstances.setUploadAudioFiles(arrayList)
                                setViewpager()
                                adapterDashBoard?.getSoundFileProFragment()?.intializePlayer()
                            } else {
                                setViewpager()
                            }
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.data?.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(requireActivity(), it.data?.message)
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        }
    }

    /**
     * On Click Listener
     * */
    private fun setListeners() {
        cardDM.setOnClickListener {
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            } else {
                if(SharedPrefs.isUserLogin()) {
                    if (SharedPrefs.subscriptionStatus()) {
                        val bundle = bundleOf(
                            AppConstants.MATCH_SCREEN to "",
                            AppConstants.receiverId to SingletonInstances.currentAudioFilePlay?.userId,
                            AppConstants.receiverName to SingletonInstances.currentAudioFilePlay?.userName
                        )
                        val bottomSheetDirectMessageFragment = BottomSheetDirectMessage()
                        bottomSheetDirectMessageFragment.arguments = bundle
                        bottomSheetDirectMessageFragment.show(
                            childFragmentManager,
                            "BottomSheetDirectMessage"
                        )
                    } else {
                        MuseLinkProBottomsheet(requireActivity()).show(
                            childFragmentManager,
                            "MuseLinkProBottomSheet"
                        )
                    }
                }else{
                    val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                    signUpTypesBottomSheet.show(requireActivity().supportFragmentManager, "AuthDialog")
                    SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
                }
            }

        }
        npmShareTo.setOnClickListener {
            if(SharedPrefs.isUserLogin()) {
                showHideViewClickShare(true)
            }else{
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(requireActivity().supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            }
        }
        closeShare.setOnClickListener {
            showHideViewClickShare(false)
        }
        cardRefresh.setOnClickListener {
            if(SharedPrefs.isUserLogin()) {
                if (SingletonInstances.listAudioFiles.isNullOrEmpty().not()) {
                    when (val currentFragment = adapterDashBoard?.getCurrentFragment()) {
                        is SoundFileProFragment -> {
                            currentFragment.playPreviousSong()
                        }
                        is SoundFileViewPagerFragment -> {
                            currentFragment.playPreviousSong()
                        }
                    }
                } else {
                    showToast(requireActivity(), getString(R.string.no_audios_found))
                }
            }else{
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(requireActivity().supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            }
        }
        npmNextSong?.setOnClickListener {
            if (SingletonInstances.listAudioFiles.isNullOrEmpty().not()) {
                when (val currentFragment = adapterDashBoard?.getCurrentFragment()) {
                    is SoundFileProFragment -> {
                        currentFragment.playNextSong()
                    }
                    is SoundFileFragment -> {
                        currentFragment.playNextSong()
                    }
                    is SoundFileViewPagerFragment -> {
                        currentFragment.playNextSong()
                    }
                }
            } else {
                showToast(requireActivity(), getString(R.string.no_audios_found))
            }
        }

        btnReport?.setOnClickListener {
            btnReport?.setStrokeColor(
                ContextCompat.getColorStateList(
                    requireActivity(),
                    R.color.colorRed
                )
            )
            btnComment?.setStrokeColor(
                ContextCompat.getColorStateList(
                    requireActivity(),
                    R.color.color_black_100
                )
            )
            val bottomSheetReport =
                BottomSheetReport(SingletonInstances.currentAudioFilePlay?.audioId.toString())
            bottomSheetReport.show(
                childFragmentManager,
                "BottomSheetReport"
            )
            showHideViewClickShare(false)
        }

        btnComment?.setOnClickListener {
            showHideViewClickShare(false)
            btnComment?.setStrokeColor(ContextCompat.getColorStateList(requireActivity(), R.color.colorRed))
            btnReport?.setStrokeColor(ContextCompat.getColorStateList(requireActivity(), R.color.color_black_100))
            val modalAudioFile = ModalAudioFile()
            modalAudioFile.audioId = SingletonInstances.currentAudioFilePlay?.audioId
            BottomSheetViewallComments(requireActivity(), modalAudioFile).show(childFragmentManager, "BottomSheetViewAllComments")
        }
    }
    /**
     * Selected view pager indicator
     * */
    private fun selectedImage(
        selectedImage: ImageView?,
        unselected1: ImageView?,
        unselected2: ImageView?) {
        selectedImage?.setBackgroundResource(R.drawable.drawable_circle_purple)
        unselected1?.setBackgroundResource(android.R.color.transparent)
        unselected2?.setBackgroundResource(android.R.color.transparent)
    }
    /**
     * [BroadcastReceiver] To show progress bar for Upload songs
     * */
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val percentage: Int = intent.getIntExtra("percentage", 0)
            val showProgress: Boolean = intent.getBooleanExtra("showProgress", false)
            if (!showProgress) {
                progressView?.visibility = View.GONE
            } else {
                progressView?.visibility = View.VISIBLE
                progressView?.progress = percentage.toFloat()
            }
        }
    }
    /**
     * onResume
     * */
    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-event-name")
        )
        super.onResume()
    }
    /**
     * [onPause]
     * */
    override fun onPause() {
        super.onPause()
        showHideViewClickShare(false)
        try {
            LocalBroadcastManager.getInstance(requireActivity())
                .unregisterReceiver(mMessageReceiver)
        } catch (e: Exception) {

        }
    }
    /**
     * On Click show share button
     * */
    private fun showHideViewClickShare(IsVisible: Boolean) {
        if (IsVisible) {
            val bottomDown: Animation = AnimationUtils.loadAnimation(context, R.anim.bottom_down)
            val hiddenPanel = llViewPlayerController as ViewGroup
            hiddenPanel.startAnimation(bottomDown)
            hiddenPanel.visibility = View.GONE
            bottomDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
            val bottomUp: Animation = AnimationUtils.loadAnimation(context, R.anim.bottom_up)
            val hiddenPanel1 = llShareToLay as ViewGroup
            hiddenPanel1.startAnimation(bottomUp)
            hiddenPanel1.visibility = View.VISIBLE
            bottomUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
        } else {
            val bottomDown: Animation = AnimationUtils.loadAnimation(context, R.anim.bottom_down)
            val hiddenPanel = llShareToLay as ViewGroup
            hiddenPanel.startAnimation(bottomDown)
            hiddenPanel.visibility = View.GONE
            bottomDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
            val bottomUp: Animation = AnimationUtils.loadAnimation(context, R.anim.bottom_up)
            val hiddenPanel1 = llViewPlayerController as ViewGroup
            hiddenPanel1.startAnimation(bottomUp)
            hiddenPanel1.visibility = View.VISIBLE
            bottomUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel?.onDispose()
    }
}