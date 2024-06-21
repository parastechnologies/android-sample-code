package com.highenergymind.view.sheet.category

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.CategoriesData
import com.highenergymind.data.CheckModel
import com.highenergymind.data.GetCategoriesResponse
import com.highenergymind.databinding.SheetCategoryLayoutBinding
import com.highenergymind.view.adapter.RadioButtonAdapter
import com.highenergymind.view.fragment.empoweraffirmation.CategoriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategorySheet(var categoryList: MutableList<CheckModel>) :
    BaseBottomSheet<SheetCategoryLayoutBinding>() {
    var callBack: ((MutableList<CheckModel>) -> Unit)? = null
    val viewModel by viewModels<CategoriesViewModel>()
    val adapter by lazy {
        RadioButtonAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.sheet_category_layout
    }

    override fun init() {
        setAdapter()
        setCollectors()
        onClick()
        if (categoryList.isEmpty()) {
            viewModel.getCategories()
        } else {
            adapter.submitList(categoryList)
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                categoriesResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as GetCategoriesResponse
                        val checkList = response.data.toCheckModel()
                        adapter.submitList(checkList)
                    })
                }
            }
        }
    }

    private fun setAdapter() {
        mBinding.apply {
            rvOptions.adapter = adapter
        }
    }


    private fun onClick() {
        mBinding.apply {
            crossIV.setOnClickListener {
                dialog!!.dismiss()
            }
            getStartedBtn.setOnClickListener {
                callBack?.invoke(adapter.currentList)
                dialog!!.dismiss()
            }
        }
    }

    private fun List<CategoriesData>.toCheckModel(): List<CheckModel> {
        val checkList = mutableListOf<CheckModel>()
        forEach {
            checkList.add(CheckModel(it.categoryName, id = it.id.toString()))
        }
        return checkList
    }
}