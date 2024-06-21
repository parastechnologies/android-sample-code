package com.highenergymind.view.activity.newCategory

import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityNewCategoryBinding
import com.highenergymind.utils.fullScreenStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCategoryActivity : BaseActivity<ActivityNewCategoryBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_new_category
    }

    override fun initView() {
        fullScreenStatusBar()
        clicks()
    }

    private fun clicks() {
        binding.apply {
            customTool.backIV.setOnClickListener {
                finish()
            }

        }
    }


}