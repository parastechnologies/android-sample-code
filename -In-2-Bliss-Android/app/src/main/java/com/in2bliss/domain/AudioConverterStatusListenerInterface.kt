package com.in2bliss.domain

import com.in2bliss.data.model.audioConversion.AudioConversion
import kotlinx.coroutines.flow.SharedFlow

interface AudioConverterStatusListenerInterface {

    /**
     * Getting Audio converted status
     * */
    fun getAudioConvertStatus(): SharedFlow<AudioConversion>

    /**
     * Change audio convert status
     * @param audioConversion is Audio conversion status
     * */
    fun changeAudioConvertStatus(
        audioConversion: AudioConversion
    )
}
