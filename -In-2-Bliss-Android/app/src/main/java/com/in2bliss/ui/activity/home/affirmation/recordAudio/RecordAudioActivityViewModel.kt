package com.in2bliss.ui.activity.home.affirmation.recordAudio

import androidx.databinding.ObservableField
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.recorder.RecordAudioImpl
import com.in2bliss.domain.RecordAudioInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordAudioActivityViewModel @Inject constructor() : BaseViewModel() {

    val transcript = ObservableField("")
    val title = ObservableField("")
    val duration = ObservableField("")

    var mediaRecorder: RecordAudioInterface? = null
    var createAffirmation: CreateAffirmation? = null

    /**
     * Initializing media recorder
     * */
    fun initializeMediaRecorder() {
        if (mediaRecorder == null) {
            mediaRecorder = getRecordAudioInstance()
        }
    }

    private fun getRecordAudioInstance(): RecordAudioInterface {
        return RecordAudioImpl()
    }

    override fun retryApiRequest(apiName: String) {

    }
}