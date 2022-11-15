package com.app.muselink.ui.activities.uploadsection

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.app.muselink.model.responses.GetGoalsResponseModel
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource

class UploadViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var getGoals: LiveData<Resource<GetGoalsResponseModel>> = repository.getGoals()
}