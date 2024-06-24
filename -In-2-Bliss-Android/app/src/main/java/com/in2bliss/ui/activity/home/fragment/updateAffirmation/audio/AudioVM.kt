package com.in2bliss.ui.activity.home.fragment.updateAffirmation.audio

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioVM @Inject constructor() : BaseViewModel() {

    val title = ObservableField("Things I appreciate!")
    val dateAndTime = ObservableField("YESTERDAY, 27 MAR . 9:41 PM")
    val recordingStartTime = ObservableField("00:01")
    val recordingEndTime = ObservableField("12:01")

    override fun retryApiRequest(apiName: String) {
    }
}

