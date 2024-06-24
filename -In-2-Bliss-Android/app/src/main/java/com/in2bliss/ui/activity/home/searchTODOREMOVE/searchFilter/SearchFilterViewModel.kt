package com.in2bliss.ui.activity.home.searchTODOREMOVE.searchFilter

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.CategoryModel
import com.in2bliss.data.model.SearchFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchFilterViewModel @Inject constructor() : BaseViewModel() {

    val showResult = ObservableField("Show 145 results")
    var selectedCategoryPosition = -1

    val adapter by lazy {
        SearchFilterAdapter()
    }

    val searchFilter = arrayListOf(
        SearchFilter(
            "stress relief ",
            arrayListOf(
                CategoryModel(
                    categoryName = "Breathing exercises",
                ),
                CategoryModel(
                    categoryName = "Stress reduction",
                ),
                CategoryModel(
                    categoryName = "Managing stress"
                )
            )
        ),
        SearchFilter(
            "anxiety management",
            arrayListOf(
                CategoryModel(
                    categoryName = "Anxiety relief",
                ),
                CategoryModel(
                    categoryName = "Anxiety reduction",
                ),
                CategoryModel(
                    categoryName = "Calming anxiety"
                )
            )
        ),
        SearchFilter(
            "fear and phobia relief",
            arrayListOf(
                CategoryModel(
                    categoryName = "Overcoming fear",
                ),
                CategoryModel(
                    categoryName = "Conquering phobias",
                ),
                CategoryModel(
                    categoryName = "Managing fear"
                )
            )
        )
    )

    override fun retryApiRequest(apiName: String) {

    }
}