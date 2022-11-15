package com.app.muselink.ui.activities.uploadsection

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.app.muselink.data.modals.responses.GetRoleResponseModel
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource

class UploadMusicViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var getRole: LiveData<Resource<GetRoleResponseModel>> = repository.getRole()
}