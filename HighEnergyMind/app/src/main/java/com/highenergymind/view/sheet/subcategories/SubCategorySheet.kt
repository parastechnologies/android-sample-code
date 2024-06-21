package com.highenergymind.view.sheet.subcategories

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.adapter.SubCategoriesAdapter
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.GetSubCategoryResponse
import com.highenergymind.data.SubCategoryData
import com.highenergymind.databinding.ItemsSelectSubcategoriesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Created by developer on 28/02/24
 */
@AndroidEntryPoint
class SubCategorySheet(
    private var categoryId: String,
    var preSubCategoryList: MutableList<SubCategoryData>? = null,

    ) :
    BaseBottomSheet<ItemsSelectSubcategoriesBinding>() {
    private val viewModel by viewModels<SubCategoryViewModel>()
    private  var subCategoriesAdapter: SubCategoriesAdapter?=null
    var callBack: ((selectedList: List<SubCategoryData>) -> Unit)? =
        null

    override fun getLayoutRes(): Int {
        return R.layout.items_select_subcategories
    }

    override fun init() {
        setCollectors()
        clicks()
        viewModel.apply {
            map.clear()
            map[ApiConstant.CATEGORY_ID] = categoryId
            getSubCategories()
        }


    }

    private fun clicks() {
        mBinding.saveSelectionBtn.setOnClickListener {
            val selectedData = subCategoriesAdapter?.subCategoryList?.filter {
                it.isChecked
            }
            selectedData?.let { it1 -> callBack?.invoke(it1) }
            dismiss()
        }
    }

    private fun setAdapter(data: List<SubCategoryData>) {
        mBinding.apply {
            subCategoriesAdapter =
                SubCategoriesAdapter(
                    data
                )

            subCategoryRV.adapter = subCategoriesAdapter
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
                subCategoriesResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as GetSubCategoryResponse
                        val allSubCategoryList = response.data.toMutableList()
                        allSubCategoryList.forEach { sub ->
                            preSubCategoryList?.find { it.id == sub.id }?.let { ine ->
                                sub.isChecked = true
                            }
                        }
                        setAdapter(response.data)
                    })
                }
            }
        }
    }

}