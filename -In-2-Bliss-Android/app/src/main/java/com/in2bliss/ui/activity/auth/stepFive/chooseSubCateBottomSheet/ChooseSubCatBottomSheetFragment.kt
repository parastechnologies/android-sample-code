package com.in2bliss.ui.activity.auth.stepFive.chooseSubCateBottomSheet

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.in2bliss.R
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.data.model.SelectedCategory
import com.in2bliss.databinding.FragmentChooseSubCatBottomSheetBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseSubCatBottomSheetFragment(var screenType: AppConstant.HomeCategory? = null) :
    BottomSheetDialogFragment() {

    @Inject
    lateinit var requestManager: RequestManager
    private lateinit var binding: FragmentChooseSubCatBottomSheetBinding
    private var adapter: SubCategoriesAdapter? = null
    var selectedSubCategories: ((selectedSubCategories: ArrayList<SelectedCategory>) -> Unit)? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseSubCatBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 2500
        dialog?.setCanceledOnTouchOutside(false)
        setScreenTypeSubTitle()
        settingRecyclerView()
        bundle()
        onClick()
    }

    private fun setScreenTypeSubTitle() {
        Log.d("csacsacsa", "setScreenTypeSubTitle: ${screenType?.name.toString()}")
        binding.tvSubTitle.gone()
        when (screenType) {
            AppConstant.HomeCategory.GUIDED_MEDITATION -> {
                binding.tvSubTitle.text =
                    getString(R.string.select_all_the_mediation_subcategories_you)
            }

            AppConstant.HomeCategory.WISDOM_INSPIRATION -> {
                binding.tvSubTitle.text = getString(R.string.wisdom_inspiration_bottom_subtitle)
            }

            else -> {
                binding.tvSubTitle.text =
                    getString(R.string.select_all_the_affirmation_categories_you_want_to_recieve_from)
            }
        }
    }

    private fun settingRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSubCategory.layoutManager = layoutManager
        adapter = SubCategoriesAdapter(
            requestManager = requestManager
        )
        binding.rvSubCategory.itemAnimator = null
        binding.rvSubCategory.adapter = adapter
    }

    private fun bundle() {
        arguments?.getString(AppConstant.CATEGORY_LIST)?.let { categoryList ->
            val list = object : TypeToken<List<CategoryResponse.Data.SubCategory>>() {}.type
            val subCategoryList: List<CategoryResponse.Data.SubCategory> =
                Gson().fromJson(categoryList, list)
            adapter?.submitList(subCategoryList) {
                binding.tvNoCategories.visibility(adapter?.currentList?.isEmpty() == true)
            }
        }
        arguments?.getString(AppConstant.CATEGORY_TITLE)?.let { title ->


            val bottomSheetTitle =   when(screenType){
                AppConstant.HomeCategory.GUIDED_AFFIRMATION->{
                    "Select all $title categories would you like to see?"

                }
                AppConstant.HomeCategory.GUIDED_MEDITATION->{
                    "Which $title categories would you like to see?"

                }
                AppConstant.HomeCategory.STEP_FIVE->{
                    "Select all the $title categories you would like to receive as pictures and reminders?"

                }
                else->{
                    "Which $title categories would you like to see?"
                }
            }
            if (title.contains("beginners",true)){
                binding.tvTitle.text= getString(R.string.if_you_are_new_to_meditation_start_here)
            }else {

                binding.tvTitle.text = try {
                    val spannableString = SpannableString(bottomSheetTitle)
                    val titlePosition = bottomSheetTitle.indexOf(title)

                    spannableString.setSpan(
                        ForegroundColorSpan(
                            Color.rgb(18, 4, 106)
                        ), 0, titlePosition - 1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    spannableString.setSpan(
                        ForegroundColorSpan(
                            Color.rgb(95, 70, 244)
                        ),
                        titlePosition,
                        ((titlePosition + (title.length))),
                        SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    spannableString.setSpan(
                        ForegroundColorSpan(
                            Color.rgb(18, 4, 106)
                        ),
                        ((titlePosition + (title.length))),
                        bottomSheetTitle.length,
                        SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    spannableString
                } catch (exception: Exception) {
                    title
                }
            }
        }
    }

    private fun onClick() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        /** Returning the selected categories in callback */
        binding.btnContinue.setOnClickListener {
            val tempList: ArrayList<SelectedCategory> = arrayListOf()
            adapter?.currentList?.forEach { subCategories ->
                if (subCategories.isSelected) {
                    subCategories.name?.let { categoryName ->
                        subCategories.id?.let { categoryId ->
                            tempList.add(
                                SelectedCategory(
                                    subCategoryName = categoryName,
                                    categoryId = categoryId
                                )
                            )
                        }
                    }
                }
            }
            selectedSubCategories?.invoke(tempList)
            dialog?.dismiss()
        }
    }
}

