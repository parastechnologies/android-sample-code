package com.highenergymind.view.sheet

import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.databinding.ItemsSelectCategoryBinding


/**
 * Created by developer on 07/03/24
 */
class SelectCategorySheet : BaseBottomSheet<ItemsSelectCategoryBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.items_select_category
    }

    override fun init() {
        clicks()

    }

    private fun clicks() {
        mBinding.apply {
            nextBtn.setOnClickListener {
                dismiss()
            }
        }
    }
}