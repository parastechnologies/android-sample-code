package com.highenergymind.view.sheet.managecategories

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.adapter.CategoryAdapter
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseBottomSheet
import com.highenergymind.data.CategoriesData
import com.highenergymind.data.GetCategoriesResponse
import com.highenergymind.data.SubCategoryData
import com.highenergymind.databinding.SheetManageCategoriesLayoutBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.newCategory.NewCategoryActivity
import com.highenergymind.view.sheet.subcategories.SubCategorySheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ManageCategorySheet : BaseBottomSheet<SheetManageCategoriesLayoutBinding>() {
    val viewModel by viewModels<ManageCategoryViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var empoweringAdapter: CategoryAdapter
    private var selectedCategoryPos = -1


    override fun getLayoutRes(): Int {
        return R.layout.sheet_manage_categories_layout
    }

    override fun init() {
        setCollectors()
        onClick()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()

    }


    private fun setCollectors() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                delSubResponse.collectLatest {
                    handleResponse(it, { resp ->
                        val response = resp as GetCategoriesResponse
                        val userData = sharedPrefs.getUserData()
                        userData?.categories = response.data
                        sharedPrefs.saveUserData(userData!!)
                    })
                }
            }
        }
    }

    private fun onClick() {
        mBinding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            saveSelectionBtn.setOnClickListener {
                requireContext().intentComponent(NewCategoryActivity::class.java)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val data = sharedPrefs.getUserData()?.categories

        empoweringAdapter =
            CategoryAdapter(
                true,
                data?.toMutableList() ?: mutableListOf(),sharedPrefs
            )
        empoweringAdapter.callBack = { pos: Int, item: CategoriesData, type: Int ->
            when (type) {
                R.id.addIV -> {
                    selectedCategoryPos = pos
                    selectSubCate(item)
                }
            }
        }
        empoweringAdapter.deleteCallBack = { innerItem: SubCategoryData, isLastItem: Boolean ->
            viewModel.apply {
                map.clear()
                map[ApiConstant.SUB_CAT_ID] = innerItem.id
                map[ApiConstant.MAIN] = if (isLastItem) "1" else "0"
                delSubApi()
            }
        }
        mBinding.categoryRV.adapter = empoweringAdapter

    }

    private fun selectSubCate(item: CategoriesData) {
        SubCategorySheet(item.id.toString(), item.subCategoryList).also {
            it.callBack = { selectedList ->
                empoweringAdapter.categoryList[selectedCategoryPos].let { itm ->
                    itm.subCategoryList = selectedList.toMutableList()
                    itm.isChecked = !itm.subCategoryList.isNullOrEmpty()
                }
                empoweringAdapter.notifyItemChanged(selectedCategoryPos)
            }
        }.show(childFragmentManager, "")
    }

}