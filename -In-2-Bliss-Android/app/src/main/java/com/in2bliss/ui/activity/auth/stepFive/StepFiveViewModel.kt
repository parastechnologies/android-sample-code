package com.in2bliss.ui.activity.auth.stepFive


import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.base.BaseResponse
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.Resource
import com.in2bliss.data.networkRequest.apiResponseHandler.safeApiRequest
import com.in2bliss.domain.ApiHelperInterface
import com.in2bliss.ui.activity.auth.stepFive.chooseSubCateBottomSheet.ChooseSubCatBottomSheetFragment
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class StepFiveViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
    private val requestManager: RequestManager,
    private val imageRequest: ImageRequest.Builder,
    private val imageLoader: ImageLoader
) : BaseViewModel() {

    var category: String? = null
    var isGeneral: String? = null

    val adapter by lazy {
        SelectSubCategoryAdapter(
            requestManager = requestManager,
            imageLoader = imageLoader,
            imageRequest = imageRequest
        )
    }

    private val mutableCategoryResponse by lazy {
        MutableSharedFlow<Resource<CategoryResponse>>()
    }
    val categoryReasonsResponse by lazy { mutableCategoryResponse.asSharedFlow() }

    private val mutableAddCategoryResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val addCategoryResponse by lazy { mutableAddCategoryResponse.asSharedFlow() }

    /**
     * Get category list api request
     * */
    private fun categoryList() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.TYPE] = ApiConstant.CategoryType.TEXT.value.toString()
            mutableCategoryResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.categoryList(
                        body = hashMap
                    )
                },
                apiName = ApiConstant.CATEGORY_LIST
            )
            mutableCategoryResponse.emit(
                value = response
            )
        }
    }

    /** Add category api request */
    private fun addCategory() {
        val hashMap = HashMap<String, String>()
        if (isGeneral != null) {
            hashMap[ApiConstant.CATEGORY] = getGeneralParam() ?: ""
        } else {
            hashMap[ApiConstant.CATEGORY] = category ?: ""
        }
        networkCallIo {
            mutableAddCategoryResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.addSubCategory(hashMap)
                },
                apiName = ApiConstant.CATEGORY_ADD
            )
            mutableAddCategoryResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.CATEGORY_LIST -> {
                categoryList()
            }

            ApiConstant.CATEGORY_ADD -> {
                addCategory()
            }
        }
    }

    /**
     * Opening the sub category bottom sheet and updating the selected subcategories
     * @param subCateList
     * @param subCatTitle
     * @param position
     * */
    fun openingSubCategoriesBottomSheet(
        subCateList: String,
        subCatTitle: String,
        position: Int,
        supportFragmentManager: FragmentManager
    ) {
        val bottomSheet = ChooseSubCatBottomSheetFragment(AppConstant.HomeCategory.STEP_FIVE)
        bottomSheet.arguments = bundleOf(
            AppConstant.CATEGORY_LIST to subCateList,
            AppConstant.CATEGORY_TITLE to subCatTitle
        )
        bottomSheet.selectedSubCategories = { subCategoriesList ->

            /**  Adding selected subCategories in adapter */
            val currentList = adapter.currentList
            currentList[position].selectedCategoryList?.clear()
            currentList[position].selectedCategoryList?.addAll(subCategoriesList)
            adapter.notifyItemChanged(position)
        }
        bottomSheet.show(
            supportFragmentManager, "Choose sub category bottom sheet"
        )
    }

    private fun getGeneralParam(): String? {
        val categoryList = mutableListOf<HashMap<String, Any>>()
        val category = HashMap<String, Any>()
        category[ApiConstant.CID] = isGeneral.toString() ?: ""
        val subCategoryList = mutableListOf<HashMap<String, Any>>()
        category[ApiConstant.SUB_CATEGORY] = subCategoryList
        categoryList.add(category)
        return Gson().toJson(categoryList)
    }
}

