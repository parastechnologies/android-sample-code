package com.highenergymind.base

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.highenergymind.di.ApplicationClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel(context: Context) :
    AndroidViewModel(context as ApplicationClass) {
    fun getViewContext(): Context = getApplication<ApplicationClass>().applicationContext
    var job: Job?=null

    val map by lazy {
        HashMap<String, Any>()
    }
    var isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()

    fun showLoader(show: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (show) {
                isLoading.emit(true)
            } else {
                isLoading.emit(false)
            }
        }
    }

    abstract fun retry(type: String)
}