package com.in2bliss.ui.activity.home.affirmation.affirmationCategories

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityAffirmationCategoriesBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmationExplore.AffirmationExploreActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AffirmationCategoriesActivity : BaseActivity<ActivityAffirmationCategoriesBinding>(
    layout = R.layout.activity_affirmation_categories
) {

    @Inject
    lateinit var sharedPreference: SharedPreference


    private val viewModel: AffirmationCategoriesViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.my_affirmations)
        onClick()
        gettingBundleData()
        settingRecyclerView()
        observer()
        viewModel.retryApiRequest(
            apiName = ApiConstant.CATEGORY_LIST
        )
        if (viewModel.isAffirmationCategoryFilter) {
            binding.toolBar.ivMenu.gone()
            binding.clTitle.gone()
            binding.clTitle2.visible()
            binding.tvTitle.visible()
            binding.tvSelectCategory2.text =
                getString(R.string.select_all_the_affirmation_categories_you_wish_to_receive_to_positively_align_your_thoughts_feelings_and_actions)
            binding.tvTitle2.text =
                getString(R.string.discover_affirmations_for_your_mind_body_and_soul)
        }

        if (viewModel.categoryType == null && !viewModel.isAffirmationCategoryFilter) {
            binding.tvSelectSubCategory.visible()
        }

    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.categoryReasonsResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationCategoriesActivity,
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
            viewModel.addUpdateAffirmationResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@AffirmationCategoriesActivity,
                    success = {
                        setResult(Activity.RESULT_OK)
                        finish()
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
                    context = this@AffirmationCategoriesActivity,
                    success = {
                        setResult(Activity.RESULT_OK, Intent())
                        finish()
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

    private fun gettingBundleData() {

        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { category ->
            viewModel.categoryType = when (category) {
                AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
                AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
                AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> AppConstant.HomeCategory.WISDOM_INSPIRATION
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                else -> null
            }
        }

        intent.getBooleanExtra(AppConstant.IS_AFFIRMATION_CATEGORIES_FILTER, false)
            .let { isFilter ->
                viewModel.isAffirmationCategoryFilter = isFilter
            }

        intent.getStringExtra(AppConstant.AFFIRMATION)?.let { affirmation ->
            viewModel.affirmation = affirmation
        }

        intent.getStringExtra(AppConstant.TYPE)?.let { type ->
            viewModel.type = type
        }

        intent.getStringExtra(AppConstant.IMAGE_STRING)?.let { image ->
            viewModel.image = image
        }

        intent.getStringExtra(AppConstant.ID)?.let { id ->
            viewModel.affirmationId = id
        }

        intent.getBooleanExtra(AppConstant.EDIT, false).let { isEdit ->
            viewModel.isEdit = isEdit
        }

        binding.toolBar.tvSkip.visibility(
            isVisible = false
        )
        binding.toolBar.ivMenu.visibility(
            isVisible = if (viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                viewModel.categoryType == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                viewModel.categoryType == AppConstant.HomeCategory.WISDOM_INSPIRATION
            ) {
                false
            } else viewModel.isAffirmationCategoryFilter
        )

        binding.tvSelectCategory.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.GUIDED_MEDITATION &&
                    viewModel.categoryType != AppConstant.HomeCategory.GUIDED_AFFIRMATION &&
                    viewModel.categoryType != AppConstant.HomeCategory.WISDOM_INSPIRATION
        )

        binding.toolBar.tvTitle.visibility(
            isVisible = viewModel.categoryType != AppConstant.HomeCategory.GUIDED_MEDITATION &&
                    viewModel.categoryType != AppConstant.HomeCategory.GUIDED_AFFIRMATION &&
                    viewModel.categoryType != AppConstant.HomeCategory.WISDOM_INSPIRATION
        )

        binding.btnContinue.setText(
            if (viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                viewModel.categoryType == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                viewModel.categoryType == AppConstant.HomeCategory.WISDOM_INSPIRATION
            ) {
                R.string.next
            } else R.string.continue_
        )

        binding.tvTitle.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                    viewModel.categoryType == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    viewModel.categoryType == AppConstant.HomeCategory.WISDOM_INSPIRATION
        )

        binding.tvSubText.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.GUIDED_MEDITATION ||
                    viewModel.categoryType == AppConstant.HomeCategory.GUIDED_AFFIRMATION ||
                    viewModel.categoryType == AppConstant.HomeCategory.WISDOM_INSPIRATION
        )

        binding.tvTitle.text = when (viewModel.categoryType) {
            AppConstant.HomeCategory.GUIDED_AFFIRMATION -> getString(R.string.search_by_guided_affirmation_categories)
            AppConstant.HomeCategory.GUIDED_MEDITATION -> getString(R.string.search_by_meditation_category)
            AppConstant.HomeCategory.WISDOM_INSPIRATION -> getString(R.string.search_by_wisdom_ins)
            else -> ""
        }

        binding.tvSubText.text = getString(R.string.sub_text_guided_affirmation)


        binding.tvSelectCategory.setText(
            if (viewModel.isAffirmationCategoryFilter) {
                R.string.select_all_the_affirmation_categories_you_wish_to_receive_to_positively_align_your_thoughts_feelings_and_actions
            } else R.string.select_categories_that_closely_relate_to_your_affirmation
        )
    }

    private fun settingRecyclerView() {
        viewModel.adapter.type = viewModel.type
        binding.rvCategory.adapter = viewModel.adapter
        (binding.rvCategory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        viewModel.adapter.addDataListener = { position, catModel, subCategoryList ->
            val subCateList = Gson().toJson(subCategoryList)
            catModel.id?.let {
                viewModel.openingSubCategoriesBottomSheet(
                    screenType = viewModel.categoryType,
                    subCateList = subCateList,
                    subCatTitle = catModel.name ?: "",
                    position = position,
                    supportFragmentManager = supportFragmentManager
                )
            }
            val arrayList = ArrayList<CategoryResponse.Data.SubCategory>()
            arrayList.addAll(subCategoryList)
        }
        viewModel.adapter.isGeneral = { generalStatus, id ->
            if (viewModel.isAffirmationCategoryFilter) {
                viewModel.isGeneral = id
            } else {
                viewModel.isGeneral = generalStatus
            }
        }
        viewModel.adapter.myAffirmation = { id ->
            viewModel.category = id
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            when (viewModel.categoryType) {
                AppConstant.HomeCategory.GUIDED_MEDITATION,
                AppConstant.HomeCategory.GUIDED_AFFIRMATION,
                AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
                    if (viewModel.isGeneral.isNullOrEmpty().not()) {
                        val bundle = bundleOf(
                            AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
                            AppConstant.IS_GENERAL to viewModel.isGeneral,
                            AppConstant.TYPE to viewModel.type,
                        )

                        intent(
                            destination = AffirmationExploreActivity::class.java,
                            bundle = bundle
                        )

                        return@setOnClickListener
                    }

                    var catId = ""
                    var selectedSubCat = ""
                    viewModel.adapter.currentList.forEach { data ->
                        if (data.selectedCategoryList?.isNotEmpty() == true) {
                            catId += "${data.id},"

                            data.selectedCategoryList?.forEach { subCat ->
                                selectedSubCat += "${subCat.categoryId},"
                            }
                        }
                    }
                    if (selectedSubCat.contains(",")) {
                        selectedSubCat = selectedSubCat.substring(0, selectedSubCat.length - 1)
                    }
                    if (catId.contains(",")) {
                        catId = catId.substring(0, catId.length - 1)
                    }

                    if (selectedSubCat.isNotEmpty()) {
                        val bundle = bundleOf(
                            AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
                            AppConstant.CATEGORY_ID to catId,
                            AppConstant.SUB_CATEGORY_ID to selectedSubCat,
                            AppConstant.TYPE to viewModel.type,
                        )
                        if (viewModel.type == AppConstant.TYPE) {
                            intent(
                                destination = AffirmationExploreActivity::class.java,
                                bundle = bundle
                            )
                            finish()
                        } else {

                            intent(
                                destination = AffirmationExploreActivity::class.java,
                                bundle = bundle
                            )
                        }


                        return@setOnClickListener
                    }
                    showToast(getString(R.string.please_select_category))
                }

                else -> {
                    if (viewModel.isAffirmationCategoryFilter) {
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
                        } else {
                            showToast(getString(R.string.please_select_subcategory))
                        }
                    } else if (viewModel.type == "0") {
                        Log.d("sacsacsacsac", "onClick: ${viewModel.category}")
                        if (viewModel.category.isNullOrEmpty().not()) {
                            viewModel.retryApiRequest(apiName = ApiConstant.ADD_AFFIRMATION)
                            return@setOnClickListener
                        } else {
                            showToast(getString(R.string.please_select_subcategory))
                        }
                    } else if (isValidate()) {
                        if (viewModel.isEdit) {
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.AFFIRMATION_UPDATE
                            )
                            return@setOnClickListener
                        }
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.ADD_AFFIRMATION
                        )
                    }
                }
            }
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

    private fun isValidate(): Boolean {
        return when {
            (!viewModel.isSubCategory()) -> {
                showToast(getString(R.string.please_select_category))
                false
            }

            else -> true
        }
    }
}

