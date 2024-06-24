package com.in2bliss.ui.activity.auth.stepFive

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityStepFiveBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.auth.appStatus.AppStatusActivity
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepFiveActivity : BaseActivity<ActivityStepFiveBinding>(
    layout = R.layout.activity_step_five
) {
    @Inject
    lateinit var sharedPreference: SharedPreference
    private val viewModel: StepFiveViewModel by viewModels()

    override fun init() {
        onClick()
        settingRecyclerView()
        observer()
        viewModel.retryApiRequest(
            apiName = ApiConstant.CATEGORY_LIST
        )
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.categoryReasonsResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@StepFiveActivity,
                    success = { response ->
                        viewModel.adapter.submitList(response.data)
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }

        lifecycleScope.launch {
            viewModel.addCategoryResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@StepFiveActivity,
                    success = {
                        val userData = sharedPreference.userData
                        userData?.data?.profileStatus = 4
                        sharedPreference.userData = userData
                        intent(
                            destination = AppStatusActivity::class.java
                        )
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    private fun settingRecyclerView() {
        binding.rvCategory.adapter = viewModel.adapter
        (binding.rvCategory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false

        viewModel.adapter.addDataListener = { position, catModel, subCategoryList ->
            val subCateList = Gson().toJson(subCategoryList)
            catModel.id?.let {
                viewModel.openingSubCategoriesBottomSheet(
                    subCateList = subCateList,
                    subCatTitle = catModel.name ?: "",
                    position = position,
                    supportFragmentManager = supportFragmentManager
                )
            }
        }
        viewModel.adapter.isGeneral = { generalStatus, id ->
            viewModel.isGeneral = id
        }
    }


    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnExplore.setOnClickListener {
            if (viewModel.isGeneral.isNullOrEmpty().not()) {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.CATEGORY_ADD
                )
                return@setOnClickListener
            }
            if (isSubCategory()) {
                viewModel.retryApiRequest(
                    apiName = ApiConstant.CATEGORY_ADD
                )
                return@setOnClickListener
            }
            showToast(getString(R.string.please_select_subcategory))
        }
    }

    /**
     * Adding selected category and sub category
     * @return true if category selected else false
     * */
    private fun isSubCategory(): Boolean {
        val categoryList = viewModel.adapter.currentList
        val addCategory = mutableListOf<HashMap<String, Any>>()
        categoryList.forEach { data ->

            if (data.selectedCategoryList?.isNotEmpty() == true) {
                val categoryMap = HashMap<String, Any>()
                categoryMap[ApiConstant.CID] = data.id.toString()

                val subCategoryMap = mutableListOf<HashMap<String, String>>()

                data.selectedCategoryList?.forEach { subData ->
                    val map = HashMap<String, String>()
                    map[ApiConstant.SCID] = subData.categoryId.toString()
                    map[ApiConstant.CID] = data.id.toString()
                    subCategoryMap.add(map)
                }

                categoryMap[ApiConstant.SUB_CATEGORY] = subCategoryMap
                addCategory.add(categoryMap)
            }
        }

        if (addCategory.size == 0) return false
        viewModel.category = Gson().toJson(addCategory)
        return true
    }
}

