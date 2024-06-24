package com.in2bliss.domain

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

interface RecordAudioInterface {

    /**
     * Record audio
     **/
    fun startRecordAudio(context: Context)

    /**
     * Pausing media recorder
     * */
    fun pauseAudioRecorder()

    /**
     * Resume audio recorder
     * */
    fun resumeAudioRecorder()

    /**
     * Stopping media recorder
     * */
    fun stopAudioRecorder(): Uri?

    /**
     * Return state flow is recording
     * */
    fun isRecording(): StateFlow<Boolean>

    /**
     * Is media recorder initialized
     * */
    fun isMediaRecorderInitialized(): Boolean

    /**
     * Amplitude callback
     * */
    fun getAmplitude(): StateFlow<Long>

    /**
     * Recorder progress
     * */
    fun getRecorderProgress(): StateFlow<Long>
}