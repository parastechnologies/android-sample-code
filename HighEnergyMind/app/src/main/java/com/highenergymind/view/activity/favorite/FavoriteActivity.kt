package com.highenergymind.view.activity.favorite

import com.google.android.material.tabs.TabLayoutMediator
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityFavoriteBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.adapter.FavStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : BaseActivity<ActivityFavoriteBinding>() {
    private val pagerAdapter by lazy {
        FavStateAdapter(supportFragmentManager, lifecycle)
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_favorite
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolBar()
        getBundleData()
        setAdapter()
    }

    private fun getBundleData() {
        if (intent.hasExtra(getString(R.string.name))) {
            binding.customTool.tvTitle.text = intent.getStringExtra(getString(R.string.name))
        }
    }

    private fun setToolBar() {
        binding.customTool.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.favorites)
        }
    }

    private fun setAdapter() {
        binding.apply {
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager
            ) { tab, pos ->
                when (pos) {
                    0 -> {
                        tab.text = getString(R.string.tracks_tab)
                    }

                    1 -> {
                        tab.text = getString(R.string.affirmations_tab)
                    }
                }
            }.attach()
        }
    }

}