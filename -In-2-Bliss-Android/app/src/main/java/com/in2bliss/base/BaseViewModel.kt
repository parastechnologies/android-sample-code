package com.in2bliss.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

abstract class BaseViewModel : ViewModel() {

    abstract fun retryApiRequest(apiName: String)

    fun networkCallIo(networkRequest : suspend () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            delay(100.milliseconds)
            networkRequest.invoke()
        }
    }
}