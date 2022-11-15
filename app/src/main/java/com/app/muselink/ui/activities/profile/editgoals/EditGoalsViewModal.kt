package com.app.muselink.ui.activities.profile.editgoals

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.IntentConstant
import com.app.muselink.data.modals.responses.CommonResponse
import com.app.muselink.model.responses.GetGoalsData
import com.app.muselink.databinding.ActivityEditGoalsBinding
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.adapter.AdapterGoalSelection
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class EditGoalsViewModal @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var enableButton = ObservableField<Boolean>()
    var showLoader = ObservableField<Boolean>()
    var updatedIds = StringBuilder()
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    var adapterPolygon: AdapterGoalSelection? = null
    var biniding: ViewBinding? = null
    var lifeCycle: LifecycleOwner? = null
    var listGoals = ArrayList<GetGoalsData>()
    var listGoalsSelected = ArrayList<GetGoalsData>()
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    private val _updateGoals = requestApi.switchMap { requestApi ->
        repository.updatepersonalCareerGoals(requestApi)
    }
    val updateGoalsResponse: LiveData<Resource<CommonResponse>> = _updateGoals
    fun setupObserversUpdateGoals() {
        updateGoalsResponse.observe(lifeCycle!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    showLoader.set(false)
                    enableButton.set(true)
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            showToast(activity, activity.getString(R.string.updated_successfully))
                            activity.finish()
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    showLoader.set(false)
                    enableButton.set(true)
                }

                Resource.Status.LOADING -> {
                    showLoader.set(true)
                    enableButton.set(false)
                }
            }
        })
    }
    fun isFormValid(): Boolean {
        formErrors.clear()
        updatedIds = StringBuilder("")
        val iterator = listGoals.iterator()
        iterator.forEach {
            if (it.IsSelected){
            updatedIds.append(it.Goal_Id)
            if (iterator.hasNext()) {
                    updatedIds.append(",")
                }
            }
        }
        if (updatedIds.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_GOALS)
        }
        return formErrors.isEmpty()
    }
    fun callApiUpdateGoals() {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
            request[SyncConstants.APIParams.GOALID.value] = updatedIds
            requestApi.value = request
        }
    }
    private fun callApiGetGoals() {
        val request = HashMap<String, Any>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApiGoals.value = request
    }

    private val requestApiGoals = MutableLiveData<HashMap<String, Any>>()
    private val getGoalsResponse = requestApiGoals.switchMap { requestApi ->
        repository.getGoals(requestApi)
    }

    fun setupObserversGoals() {
        getGoalsResponse.observe(lifeCycle!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    (biniding as ActivityEditGoalsBinding).showLoaderGoals = false
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data.isNullOrEmpty().not()) {
                                (biniding as ActivityEditGoalsBinding).listFoundGoal = true
                                listGoals = it.data.data!!
                                if (listGoalsSelected.isNullOrEmpty().not()) {
                                    for (i in 0 until listGoalsSelected.size) {
                                        for (j in 0 until listGoals.size) {
                                            if (listGoalsSelected[i].Goal_Id!! == listGoals[j].Goal_Id) {
                                                listGoals[j].IsSelected = true
                                                break
                                            }
                                        }
                                    }
                                }
                                adapterPolygon?.updateGoalsList(listGoals)
                            } else {
                                (biniding as ActivityEditGoalsBinding).listFoundGoal = false
                            }
                        } else {
                            (biniding as ActivityEditGoalsBinding).listFoundGoal = false
                            showToast(activity, it.data.message)
                        }
                    } else {
                        (biniding as ActivityEditGoalsBinding).listFoundGoal = false
                        showToast(activity, it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    (biniding as ActivityEditGoalsBinding).showLoaderGoals = false
                    (biniding as ActivityEditGoalsBinding).listFoundGoal = false
                }
                Resource.Status.LOADING -> {
                    (biniding as ActivityEditGoalsBinding).showLoaderGoals = true
                }
            }
        })
    }

    private fun getIntentData() {
        enableButton.set(true)
        val bundle = activity.intent.extras
        if (bundle != null) {
            if (bundle.containsKey(IntentConstant.GOALS_LIST)) {
                listGoalsSelected = activity.intent.getSerializableExtra(IntentConstant.GOALS_LIST) as ArrayList<GetGoalsData>
            }
        }
    }
    val adapterPolygonPressedNavigator =
        object : AdapterGoalSelection.AdapterGoalPressedNavigator {
            override fun onClickCategory(position: Int) {
                listGoals[position].IsSelected = !listGoals[position].IsSelected
            }
        }

    fun setRecycleView() {
        getIntentData()
        val linearLayoutManager = GridLayoutManager(activity, 2)
        (biniding as ActivityEditGoalsBinding).recycleGoals.layoutManager =
            linearLayoutManager
        adapterPolygon = AdapterGoalSelection(adapterPolygonPressedNavigator)
        (biniding as ActivityEditGoalsBinding).recycleGoals.adapter = adapterPolygon
        callApiGetGoals()
    }


}