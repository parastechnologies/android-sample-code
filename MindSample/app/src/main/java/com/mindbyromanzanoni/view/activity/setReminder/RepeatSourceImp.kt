package com.mindbyromanzanoni.view.activity.setReminder

import android.content.Context
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.retrofit.ApiService
import com.mindbyromanzanoni.retrofit.NetworkErrorHandlerImpl
import com.mindbyromanzanoni.retrofit.responseToResourceFromServer
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
class RepeatSourceImp @Inject constructor(
    private val apiService: ApiService,
    private val networkErrorHandlerImpl: NetworkErrorHandlerImpl,
    @ApplicationContext val context: Context,
    private val sharedPrefs: SharedPrefs) {
    suspend fun executeAddReminderApi(param: HashMap<String,Any?>,apiType: String) = flow {
        try {
            emit(responseToResourceFromServer(
                    response = apiService.addReminderApi(param),
                    sharedPrefs = sharedPrefs,
                    context = context,
                    apiType = apiType))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(networkErrorHandlerImpl.getErrorMessage(e), apiType = apiType))
        }
    }
    suspend fun executeReminderListApi(apiType: String) = flow {
        try {
            emit(responseToResourceFromServer(
                    response = apiService.getReminderApi(),
                    sharedPrefs = sharedPrefs,
                    context = context,
                    apiType = apiType))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(networkErrorHandlerImpl.getErrorMessage(e), apiType = apiType))
        }
    }
    suspend fun executeDeleteReminderApi(hashMap: HashMap<String,Any?>,apiType: String) = flow {
        try {
            emit(responseToResourceFromServer(
                    response = apiService.deleteReminderApi(hashMap),
                    sharedPrefs = sharedPrefs,
                    context = context,
                    apiType = apiType))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(networkErrorHandlerImpl.getErrorMessage(e), apiType = apiType))
        }
    }
}
