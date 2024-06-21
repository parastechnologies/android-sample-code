package com.highenergymind.view.sheet.trackmusicsheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.BackAudios
import com.highenergymind.data.GetMusicResponse
import com.highenergymind.databinding.SheetMusicLayoutBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.musicdetails.MusicDetailsActivity
import com.highenergymind.view.activity.seeAllMusic.SeeAllMusicActivity
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity
import com.highenergymind.view.adapter.MusicOuterAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TrackMusicSheet : BaseBottomSheet<SheetMusicLayoutBinding>() {
    private val seeAllResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { inte ->
                    val model = Gson().fromJson(
                        inte.getStringExtra(AppConstant.MUSIC_DATA),
                        BackAudios::class.java
                    )
                    redirectScreen(model)

                }
            }
        }


    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                musicAdapter.notifyDataSetChanged()
            }
        }
    val viewModel by viewModels<TrackMusicViewModel>()
    var isMusicDetailShow: Boolean = false
    var callBack: ((BackAudios) -> Unit)? = null

    private val musicAdapter: MusicOuterAdapter by lazy {
        MusicOuterAdapter(viewModel.sharedPrefs).also {
            it.callBack = { outerItem, outerPos, innerItem, innerPos, type, isPremium ->
                if (isPremium) {
                    val intent = Intent(requireContext(), UnlockFeatureActivity::class.java)
                    intent.putExtras(Bundle().also { bnd ->
                        bnd.putInt(AppConstant.SCREEN_FROM, R.id.musicCV)
                    })
                    activityResult.launch(intent)

                } else {
                    when (type) {
                        ApiEndPoint.SEE_ALL_MUSIC_LIBRARY -> {
                            val intent = Intent(requireContext(), SeeAllMusicActivity::class.java)
                            intent.putExtras(Bundle().also { bnd ->
                                bnd.putString(
                                    ApiConstant.CATEGORY,
                                    outerItem.backgroundCategoryName
                                )
                                bnd.putString(ApiConstant.CATEGORY_ID, outerItem.id.toString())
                                bnd.putBoolean(ApiConstant.IS_MUSIC_DETAIL_SHOW, isMusicDetailShow)
                            })
                            seeAllResult.launch(intent)
                        }

                        else -> {
                            redirectScreen(innerItem)
                        }
                    }
                }

            }
        }
    }

    private fun redirectScreen(innerItem: BackAudios?) {

        if (isMusicDetailShow) {
            requireContext().intentComponent(
                MusicDetailsActivity::class.java,
                Bundle().also { bnd ->
                    bnd.putString(AppConstant.MUSIC_DATA, Gson().toJson(innerItem))

                })
        } else {
            callBack?.invoke(innerItem!!)
            dismiss()
        }
    }

    override fun getLayoutRes(): Int {
        return com.highenergymind.R.layout.sheet_music_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialo = dialog as BottomSheetDialog
        dialo.behavior.peekHeight = 5000
    }


    override fun init() {
        setUpRecyclerView()
        setCollectors()
        clicks()
        getMusic()
    }


    private fun clicks() {
        mBinding.swipeToRefresh.setOnRefreshListener {
            getMusic()
        }
        mBinding.crossIV.setOnClickListener {
            dialog!!.dismiss()
        }
        mBinding.apply {
            etSearch.addTextChangedListener {
                getMusic()

            }
        }
    }

    private fun getMusic() {
        viewModel.apply {

            map.clear()
            map[ApiConstant.KEYWORD] = mBinding.etSearch.text?.trim()?.toString() ?: ""
            getMusicApi()
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    mBinding.swipeToRefresh.isRefreshing = it
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                musicResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as GetMusicResponse
                        musicAdapter.submitList(response.data)

                    })
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        mBinding.apply {
            rvMusic.adapter = musicAdapter
        }

    }

}