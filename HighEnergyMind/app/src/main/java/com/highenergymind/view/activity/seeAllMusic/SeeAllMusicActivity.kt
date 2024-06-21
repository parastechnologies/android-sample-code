package com.highenergymind.view.activity.seeAllMusic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivitySeeAllMusicBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.musicdetails.MusicDetailsActivity
import com.highenergymind.view.activity.unlockFeature.UnlockFeatureActivity
import com.highenergymind.view.adapter.SeeAllMusicAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeeAllMusicActivity : BaseActivity<ActivitySeeAllMusicBinding>() {
    val viewModel by viewModels<SeeAllMusicViewModel>()
    val adapter by lazy {
        SeeAllMusicAdapter(viewModel.sharedPrefs)
    }
    private var isMusicDetailShow: Boolean = false

    private var catId: String? = null

    override fun getLayoutRes(): Int {
        return R.layout.activity_see_all_music
    }

    override fun initView() {
        fullScreenStatusBar()
        iniToolbar()
        setCollectors()
        setViews()
        getBundleData()
        clicks()
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                adapter.notifyDataSetChanged()
            }
        }

    private fun setViews() {
        binding.apply {
            rvTrack.adapter = adapter
            adapter.callBack = { item, _, isPremium ->
                if (isPremium) {
                    val intent = Intent(this@SeeAllMusicActivity, UnlockFeatureActivity::class.java)
                    intent.putExtras(Bundle().also { bnd ->
                        bnd.putInt(AppConstant.SCREEN_FROM, R.id.musicCV)
                    })
                    activityResult.launch(intent)
                } else {
                    if (isMusicDetailShow) {
                        intentComponent(
                            MusicDetailsActivity::class.java,
                            Bundle().also { bnd ->
                                bnd.putString(AppConstant.MUSIC_DATA, Gson().toJson(item))

                            })
                    } else {
                        val intent = Intent()
                        intent.putExtra(AppConstant.MUSIC_DATA, Gson().toJson(item))
                        setResult(Activity.RESULT_OK, intent)
                    }
                }
            }
        }
    }

    private fun clicks() {
        binding.apply {
            etSearch.addTextChangedListener {
                callApi()
            }
            swipeToRefresh.setOnRefreshListener {
                callApi()
            }
        }

    }

    private fun setCollectors() {
        viewModel.apply {
            adapter.addLoadStateListener {

                when (it.refresh) {
                    is LoadState.Loading -> {
                        binding.swipeToRefresh.isRefreshing = true
                    }

                    is LoadState.NotLoading -> {
                        binding.swipeToRefresh.isRefreshing = false
                    }

                    is LoadState.Error -> {
                        binding.swipeToRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun getBundleData() {

        if (intent.hasExtra(ApiConstant.CATEGORY_ID)) {
            isMusicDetailShow = intent.getBooleanExtra(ApiConstant.IS_MUSIC_DETAIL_SHOW, false)
            catId = intent.getStringExtra(ApiConstant.CATEGORY_ID) ?: ""
            callApi()
        }
    }

    private fun callApi() {
        lifecycleScope.launch {

            catId?.let { id ->
                viewModel.getSeeAllMusicLib(id, binding.etSearch.text?.trim()?.toString() ?: "")
                    .collectLatest {
                        adapter.submitData(it)
                    }
            }
        }
    }

    private fun iniToolbar() {
        binding.customTool.apply {
            tvTitle.text = intent.getStringExtra(ApiConstant.CATEGORY)
            ivBack.setOnClickListener {
                finish()
            }
        }
    }

}