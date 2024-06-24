package com.in2bliss.ui.activity.home.fragment.updateAffirmation.detail

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.utils.constants.AppConstant
import javax.inject.Inject

class DetailsVM  @Inject constructor() : BaseViewModel(){


    val affirmation = ObservableField("")
    val title = ObservableField("")
    val textCount = ObservableField("0/50")
    val createAffirmationCount = ObservableField("")
    val createAffirmation = ObservableField("")
    val transcript = ObservableField("")
    val transcriptCount = ObservableField("")

    var isJournal = false
    var categoryType : AppConstant.HomeCategory? = null
    var isTranscript = false


    override fun retryApiRequest(apiName: String) {

    }
}