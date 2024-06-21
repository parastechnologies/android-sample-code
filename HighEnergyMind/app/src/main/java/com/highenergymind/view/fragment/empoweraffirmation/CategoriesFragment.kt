package com.highenergymind.view.fragment.empoweraffirmation

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.highenergymind.R
import com.highenergymind.adapter.CategoryAdapter
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseFragment
import com.highenergymind.data.CategoriesData
import com.highenergymind.data.GetCategoriesResponse
import com.highenergymind.databinding.FragmentEmpoweringAffirmationBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.view.activity.newCategory.NewCategoryActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessActivity
import com.highenergymind.view.activity.signUpProcess.SignUpProcessViewModel
import com.highenergymind.view.sheet.SelectCategorySheet
import com.highenergymind.view.sheet.empoweringAffirmationCategory.EmpoweringAffirmCategorySheet
import com.highenergymind.view.sheet.subcategories.SubCategorySheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : BaseFragment<FragmentEmpoweringAffirmationBinding>() {
    private val viewModel by viewModels<CategoriesViewModel>()
    private lateinit var empoweringAdapter: CategoryAdapter
    private var selectedCategoryPos = -1
    var filterCategory: List<CategoriesData>? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_empowering_affirmation
    }

    override fun initViewWithData() {
        requireActivity().fullScreenStatusBar()
        setCollectors()
        setData()
        onClick()
        if (!::empoweringAdapter.isInitialized) {
            viewModel.getCategories()
        } else {
            mBinding.categoryRV.adapter = empoweringAdapter
        }
    }

    private fun setData() {
        when (requireActivity()) {
            is SignUpProcessActivity -> {
                /** do nothing **/
            }

            is NewCategoryActivity -> {
                /** do nothing **/

            }

            else -> {
                mBinding.apply {
                    contentTV.text =
                        requireContext().getString(R.string.select_at_least_one_to_category)
                    contiguousBtn.text = requireContext().getString(R.string.show_affirmations)
                }
            }
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
                updateCategoriesResponse.collectLatest {
                    handleResponse(it, { resp ->
                        val response = resp as GetCategoriesResponse
                        val userData = viewModel.sharedPrefs.getUserData()
                        userData?.categories = response.data
                        viewModel.sharedPrefs.saveUserData(userData!!)
                        requireActivity().finish()
                    })
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                categoriesResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as GetCategoriesResponse
                        when (parentFragment) {
                            is EmpoweringAffirmCategorySheet -> {
                                if (filterCategory.isNullOrEmpty().not()) {
                                    preFilterSelection(response)
                                } else {
                                    preSelected(response)
                                }
                            }
                        }

                        when (requireActivity()) {
                            is NewCategoryActivity -> {
                                preSelected(response)
                            }
                        }
                        setupRecyclerView(response.data)
                    })
                }
            }
        }
    }

    private fun preFilterSelection(response: GetCategoriesResponse) {
        filterCategory?.forEach { catg ->
            catg.isChecked = true
            response.data.find { it.id == catg.id }?.let { fi ->
                response.data[response.data.indexOf(fi)] = catg
            }
        }
    }

    private fun preSelected(response: GetCategoriesResponse) {
        val oldCategory = viewModel.sharedPrefs.getUserData()?.categories
        oldCategory?.forEach { catg ->
            catg.isChecked = true
            response.data.find { it.id == catg.id }?.let { fi ->
                response.data[response.data.indexOf(fi)] = catg
            }
        }
    }


    override fun onResume() {
        super.onResume()

        when (requireActivity()) {
            is SignUpProcessActivity -> {
                (requireActivity() as SignUpProcessActivity).apply {
                    setProgressMeter(com.intuit.sdp.R.dimen._98sdp)
                }
            }
        }
    }

    private fun onClick() {

        mBinding.contiguousBtn.setOnClickListener {
            if (empoweringAdapter.categoryList.none { it.isChecked }) {
                SelectCategorySheet().show(childFragmentManager, "")
            } else {
                when (parentFragment) {
                    is EmpoweringAffirmCategorySheet -> {
                        (parentFragment as EmpoweringAffirmCategorySheet).let { p ->
                            val checkedCategory =
                                empoweringAdapter.categoryList.filter { it.isChecked }
                            p.callBackFunction(checkedCategory)
                        }
                    }
                }
                when (requireActivity()) {
                    is SignUpProcessActivity -> {
                        (activityViewModels<SignUpProcessViewModel>().value).apply {
                            val checkedCategory =
                                empoweringAdapter.categoryList.filter { it.isChecked }
                            /** getting comma separated categoryId's using below method**/
                            map[ApiConstant.CATEGORY] =
                                checkedCategory.map { it.id }.joinToString(",")
                            val subCategorySpan = SpannableStringBuilder()
                            /** getting comma separated subCategoryId's using below method**/
                            checkedCategory.filter { !it.subCategoryList.isNullOrEmpty() }
                                .forEach { categ ->
                                    categ.subCategoryList?.forEach { sub ->
                                        subCategorySpan.append(sub.id.toString() + ",")
                                    }
                                }
                            /** removing extra comma from the last**/
                            map[ApiConstant.SUB_CATEGORY] =
                                subCategorySpan.removeSuffix(",").toString()

                        }
                        view?.let { view ->
                            Navigation.findNavController(view)
                                .navigate(R.id.action_empowering_to_signUpFragment)
                        }
                    }

                    is NewCategoryActivity -> {

                        viewModel.apply {
                            val checkedCategory =
                                empoweringAdapter.categoryList.filter { it.isChecked }
                            /** getting comma separated categoryId's using below method**/
                            map[ApiConstant.CATEGORY] =
                                checkedCategory.map { it.id }.joinToString(",")
                            val subCategorySpan = SpannableStringBuilder()
                            /** getting comma separated subCategoryId's using below method**/
                            checkedCategory.filter { !it.subCategoryList.isNullOrEmpty() }
                                .forEach { categ ->
                                    categ.subCategoryList?.forEach { sub ->
                                        subCategorySpan.append(sub.id.toString() + ",")
                                    }
                                }
                            /** removing extra comma from the last**/
                            map[ApiConstant.SUB_CATEGORY] =
                                subCategorySpan.removeSuffix(",").toString()
                            updateCategory()
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(data: List<CategoriesData>) {
        empoweringAdapter =
            CategoryAdapter(
                false,
                data.toMutableList(), sharedPrefs = viewModel.sharedPrefs
            )
        empoweringAdapter.callBack = { pos: Int, item: CategoriesData, type: Int ->
            when (type) {
                R.id.addIV -> {
                    selectedCategoryPos = pos
                    selectSubCate(item)
                }
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