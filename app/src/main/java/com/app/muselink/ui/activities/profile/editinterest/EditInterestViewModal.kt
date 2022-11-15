package com.app.muselink.ui.activities.profile.editinterest

import android.app.Activity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.IntentConstant
import com.app.muselink.data.modals.responses.CommonResponse
import com.app.muselink.data.modals.responses.getInterest.GetInterestResponseData
import com.app.muselink.data.modals.responses.getInterest.GetInterestResponseModel
import com.app.muselink.model.responses.getInterest.InterestsDatum
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.databinding.ActivityEditInterestBinding
import com.app.muselink.ui.adapter.AdapterInterestSelection
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class EditInterestViewModal @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var enableButton = ObservableField<Boolean>()
    var showLoader = ObservableField<Boolean>()
    var updatedIds = ""
    val formErrors = ObservableArrayList<AppConstants.FormErrors>()
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    var selectedInterestCount = ObservableField<String>()
    var adapterPolygon: AdapterInterestSelection? = null
    var biniding: ViewBinding? = null
    var lifeCycle: LifecycleOwner? = null
    var listInterests = ArrayList<GetInterestResponseData>()
    var addedItem = ArrayList<InterestsDatum>()
    var listInterestsSelected = ArrayList<InterestsDatum>()


    private fun getIntentData() {
        enableButton.set(true)
        val bundle = activity.intent.extras
        if (bundle != null) {
            if (bundle.containsKey(IntentConstant.INTEREST_LIST)) {
                addedItem =
                    activity.intent.getSerializableExtra(IntentConstant.INTEREST_LIST) as ArrayList<InterestsDatum>
            }
        }
    }

    /**
     * Set Recycler View
     * */
    fun setRecycleView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        (biniding as ActivityEditInterestBinding).recycleCategoryInterest.layoutManager =
            linearLayoutManager
        adapterPolygon =
            AdapterInterestSelection(activity, listInterests, addRemoveSelectedItemListener)
        (biniding as ActivityEditInterestBinding).recycleCategoryInterest.setHasFixedSize(true)
        (biniding as ActivityEditInterestBinding).recycleCategoryInterest.isNestedScrollingEnabled =
            true
        (biniding as ActivityEditInterestBinding).recycleCategoryInterest.adapter = adapterPolygon
    }

    private val requestApiPersonalInterests = MutableLiveData<HashMap<String, Any>>()
    private val _getPersonalInterest = requestApiPersonalInterests.switchMap { requestApi ->
        repository.getEditInterests(requestApi)
    }

    /**
     * Get all Interest api
     * */
    private fun callApiGetInterests() {
        val request = HashMap<String, Any>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApiPersonalInterests.value = request
    }

    /**
     * GetPersonal Interest Functionality
     * */
    private val getPersonalInterestResponse: LiveData<Resource<GetInterestResponseModel>> =
        _getPersonalInterest

    /**
     * Get Personal Interest
     * */
    fun setupObserversPersonalInterest() {
        getIntentData()
        (biniding as ActivityEditInterestBinding).listFound = true
        getPersonalInterestResponse.observe(lifeCycle!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    (biniding as ActivityEditInterestBinding).showLoaderInterest = false
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data.isNullOrEmpty().not()) {
                                (biniding as ActivityEditInterestBinding).listFound = true
                                listInterests = it.data.data as ArrayList<GetInterestResponseData>
                                if (addedItem.size > 0) {
                                    for (addItemModel in addedItem) {
                                        for (i in 0 until listInterests.size) {
                                            for (j in 0 until listInterests[i].interestsData!!.size) {
                                                if (addItemModel.interestId == listInterests[i].interestsData!![j].interestId) {
                                                    listInterests[i].interestsData!![j].isSelected =
                                                        true
                                                    break
                                                }
                                            }
                                        }
                                    }
                                }
                                setRecycleView()
                            } else {
                                (biniding as ActivityEditInterestBinding).listFound = false
                            }
                        } else {
                            (biniding as ActivityEditInterestBinding).listFound = false
                            showToast(activity, it.data.message)
                        }
                    } else {
                        (biniding as ActivityEditInterestBinding).listFound = false
                        showToast(activity, it.message)
                    }
                }

                Resource.Status.ERROR -> {
                    (biniding as ActivityEditInterestBinding).showLoaderInterest = false
                    (biniding as ActivityEditInterestBinding).listFound = false
                }

                Resource.Status.LOADING -> {
                    (biniding as ActivityEditInterestBinding).showLoaderInterest = true
                }
            }
        })
        callApiGetInterests()
    }

    private val _updateInterest = requestApi.switchMap { requestApi ->
        repository.updateInterest(requestApi)
    }
    private val updateInterestResponse: LiveData<Resource<CommonResponse>> = _updateInterest

    /**
     * Update interest Api
     * */
    fun callApiUpdateInterests() {
        if (isFormValid()) {
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
            request[SyncConstants.APIParams.INTERESTID.value] = updatedIds
            requestApi.value = request
        }
    }

    /**
     * Update interest Observer
     * */
    fun setupObserversUpdateInterest() {
        updateInterestResponse.observe(lifeCycle!!, Observer {
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


    /**
     * Add and remove item Listener
     * */
    val addRemoveSelectedItemListener =
        object : AdapterInterestSelection.AdapterPolygonPressedNavigator {
            override fun onAdd(interestsDatum: InterestsDatum) {
                addedItem.add(interestsDatum)
            }

            override fun onRemove(id: String) {
                val index = addedItem.indexOfLast { it.interestId == id }
                if (index != -1) {
                    addedItem.removeAt(index)
                }
            }
        }

    /**
     * Validation to upload Data
     * */
    fun isFormValid(): Boolean {
        formErrors.clear()
        updatedIds = ""
        for (i in 0 until addedItem.size) {
            if ((addedItem.size) == (i + 1)) {
                updatedIds += addedItem[i].interestId
            } else {
                updatedIds = updatedIds + addedItem[i].interestId + ","
            }
        }
        if (updatedIds.isEmpty()) {
            formErrors.add(AppConstants.FormErrors.MISSING_INTERESTS)
        }
        return formErrors.isEmpty()
    }
}