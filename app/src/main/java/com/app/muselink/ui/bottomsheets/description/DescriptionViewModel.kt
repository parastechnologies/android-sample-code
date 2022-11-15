package com.app.muselink.ui.bottomsheets.description

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel

class DescriptionViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {}