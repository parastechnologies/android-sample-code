package com.app.muselink.ui.activities.settings.blockaccount

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.BlockAccountRes
import com.app.muselink.data.modals.responses.UnBlockRes
import com.app.muselink.model.ui.BlockAccountDetail
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.databinding.ActivityBlockedAccountBinding
import com.app.muselink.ui.adapter.settings.AdapterBlockAccounts
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.showToast

class BlockAccountViewmodel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    private var adapterBlockAccounts: AdapterBlockAccounts? = null

    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null
    var listBlockAccounts = ArrayList<BlockAccountDetail>()


    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    private val requestApiUnBlock = MutableLiveData<HashMap<String, Any>>()

    private val _getBlockAccounts = requestApi.switchMap { requestApi ->
        repository.getBlockAccountDetails(requestApi)
    }

    val getBlockAccountResponse: LiveData<Resource<BlockAccountRes>> = _getBlockAccounts

    private val _unBlockAccounts = requestApiUnBlock.switchMap { requestApi ->
        repository.unBlockAccountDetails(requestApi)
    }

    val unBlockAccountResponse: LiveData<Resource<UnBlockRes>> = _unBlockAccounts


    fun setupObserversUnBlock() {

        unBlockAccountResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            listBlockAccounts.removeAt(selectedPos)
                            adapterBlockAccounts?.updateList(listBlockAccounts)
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.message)
                    }
                }

                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(activity, it.message)
                }

                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })

    }

    fun callApiUnblockAccounts(id:String) {
        val requets = HashMap<String, Any>()
        requets.put(SyncConstants.APIParams.BLOCK_ACCOUNT_ID.value, id)
//        requets.put(SyncConstants.APIParams.USER_ID.value, "1")
        requestApiUnBlock.value = requets
    }


    fun callApiGetBlockAccounts() {
        val requets = HashMap<String, Any>()
        requets.put(SyncConstants.APIParams.USER_ID.value, SharedPrefs.getUser().id.toString())
//        requets.put(SyncConstants.APIParams.USER_ID.value, "1")
        requestApi.value = requets
    }

    var selectedPos = 0

    fun initRecyclerView() {
        (binding as ActivityBlockedAccountBinding).listFound = true
        val linearLayoutManager = LinearLayoutManager(activity)
        (binding as ActivityBlockedAccountBinding).rvBlockedAccounts?.layoutManager = linearLayoutManager
        adapterBlockAccounts = AdapterBlockAccounts(activity,listBlockAccounts,object : AdapterBlockAccounts.AdapterBlockAccountsNavigator{
            override fun onClickUnBloc(position: Int, blockAccountDetail: BlockAccountDetail) {
                selectedPos = position
                callApiUnblockAccounts(blockAccountDetail.BlockedAccountId.toString())
            }
        })
        (binding as ActivityBlockedAccountBinding).rvBlockedAccounts.adapter = adapterBlockAccounts
    }

    fun setupObservers() {

        getBlockAccountResponse?.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data!!.isSuccess()) {
                            if(it.data.data.isNullOrEmpty().not()){
                                (binding as ActivityBlockedAccountBinding).listFound = true
                                listBlockAccounts = it.data.data!!
                                adapterBlockAccounts?.updateList(listBlockAccounts)
                            }else{
                                (binding as ActivityBlockedAccountBinding).listFound = false
                            }
                        } else {
                            (binding as ActivityBlockedAccountBinding).listFound = false
                            showToast(activity, it.data.message)
                        }
                    } else {
                        (binding as ActivityBlockedAccountBinding).listFound = false
                        showToast(activity, it.message)
                    }
                }

                Resource.Status.ERROR -> {
                    (binding as ActivityBlockedAccountBinding).listFound = false
                    hideLoader()
                    showToast(activity, it.message)
                }

                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })

    }

}