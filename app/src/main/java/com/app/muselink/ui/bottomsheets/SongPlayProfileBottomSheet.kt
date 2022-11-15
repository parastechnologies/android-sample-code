package com.app.muselink.ui.bottomsheets

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.muselink.R
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.adapter.profile.AdapterVsualizers
import com.app.muselink.util.WrappingViewPager
import kotlinx.android.synthetic.main.song_play_profile_bottom_sheet.*

class SongPlayProfileBottomSheet(val modalAudioFile: ModalAudioFile) : BottomSheetDialogFragment() {

    private var adapterVisualizers: AdapterVsualizers? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
    }


    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View?>(bottomSheet!!)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        layoutParams.height = windowHeight
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }
    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.song_play_profile_bottom_sheet, container, false)
        val viewPagerAudioPlay = contentView.findViewById<WrappingViewPager>(R.id.viewPagerAudioPlay)
        val imgClose = contentView.findViewById<ImageView>(R.id.imgClose)

        imgClose.setOnClickListener {
            dismiss()
        }
        adapterVisualizers = AdapterVsualizers(childFragmentManager,modalAudioFile)
        viewPagerAudioPlay.adapter = adapterVisualizers
        viewPagerAudioPlay.offscreenPageLimit = 3
        viewPagerAudioPlay.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(pos: Int) {
            }
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        selectedImage(image1, image2, image3)
                        adapterVisualizers?.getVisualizerFragment()?.onStop()
                    }
                    1 -> {
                        selectedImage(image2, image1, image3)
                        adapterVisualizers?.getAudioVisualizerFragment()?.onStop()
                    }
                    2 -> {
                        adapterVisualizers?.getVisualizerFragment()?.onStop()
                        adapterVisualizers?.getAudioVisualizerFragment()?.onStop()
                        selectedImage(image3, image1, image2)
                    }
                }
            }

        })

        Handler(Looper.getMainLooper()).postDelayed({
            val listAudioFile = ArrayList<ModalAudioFile>()
            listAudioFile.add(modalAudioFile)
            SingletonInstances.setUploadAudioFiles(listAudioFile)
            adapterVisualizers?.getVisualizerFragment()?.intializePlayer()
            adapterVisualizers?.getAudioVisualizerFragment()?.intializePlayer()
        }, 1000)


        return contentView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        cardShareTo.setOnClickListener {
            ShareToBottomsheet(requireActivity()).show(childFragmentManager, "ShareToSheet")

        }

    }
    private fun selectedImage(
        selectedImage: ImageView?,
        unselected1: ImageView?,
        unselected2: ImageView?) {
        selectedImage?.setBackgroundResource(R.drawable.drawable_circle_red)
        unselected1?.setBackgroundResource(android.R.color.transparent)
        unselected2?.setBackgroundResource(android.R.color.transparent)

    }
}
