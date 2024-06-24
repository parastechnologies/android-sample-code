package com.in2bliss.ui.activity.home.affirmation.affirmation

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.utils.constants.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AffirmationViewModel @Inject constructor() : BaseViewModel() {

    val affirmation = ObservableField("Today will bring me joy, satisfaction, and happiness.")
    val date = ObservableField("12 May")
    val category = ObservableField("Self-love")
    var categoryName : AppConstant.HomeCategory ?= null

    var isSearch = false

    override fun retryApiRequest(apiName: String) {

    }
}

