package com.in2bliss.data.audioConverter

import com.in2bliss.data.model.audioConversion.AudioConversion
import com.in2bliss.domain.AudioConverterStatusListenerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AudioConverterStatusImpl : AudioConverterStatusListenerInterface {

    private var job: Job? = null

    private val mutableAudioConvertStatus by lazy {
        MutableSharedFlow<AudioConversion>()
    }

    /**
     * Getting Audio converted status
     * */
    override fun getAudioConvertStatus(): SharedFlow<AudioConversion> {
        return mutableAudioConvertStatus.asSharedFlow()
    }

    /**
     * Change audio convert status
     * @param audioConversion is Audio conversion status
     * */
    override fun changeAudioConvertStatus(audioConversion: AudioConversion) {
        job = CoroutineScope(Dispatchers.Main).launch {
            mutableAudioConvertStatus.emit(
                value = audioConversion
            )
        }
    }
}