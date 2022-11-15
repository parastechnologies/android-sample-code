package com.app.muselink.ui.fragments.profile.aboutme

import android.app.Activity
import android.content.Intent
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.IntentConstant
import com.app.muselink.data.modals.responses.GetInterestsGoalsRes
import com.app.muselink.databinding.FragmentProfileAboutmeBinding
import com.app.muselink.model.responses.GetGoalsData
import com.app.muselink.model.responses.getInterest.InterestsDatum
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.activities.profile.editbiography.EditBiography_Activity
import com.app.muselink.ui.activities.profile.editgoals.EditGoalsActivity
import com.app.muselink.ui.activities.profile.editinterest.EditInterestActivity
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.fragments.profile.soundfileprofile.AdapterInterests
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class AboutMeViewModal @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    var viewLifecycleOwner: LifecycleOwner? = null
    var adapterInterests: AdapterInterests? = null
    var adapterGoals: AdapterGoals? = null
    var binding: ViewBinding? = null
    private var listInterests = ArrayList<InterestsDatum>()
    var listGoals = ArrayList<GetGoalsData>()
    var biography = ObservableField<String>()
    var biographyFound = ObservableField<Boolean>()
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    private val _getPersonalInterest = requestApi.switchMap { requestApi ->
        repository.getpersonalInterests(requestApi)
    }

    private val getPersonalInterestResponse: LiveData<Resource<GetInterestsGoalsRes>> =
        _getPersonalInterest

    /**
     * Interest api call
     * */
    fun callApiGetInterests() {
        val request = HashMap<String, Any>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApi.value = request
    }

    /**
     * RecyclerView Interest
     * */
    fun initRecyclerViewInterests(spanCountInterest: Int) {
        (binding as FragmentProfileAboutmeBinding).listFound = true
        val mLayoutManager =
            StaggeredGridLayoutManager(spanCountInterest, StaggeredGridLayoutManager.HORIZONTAL)
        (binding as FragmentProfileAboutmeBinding).recyclePersonalInterest.layoutManager =
            mLayoutManager
        adapterInterests = AdapterInterests(activity, listInterests)
        (binding as FragmentProfileAboutmeBinding).recyclePersonalInterest.adapter =
            adapterInterests
    }

    /**
     * RecyclerView Goals
     * */
    fun initRecyclerViewGoals(spanCountGoals: Int) {
        (binding as FragmentProfileAboutmeBinding).listFoundGoal = true
        val mLayoutManager =
            StaggeredGridLayoutManager(spanCountGoals, StaggeredGridLayoutManager.HORIZONTAL)
        (binding as FragmentProfileAboutmeBinding).recycleGoals.layoutManager = mLayoutManager
        adapterGoals = AdapterGoals(activity, listGoals)
        (binding as FragmentProfileAboutmeBinding).recycleGoals.adapter = adapterGoals
    }

    /**
     * Interest Observer
     * */
    fun setupObserversPersonalInterest() {
        getPersonalInterestResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    (binding as FragmentProfileAboutmeBinding).showLoaderInterest = false
                    (binding as FragmentProfileAboutmeBinding).showLoaderGoals = false
                    (binding as FragmentProfileAboutmeBinding).showLoaderBio = false
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data != null) {
                                if (it.data.data!!.biography.isNullOrEmpty().not()) {
                                    biographyFound.set(true)
                                    biography.set(it.data.data!!.biography)
                                    (binding as FragmentProfileAboutmeBinding).listFoundBio = true
                                } else {
                                    biographyFound.set(false)
                                    (binding as FragmentProfileAboutmeBinding).listFoundBio = false
                                }
                                listInterests = it.data.data!!.personalInterest!!
                                listGoals = it.data.data!!.personalCareerGoal!!

                                (binding as FragmentProfileAboutmeBinding).listFound =
                                    listInterests.size > 0

                                (binding as FragmentProfileAboutmeBinding).listFoundGoal =
                                    listGoals.size > 0

                                val spanCountGoal = if (listGoals.size > 3) 2 else 1
                                val spanCountInterest = if (listInterests.size > 3) 2 else 1
                                initRecyclerViewInterests(spanCountInterest)
                                initRecyclerViewGoals(spanCountGoal)
                                adapterInterests?.updateInterestList(listInterests)
                                adapterGoals?.updateGoalsList(listGoals)
                                if (listInterests.size <= 0) {
                                    (binding as FragmentProfileAboutmeBinding).listFound = false
                                }
                                if (listGoals.size <= 0) {
                                    (binding as FragmentProfileAboutmeBinding).listFoundGoal = false
                                }
                            } else {
                                (binding as FragmentProfileAboutmeBinding).listFound = false
                                (binding as FragmentProfileAboutmeBinding).listFoundGoal = false

                            }
                        } else {
                            (binding as FragmentProfileAboutmeBinding).listFound = false
                            (binding as FragmentProfileAboutmeBinding).listFoundGoal = false
                            (binding as FragmentProfileAboutmeBinding).listFoundBio = false
                            showToast(activity, it.data.message)
                        }
                    } else {
                        (binding as FragmentProfileAboutmeBinding).listFound = false
                        (binding as FragmentProfileAboutmeBinding).listFoundGoal = false
                        (binding as FragmentProfileAboutmeBinding).listFoundBio = false
                        showToast(activity, it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    (binding as FragmentProfileAboutmeBinding).showLoaderInterest = false
                    (binding as FragmentProfileAboutmeBinding).showLoaderGoals = false
                    (binding as FragmentProfileAboutmeBinding).showLoaderBio = false
                    (binding as FragmentProfileAboutmeBinding).listFound = false
                    (binding as FragmentProfileAboutmeBinding).listFoundGoal = false
                    (binding as FragmentProfileAboutmeBinding).listFoundBio = false
                }
                Resource.Status.LOADING -> {
                    (binding as FragmentProfileAboutmeBinding).showLoaderInterest = true
                    (binding as FragmentProfileAboutmeBinding).showLoaderGoals = true
                    (binding as FragmentProfileAboutmeBinding).showLoaderBio = true
                }
            }
        })
    }

    fun onClickEditGoals() {
        val intent = Intent(activity, EditGoalsActivity::class.java)
        intent.putExtra(IntentConstant.GOALS_LIST, listGoals)
        activity.startActivity(intent)
    }

    fun onClickEditBiography() {
        val intent = Intent(activity, EditBiography_Activity::class.java)
        if (biography.get().isNullOrEmpty().not()) {
            intent.putExtra(IntentConstant.BIOGRAPHY, biography.get().toString())
        } else {
            intent.putExtra(IntentConstant.BIOGRAPHY, "")
        }
        activity.startActivity(intent)
    }

    fun onClickEditInterest() {
        val intent = Intent(activity, EditInterestActivity::class.java)
        intent.putExtra(IntentConstant.INTEREST_LIST, listInterests)
        activity.startActivity(intent)
    }
}