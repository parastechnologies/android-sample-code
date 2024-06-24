package com.in2bliss.ui.activity.home.updateAffirmation

import com.google.android.material.tabs.TabLayout
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityEditAffirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAffirmationActivity : BaseActivity<ActivityEditAffirmationBinding>(
    layout = R.layout.activity_edit_affirmation
)  {
    override fun init() {
        setupTabLayout()
        setupViewPager()
        onClick()
    }

    private fun onClick(){
        binding.apply {
            binding.toolBar.tvTitle.text = getString(R.string.edit_affirmation)
            binding.toolBar.ivBack.setOnClickListener{finish()}
        }
    }

    private fun setupTabLayout() {
        binding.tlEditAffirmationTab.apply {
            addTab(this.newTab().setText(R.string.details))
            addTab(this.newTab().setText(R.string.audio))
            addTab(this.newTab().setText(R.string.category))
            addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let {
                        binding.viewPagerEditAffirmation.currentItem = it
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun setupViewPager() {
        binding.viewPagerEditAffirmation.apply {
            adapter = EditAffirmationViewPagerAdapter(
                supportFragmentManager,
                binding.tlEditAffirmationTab.tabCount
            )
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tlEditAffirmationTab))
        }
    }
}