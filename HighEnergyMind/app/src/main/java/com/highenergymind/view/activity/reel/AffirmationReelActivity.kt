package com.highenergymind.view.activity.reel

import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityAffirmationReelBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.adapter.AffirmationFullAdapter
import com.highenergymind.view.sheet.empoweringAffirmationCategory.EmpoweringAffirmCategorySheet
import com.highenergymind.view.sheet.theme.SheetThemeBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffirmationReelActivity : BaseActivity<ActivityAffirmationReelBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_affirmation_reel
    }

    override fun initView() {
        fullScreenStatusBar()
        clicks()

    }

    private fun clicks() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
        }
    }




}