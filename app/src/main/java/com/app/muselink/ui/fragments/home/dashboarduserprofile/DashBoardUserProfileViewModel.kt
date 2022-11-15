package com.app.muselink.ui.fragments.home.dashboarduserprofile

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.data.modals.responses.FavouriteUserRes
import com.app.muselink.data.modals.responses.GetUserListingRes
import com.app.muselink.data.modals.responses.UserData
import com.app.muselink.databinding.FragmentDashboardUserprofileBinding
import com.app.muselink.databinding.ItemUserDetailsBinding
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.bottomsheets.BottomSheetDirectMessage
import com.app.muselink.ui.bottomsheets.musiclinkpro.MuseLinkProBottomsheet
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.ui.fragments.profile.aboutme.AdapterGoals
import com.app.muselink.ui.fragments.profile.soundfileprofile.AdapterInterests
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.loadImageUser
import com.app.muselink.util.showToast
import soup.neumorphism.ShapeType

class DashBoardUserProfileViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {
    var binding: FragmentDashboardUserprofileBinding? = null
    var lifecycleOwner: LifecycleOwner? = null
    private val requestApi = MutableLiveData<HashMap<String, Any>>()
    private val requestApiFavourite = MutableLiveData<HashMap<String, Any>>()
    private val _favouriteUser = requestApiFavourite.switchMap { requestApi ->
        repository.favouriteUser(requestApi)
    }
    val favouriteUser: LiveData<Resource<FavouriteUserRes>> = _favouriteUser
    fun setObserverFavUser() {
        favouriteUser.observe(lifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            adapter?.getItemList()?.get(selectedPos)?.Favorite_Staus = 1
                            binding?.npmStar?.setShapeType(ShapeType.PRESSED)
                        } else {
                            showToast(activity, it.data.message)
                        }
                    } else {
                        showToast(activity, it.data?.message)
                    }
                }

                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(activity, it.data?.message)
                }

                Resource.Status.LOADING -> {
                    showLoader()
                }
            }

        })

    }

    private val _userListingProfile = requestApi.switchMap { requestApi ->
        repository.getUserListing(requestApi)
    }

    val userListingProfile: LiveData<Resource<GetUserListingRes>> = _userListingProfile

    var adapter: ProfilePagerAdapter? = null

    fun init() {
        adapter = ProfilePagerAdapter(binding!!)
        binding?.viewePageUsers?.adapter = adapter
        binding?.viewePageUsers?.offscreenPageLimit = 1
    }

    var selectedPos = 0

    fun addToFav() {
        val currentItem = binding?.viewePageUsers?.currentItem
        if (adapter?.getItemList()?.get(currentItem!!)?.Favorite_Staus == 0) {
            selectedPos = currentItem!!
            val request = HashMap<String, Any>()
            request[SyncConstants.APIParams.FROM_ID.value] = SharedPrefs.getUser().id.toString()
            request[SyncConstants.APIParams.TO_ID.value] =
                adapter?.getItemList()?.get(currentItem)?.id.toString()
            requestApiFavourite.value = request
        } else {
            showToast(activity, activity.getString(R.string.already_added_to_favourite))
        }
    }

    fun onclickFavourite() {
        if (!SharedPrefs.isUserLogin()) {
            val signUpTypesBottomSheet = SignUpTypesBottomSheet()
            signUpTypesBottomSheet.show(
                (activity as AppCompatActivity).supportFragmentManager,
                "AuthDialog"
            )
            SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
        } else {
            addToFav()
        }
    }

    fun onClickRefresh() {
        init()
        getUserList()
    }


    fun onClickDM() {
        if (!SharedPrefs.isUserLogin()) {
            val signUpTypesBottomSheet = SignUpTypesBottomSheet()
            signUpTypesBottomSheet.show((activity as AppCompatActivity).supportFragmentManager, "AuthDialog")
            SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
        } else {
            if(SharedPrefs.isUserLogin()) {
                if (SharedPrefs.subscriptionStatus()) {
                    val bundle = bundleOf(
                        AppConstants.MATCH_SCREEN to "",
                        AppConstants.receiverId to adapter?.getCurrentUserProfileDetails()?.id,
                        AppConstants.receiverName to adapter?.getCurrentUserProfileDetails()?.User_Name
                    )
                    val bottomSheetDirectMessageFragment = BottomSheetDirectMessage()
                    bottomSheetDirectMessageFragment.arguments = bundle
                    bottomSheetDirectMessageFragment.show(
                        (activity as AppCompatActivity).supportFragmentManager,
                        "BottomSheetDirectMessage"
                    )
                } else {
                    MuseLinkProBottomsheet(activity).show(
                        (activity as AppCompatActivity).supportFragmentManager,
                        "MuseLinkProBottomsheet"
                    )
                }
            }else{
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show((activity as AppCompatActivity).supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            }
        }
    }

    fun onClickNext() {
        binding?.viewePageUsers?.currentItem = binding?.viewePageUsers?.currentItem!!.plus(1)
    }

    fun setObserverUserListing() {
        userListingProfile.observe(lifecycleOwner!!, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {

                            if (it.data.subscriptionStatus != null) {
                                SharedPrefs.save(
                                    AppConstants.PREFS_SUBSCRIPTION_STATUS,
                                    it.data.subscriptionStatus
                                )
                            } else {
                                SharedPrefs.save(
                                    AppConstants.PREFS_SUBSCRIPTION_STATUS,
                                    0
                                )
                            }

                            if (it.data.data.isNullOrEmpty().not()) {
                                binding?.noDataFound = true
                                adapter?.addPost(it.data.data)
                            } else {
                                binding?.noDataFound = false
                            }
                        } else {
                            binding?.noDataFound = false
                            showToast(activity, it.data.message)
                        }
                    } else {
                        binding?.noDataFound = false
                        showToast(activity, it.data?.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    binding?.noDataFound = false
                    showToast(activity, it.data?.message)
                }
                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }

    fun getUserList() {
        val request = HashMap<String, Any>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApi.value = request
    }

    open inner class ProfilePagerAdapter(val bindingMain: FragmentDashboardUserprofileBinding) :
        PagerAdapter(), View.OnClickListener {

        lateinit var binding: ItemUserDetailsBinding
        private val itemList = ArrayList<UserData>()
        private var currentUser : UserData? = null

        fun getCurrentUserProfileDetails(): UserData?{
            return currentUser
        }

        fun addPost(list: List<UserData>) {
            itemList.addAll(list)
            notifyDataSetChanged()
        }

        fun getItemList(): ArrayList<UserData>? {
            return itemList
        }

        override fun getCount(): Int {
            return itemList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        //for set the height of the viewpager according to the view
        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            currentUser = itemList[position]
            if (itemList[position].Favorite_Staus == 0) {
                this.bindingMain.npmStar.setShapeType(ShapeType.FLAT)
            } else {
                this.bindingMain.npmStar.setShapeType(ShapeType.PRESSED)
            }
//            if (position != mCurrentPosition && container is WrappingViewPager) {
//                val fragment = `object` as ScrollView
//                if (fragment != null) {
//                    mCurrentPosition = position
//                    container.measureCurrentView(fragment)
//                }
//            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(activity)
            binding = ItemUserDetailsBinding.inflate(inflater, container, false)
            bindUser(position, binding)
            container.addView(binding.root)
            return binding.root
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun onClick(v: View?) {

        }

        var adapterInterests: AdapterInterests? = null
        var adapterGoals: AdapterGoals? = null

        fun bindUser(position: Int, binding: ItemUserDetailsBinding) {

            binding.imgUser.loadImageUser(itemList[position].Profile_Image)
            binding.tvUserName.text = itemList[position].User_Name

            if (itemList[position].Biography.isNullOrEmpty().not()) {
                binding.bioFound = true
                binding.tvBio.text = itemList[position].Biography
            } else {
                binding.bioFound = false
            }
            if (itemList[position].Personal_Interest!!.size <= 0) {
                binding.tvInterest.visibility = View.GONE
            }

            if (itemList[position].Career_Goals!!.size <= 0) {
                binding.tvGoal.visibility = View.GONE
            }

            if (itemList[position].Biography.isNullOrEmpty()) {
                binding.tvBiography.visibility = View.GONE
                binding.ncvBiography.visibility = View.GONE
            }

            if (itemList[position].Personal_Interest.isNullOrEmpty().not()) {
                val spanCountGoal = if (itemList[position].Personal_Interest!!.size > 3) 2 else 1
                binding.listFound = true
                val mLayoutManager =
                    StaggeredGridLayoutManager(spanCountGoal, StaggeredGridLayoutManager.HORIZONTAL)
                binding.recyclePersonalInterest.layoutManager =
                    mLayoutManager
                adapterInterests = AdapterInterests(activity, itemList[position].Personal_Interest)
                binding.recyclePersonalInterest.adapter =
                    adapterInterests
            } else {
                binding.listFound = false
            }
            if (itemList[position].Career_Goals.isNullOrEmpty().not()) {
                val spanCountGoal = if (itemList[position].Career_Goals!!.size > 3) 2 else 1
                binding.listFoundGoal = true
                val mLayoutManager =
                    StaggeredGridLayoutManager(
                        spanCountGoal,
                        StaggeredGridLayoutManager.HORIZONTAL
                    )
                binding.recycleGoals.layoutManager =
                    mLayoutManager
                adapterGoals = AdapterGoals(activity, itemList[position].Career_Goals!!)
                binding.recycleGoals.adapter = adapterGoals
            } else {
                binding.listFoundGoal = false
            }
        }
    }
}