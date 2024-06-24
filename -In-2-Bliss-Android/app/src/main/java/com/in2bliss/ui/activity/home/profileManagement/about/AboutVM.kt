package com.in2bliss.ui.activity.home.profileManagement.about

import androidx.databinding.ObservableField
import com.in2bliss.R
import com.in2bliss.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutVM @Inject constructor() : BaseViewModel() {

    val title = ObservableField(R.string.content_containing_1000_words_with_images_that_will_be_hardcoded)

    override fun retryApiRequest(apiName: String) {
    }
}