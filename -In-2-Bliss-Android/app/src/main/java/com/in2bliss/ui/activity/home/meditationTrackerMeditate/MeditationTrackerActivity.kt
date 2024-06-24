package com.in2bliss.ui.activity.home.meditationTrackerMeditate

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityMeditationTrackerBinding
import com.in2bliss.ui.activity.home.fragment.meditationTracker.dashboard.DashboardFragment
import com.in2bliss.ui.activity.home.fragment.meditationTracker.history.HistoryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeditationTrackerActivity : BaseActivity<ActivityMeditationTrackerBinding>(
    layout = R.layout.activity_meditation_tracker
) {

    override fun init() {
        setupViewPager()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            binding.toolBar.tvTitle.text = getString(R.string.meditation_tracker)
            binding.toolBar.ivBack.setOnClickListener { finish() }
        }
    }

    private fun setupViewPager() {
        val fragments = arrayListOf<Fragment>(DashboardFragment(), HistoryFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPagerMeditation.adapter = adapter
        TabLayoutMediator(binding.tlMeditationTab, binding.viewPagerMeditation) { tab, position ->
            if (position == 0) tab.text = getString(R.string.dashboard) else tab.text =
                getString(R.string.history)
        }.attach()
    }
}

