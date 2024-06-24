package com.in2bliss.ui.activity.home.affirmation.affirmationCategories

import android.util.Log
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
import com.in2bliss.ui.activity.auth.stepFive.SelectSubCategoryAdapter
import com.in2bliss.ui.activity.auth.stepFive.chooseSubCateBottomSheet.ChooseSubCatBottomSheetFragment
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AffirmationCategoriesViewModel @Inject constructor(
    private val apiHelperInterface: ApiHelperInterface,
     val requestManager: RequestManager,
     val imageLoader: ImageLoader,
     val imageRequest: ImageRequest.Builder
) : BaseViewModel() {

    var isAffirmationCategoryFilter = false
    var category: String? = null
    var affirmation: String? = null
    var image: String? = null
    var affirmationId: String? = null
    var isEdit = false
    var isGeneral: String? = null
    var type: String? = null
    var categoryType: AppConstant.HomeCategory? = null



    private val mutableCategoryResponse by lazy {
        MutableSharedFlow<Resource<CategoryResponse>>()
    }
    val categoryReasonsResponse by lazy { mutableCategoryResponse.asSharedFlow() }

    private val mutableAddUpdateAffirmationResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }
    val addUpdateAffirmationResponse by lazy { mutableAddUpdateAffirmationResponse.asSharedFlow() }

    val adapter by lazy {
        SelectSubCategoryAdapter(
            requestManager = requestManager, imageLoader = imageLoader, imageRequest = imageRequest,
        )
    }

    val myAffirmationAdapter by lazy {
        SelectSubCategoryAdapter(
            requestManager = requestManager, imageLoader = imageLoader, imageRequest = imageRequest,
        )
    }




    val addCategoryResponse by lazy { mutableAddCategoryResponse.asSharedFlow() }


    private val mutableAddCategoryResponse by lazy {
        MutableSharedFlow<Resource<BaseResponse>>()
    }

    private fun categoryList() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            val type = when (categoryType) {
                AppConstant.HomeCategory.GUIDED_AFFIRMATION -> ApiConstant.CategoryType.GUIDED_AFFIRMATION.value
                AppConstant.HomeCategory.GUIDED_MEDITATION -> ApiConstant.CategoryType.GUIDED_MEDITATION.value
                AppConstant.HomeCategory.WISDOM_INSPIRATION -> ApiConstant.CategoryType.GUIDED_MEDITATION.value
                else -> ApiConstant.CategoryType.TEXT.value
            }
            hashMap[ApiConstant.TYPE] = type.toString()

            mutableCategoryResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.categoryList(
                        body = hashMap
                    )
                }, apiName = ApiConstant.CATEGORY_LIST
            )
            mutableCategoryResponse.emit(
                value = response
            )
        }
    }

    /**
     * Add affirmation
     * */
    private fun addAffirmation() {
        networkCallIo {

            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.TYPE] = "0"
            hashMap[ApiConstant.CATEGORY] = category.orEmpty()
            hashMap[ApiConstant.DESCRIPTION] = affirmation.orEmpty()
            hashMap[ApiConstant.THUMBNAIL] = image.orEmpty()

            mutableAddUpdateAffirmationResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.addTextAffirmation(hashMap)
                }, apiName = ApiConstant.ADD_AFFIRMATION
            )
            mutableAddUpdateAffirmationResponse.emit(
                value = response
            )
        }
    }

    /**
     * Add affirmation
     * */
    private fun updateAffirmation() {
        networkCallIo {
            val hashMap = HashMap<String, String>()
            hashMap[ApiConstant.TYPE] = "0"
            hashMap[ApiConstant.CATEGORY] = category.orEmpty()
            hashMap[ApiConstant.DESCRIPTION] = affirmation.orEmpty()
            hashMap[ApiConstant.THUMBNAIL] = image.orEmpty()
            hashMap[ApiConstant.AFFIRMATION_ID] = affirmationId.orEmpty()

            mutableAddUpdateAffirmationResponse.emit(
                value = Resource.Loading()
            )
            val response = safeApiRequest(
                apiRequest = {
                    apiHelperInterface.updateTextAffirmation(hashMap)
                }, apiName = ApiConstant.ADD_AFFIRMATION
            )
            mutableAddUpdateAffirmationResponse.emit(
                value = response
            )
        }
    }

    override fun retryApiRequest(apiName: String) {
        when (apiName) {
            ApiConstant.CATEGORY_LIST -> categoryList()
            ApiConstant.ADD_AFFIRMATION -> addAffirmation()
            ApiConstant.AFFIRMATION_UPDATE -> updateAffirmation()
            ApiConstant.CATEGORY_ADD -> {
                addCategory()
            }
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
                }, apiName = ApiConstant.CATEGORY_ADD
            )
            mutableAddCategoryResponse.emit(
                value = response
            )
        }
    }

    /**
     * Opening the sub category bottom sheet and updating the selected subcategories
     * @param subCateList
     * @param subCatTitle
     * @param position
     * */
    fun openingSubCategoriesBottomSheet(
        screenType:AppConstant.HomeCategory?=null,
        subCateList: String,
        subCatTitle: String,
        position: Int,
        supportFragmentManager: FragmentManager
    ) {
        val bottomSheet = ChooseSubCatBottomSheetFragment(screenType = screenType)
        bottomSheet.arguments = bundleOf(
            AppConstant.CATEGORY_LIST to subCateList, AppConstant.CATEGORY_TITLE to subCatTitle
        )
        bottomSheet.selectedSubCategories = { subCategoriesList ->
//            if (categoryType == null) {
//                /** Clearing the previous selected category */
//                adapter.currentList.forEachIndexed { index, data ->
//                    if ((data.selectedCategoryList?.size ?: 0) > 0 && index != position) {
//                        adapter.currentList[index].selectedCategoryList?.clear()
//                        adapter.notifyItemChanged(index)
//                    }
//                }
//            }

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

    /***
     * Creating category list api request
     * */
    fun isSubCategory(): Boolean {
        val categoryList = mutableListOf<HashMap<String, Any>>()
        adapter.currentList.forEach { data ->
            if (data.selectedCategoryList?.isNotEmpty() == true) {
                val category = HashMap<String, Any>()
                category[ApiConstant.CID] = data.id.toString()
                val subCategoryList = mutableListOf<HashMap<String, Any>>()
                data.selectedCategoryList?.forEach { subData ->
                    val subCategory = HashMap<String, Any>()
                    subCategory[ApiConstant.SCID] = subData.categoryId
                    subCategory[ApiConstant.CID] = data.id.toString()
                    subCategoryList.add(subCategory)
                }
                category[ApiConstant.SUB_CATEGORY] = subCategoryList
                categoryList.add(category)
            }
        }

        if (categoryList.size == 0) return false
        category = Gson().toJson(categoryList)
        return true
    }
}