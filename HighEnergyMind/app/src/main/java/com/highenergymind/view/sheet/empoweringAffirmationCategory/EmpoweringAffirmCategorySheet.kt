package com.highenergymind.view.sheet.empoweringAffirmationCategory

import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.CategoriesData
import com.highenergymind.databinding.SheetEmpoweringAffirmCategoryBinding
import com.highenergymind.view.fragment.empoweraffirmation.CategoriesFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by developer on 29/02/24
 */
@AndroidEntryPoint
class EmpoweringAffirmCategorySheet(var filterCategory: List<CategoriesData>? = null) :
    BaseBottomSheet<SheetEmpoweringAffirmCategoryBinding>() {
    var callBack: ((categoryList: List<CategoriesData>) -> Unit)? = null
    override fun getLayoutRes(): Int {
        return R.layout.sheet_empowering_affirm_category
    }

    override fun init() {
        clicks()


    }

    override fun onResume() {
        super.onResume()
        mBinding.fragmentView.getFragment<CategoriesFragment>().let {
            it.filterCategory=filterCategory
        }
    }

    private fun clicks() {
        mBinding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    fun callBackFunction(categoryList: List<CategoriesData>?) {
        callBack?.invoke(categoryList ?: mutableListOf())
        dismiss()
    }
}