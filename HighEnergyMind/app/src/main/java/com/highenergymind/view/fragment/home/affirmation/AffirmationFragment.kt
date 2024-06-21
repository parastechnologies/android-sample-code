package com.highenergymind.view.fragment.home.affirmation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.SpannableStringBuilder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.Affirmation
import com.highenergymind.data.BackgroundThemeResponse
import com.highenergymind.data.CategoriesData
import com.highenergymind.data.ScrollAffirmationResponse
import com.highenergymind.data.ThemeData
import com.highenergymind.databinding.FragmentAffirmationBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.OnSwipeTouchListener
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.checkCameraPermission
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.ScreenShotActivity
import com.highenergymind.view.adapter.AffirmationFullAdapter
import com.highenergymind.view.sheet.empoweringAffirmationCategory.EmpoweringAffirmCategorySheet
import com.highenergymind.view.sheet.theme.SheetThemeBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class AffirmationFragment : BaseFragment<FragmentAffirmationBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var themeDataList: List<ThemeData>? = null
    val viewModel by viewModels<AffirmationViewModel>()
    val adapter by lazy {
        AffirmationFullAdapter()
    }
    private var filterCategory: List<CategoriesData>? = null
    private var selectedTheme: String? = null


    override fun getLayoutRes(): Int {
        return R.layout.fragment_affirmation
    }

    override fun initViewWithData() {
        clicks()
        setAdapter()
        setCollectors()

        viewModel.getAffirmationScrollApi()
    }

    private fun setCollectors() {

        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                affirmationScrollResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as ScrollAffirmationResponse
                        mBinding.apply {
                            selectedTheme = response.data.backgroundAffirmationImg
                            ivBackground.glideImage(response.data.backgroundAffirmationImg)
                            adapter.submitList(response.data.affirmationList)
                            checkFirstSeen()
                            if (adapter.itemCount > 0) cvBottomBar.visible() else cvBottomBar.gone()
                        }
                    })
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                themeResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as BackgroundThemeResponse
                        themeDataList = response.data
                        mBinding.themeBackgroundIV.performClick()
                    })
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    mBinding.swipeToRefresh.isRefreshing = it
                }
            }
        }

    }

    private fun checkFirstSeen() {
        mBinding.apply {
            if (!sharedPrefs.getBoolean(AppConstant.IS_SEEN)) {
                layoutSwipeUp.root.visible()
            } else {
                layoutSwipeUp.root.gone()

            }
        }
    }

    private fun clicks() {
        mBinding.apply {
            layoutSwipeUp.root.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
                override fun onSwipeTop() {
                    super.onSwipeTop()
                    layoutSwipeUp.root.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.swipe_up
                        )
                    )
                    layoutSwipeUp.root.gone()
                    sharedPrefs.save(AppConstant.IS_SEEN, true)
                }
            })

            swipeToRefresh.setOnRefreshListener {
                viewModel.getAffirmationScrollApi()
            }
            ivFav.setOnClickListener {
                adapter.currentList[viewPager.currentItem]?.let { item ->
                    item.isFavourite = if (item.isFavourite == 1) 0 else 1
                    ivFav.setImageResource(if (item.isFavourite == 1) R.drawable.ic_fill_heart else R.drawable.ic_un_fill_heart)
                    markFavApi(item)
                }
            }
            ivShare.setOnClickListener {
                if (requireActivity().checkCameraPermission()) {
                    takeScreenShot(adapter.currentList.get(viewPager.currentItem))
                } else {
                    requestPermissionCam.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            cameraPermissionHigherArray
                        } else {
                            cameraPermissionLowerArray
                        }
                    )
                }
            }
            llCategory.setOnClickListener {
                EmpoweringAffirmCategorySheet().also {
                    it.filterCategory = filterCategory
                    it.callBack = { list ->
                        filterCategory = list
                        filterAffirmation(list)
                    }
                }.show(childFragmentManager, "")
            }
            themeBackgroundIV.setOnClickListener {
                if (themeDataList.isNullOrEmpty()) {
                    viewModel.getThemeApi()
                } else {
                    SheetThemeBackground(themeDataList!!).also {
                        it.callBack = { img ->
                            viewModel.apply {
                                map.clear()
                                map[ApiConstant.IMG] = img.onlyImage
                                changeBackgroundImageApi()
                            }
                            ivBackground.glideImage(img.backgroundThemeImg)
                            selectedTheme = img.backgroundThemeImg

                        }
                    }.show(childFragmentManager, "")
                }
            }
        }
    }

    private fun takeScreenShot(aff: Affirmation?) {
        mBinding.apply {


            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)?.path.let {
                    aff?.createdAt = selectedTheme.toString()
                    val intent = Intent(requireContext(), ScreenShotActivity::class.java)
                    intent.putExtra(AppConstant.TRACK_DATA, Gson().toJson(aff))
                    activityResult.launch(intent)
            }
        }

    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val img = it.data?.getStringExtra(ApiConstant.IMG)
                img?.let { it1 -> shareImageIntent(it1) }
            }
        }

    private fun shareImageIntent(s: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.setType("image/jpeg")
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(s))
        startActivity(Intent.createChooser(share, "Share Affirmation"))
    }


    private fun filterAffirmation(checkedCategory: List<CategoriesData>) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.CATEGORY] =
                checkedCategory.map { it.id }.joinToString(",")
            val subCategorySpan = SpannableStringBuilder()
            /** getting comma separated subCategoryId's using below method**/
            checkedCategory.filter { !it.subCategoryList.isNullOrEmpty() }
                .forEach { categ ->
                    categ.subCategoryList?.forEach { sub ->
                        subCategorySpan.append(sub.id.toString() + ",")
                    }
                }
            /** removing extra comma from the last**/
            map[ApiConstant.SUB_CATEGORY] =
                subCategorySpan.removeSuffix(",").toString()
            viewModel.getAffirmationScrollApi()
        }
    }

    private fun setAdapter() {

        mBinding.apply {
            viewPager.adapter = adapter
            adapter.callBack = { _: Affirmation?, _: Int, type: Int ->

            }
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mBinding.apply {
                        adapter.currentList[position]?.let { item ->
                            tvCategory.text = item.categoryName
                            val res =
                                if (item.isFavourite == 1) R.drawable.ic_fill_heart else R.drawable.ic_un_fill_heart
                            ivFav.setImageResource(res)
                        }
                    }
                }
            })
        }
    }

    private fun markFavApi(item: Affirmation) {
        viewModel.apply {
            map.clear()
            map[ApiConstant.ID] = item.id
            map[ApiConstant.FAVOURITE] = item.isFavourite
            map[ApiConstant.TYPE] = AppConstant.TYPE_AFFIRMATION
            markFav()
        }
    }


    private val cameraPermissionLowerArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val cameraPermissionHigherArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )

        private val requestPermissionCam =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (it[Manifest.permission.CAMERA] == true && it[Manifest.permission.READ_MEDIA_IMAGES] == true && it[Manifest.permission.READ_MEDIA_VIDEO] == true) {
                        mBinding.ivShare.performClick()
                    }
                } else {
                    if (it[Manifest.permission.CAMERA] == true && it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true && it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                        mBinding.ivShare.performClick()
                    }
                }
            }
}