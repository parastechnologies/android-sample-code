package com.app.muselink.ui.fragments.profile.soundfileprofile

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.data.modals.responses.CommonResponse
import com.app.muselink.data.modals.responses.GetSoundFileProfileRes
import com.app.muselink.databinding.FragmentSoundfileProfileBinding
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.adapter.Soundfile_Adapter
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.ui.bottomsheets.ProfileScreenVisualizerBottomsheet
import com.app.muselink.ui.bottomsheets.SongPlayProfileBottomsheet
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.copyToclipBoard
import com.app.muselink.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SoundFileProfileViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {

    private var soundFileAdapter: Soundfile_Adapter? = null
    var listSoundFiles = ArrayList<ModalAudioFile>()
    var viewLifecycleOwner: LifecycleOwner? = null
    var binding: ViewBinding? = null

    private val requestApi = MutableLiveData<HashMap<String, String>>()

    private val _getSoundFiles = requestApi.switchMap { requestApi ->
        repository.getSoundFiles(requestApi)
    }

    private val soundFileProfileResponse: LiveData<Resource<GetSoundFileProfileRes>> = _getSoundFiles


    private val requestApiDeleteAudio = MutableLiveData<HashMap<String, String>>()

    private val _deleteAudio = requestApiDeleteAudio.switchMap { requestApi ->
        repository.deleteAudio(requestApi)
    }
    private val deleteAudioResponse: LiveData<Resource<CommonResponse>> =
        _deleteAudio

    private val requestApiChnageSattus = MutableLiveData<HashMap<String, String>>()

    private val _chnageSattusAudio = requestApiChnageSattus.switchMap { requestApi ->
        repository.chnageNotificationSatusAudio(requestApi)
    }
    private val chnageSattusAudioResponse: LiveData<Resource<CommonResponse>> = _chnageSattusAudio


    var selectedPos = 0
    var IsSelected = false

    /**
     * On Song Click Listener
     * */
    private var itemClickListener = object : Soundfile_Adapter.ItemClickListener {
        override fun onItemClick(position: Int) {
            SongPlayProfileBottomsheet(activity,listSoundFiles[position]).show((activity as AppCompatActivity).supportFragmentManager, "SongPlay")
        }
        override fun onClickMoreInfo(position: Int) {
            selectedPos = position
            ProfileScreenVisualizerBottomsheet(activity,profileScreenVisualizerBottomsheetNavigator,listSoundFiles[position]).show((activity as AppCompatActivity).supportFragmentManager, "ForgotPassword")
        }
    }
    val profileScreenVisualizerBottomsheetNavigator = object : ProfileScreenVisualizerBottomsheet.ProfileScreenVisualizerBottomsheetNavigator {

        override fun onClickRemove() {
            IsSelected = true
            callApiGetDeleteAudio(listSoundFiles[selectedPos].audioId.toString())
        }

        override fun onClickCopy() {
            val url = if (listSoundFiles[selectedPos].fullAudio.toString().contains("http")) {
                listSoundFiles[selectedPos].fullAudio.toString()
            } else {
                SyncConstants.AUDIO_FILE_FULL + listSoundFiles[selectedPos].fullAudio
            }
            activity.copyToclipBoard(url)
            Toast.makeText(activity, "Copied To Clipboard", Toast.LENGTH_SHORT).show()
        }

        override fun onClickShareTo() {
            val url = if (listSoundFiles[selectedPos].fullAudio.toString().contains("http")) {
                listSoundFiles[selectedPos].fullAudio.toString()
            } else {
                SyncConstants.AUDIO_FILE_FULL + listSoundFiles[selectedPos].fullAudio
            }
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                url
            )
            sendIntent.type = "text/plain"
            activity.startActivity(sendIntent)
        }

        override fun onClickNotificationOnOff() {
            IsSelected = true
            var notificationStatus = "0"
            if (listSoundFiles[selectedPos].Notification_Status.isNullOrEmpty().not()) {
                if (listSoundFiles[selectedPos].Notification_Status.equals("1")) {
                    notificationStatus = "0"
                } else {
                    notificationStatus = "1"
                }
                callApiChnageStatus(notificationStatus,listSoundFiles[selectedPos].audioId.toString())
            } else {
                notificationStatus = "1"
                callApiChnageStatus(
                    notificationStatus,
                    listSoundFiles[selectedPos].audioId.toString()
                )
            }
        }
    }
    /**
     *Init RecyclerView
     **/
    fun initRecyclerView() {
        (binding as FragmentSoundfileProfileBinding).listFound = true
        val linearLayoutManager = LinearLayoutManager(activity)
        (binding as FragmentSoundfileProfileBinding).recycleSoundFileProfile.layoutManager = linearLayoutManager
        soundFileAdapter = Soundfile_Adapter(activity, itemClickListener, listSoundFiles)
        (binding as FragmentSoundfileProfileBinding).recycleSoundFileProfile.adapter = soundFileAdapter
        (binding as FragmentSoundfileProfileBinding).uploadAFile.setOnClickListener {
        //UploadAudioBottomSheet().show((activity as AppCompatActivity).supportFragmentManager,"UploadAudioBottomSheet" )
            (activity as HomeActivity).openUploadFragment()
        }
    }
    fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, fragment)
        transaction.commit()
    }
    fun callApiGetDeleteAudio(audioId: String) {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.AUDIO_ID.value] = audioId
        requestApiDeleteAudio.value = request
    }


    fun callApiChnageStatus(status: String, audioId: String) {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.AUDIO_ID.value] = audioId
        request[SyncConstants.APIParams.STATUS.value] = status.toString()
        requestApiChnageSattus.value = request
    }

    /**
     * Call api for Sound list
     * */
    fun callApiGetSoundFiles() {
        val request = HashMap<String, String>()
        request[SyncConstants.APIParams.USER_ID.value] = SharedPrefs.getUser().id.toString()
        requestApi.value = request
    }

    fun setupObserversDeletAudio() {
        deleteAudioResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (IsSelected) {
                                IsSelected = false
                                listSoundFiles.removeAt(selectedPos)
                                soundFileAdapter?.updateList(listSoundFiles)
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                }
                Resource.Status.LOADING -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        showLoader()
                    }
                }
            }
        })
    }

    fun setupObserversChnageStatus() {
        chnageSattusAudioResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (IsSelected) {
                                IsSelected = false
                                if (listSoundFiles[selectedPos].Notification_Status.isNullOrEmpty()
                                        .not()
                                ) {
                                    if (listSoundFiles[selectedPos].Notification_Status.equals("1")) {
                                        listSoundFiles[selectedPos].Notification_Status = "0"
                                    } else {
                                        listSoundFiles[selectedPos].Notification_Status = "1"
                                    }
                                } else {
                                    listSoundFiles[selectedPos].Notification_Status = "1"
                                }
                                soundFileAdapter?.updateList(listSoundFiles)
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                }
                Resource.Status.LOADING -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        showLoader()
                    }
                }
            }
        })
    }

    /**
     * Response Observer
     * */
    fun setupObserversSoundFileProfile() {
        soundFileProfileResponse.observe(viewLifecycleOwner!!, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
                            if (it.data.data.isNullOrEmpty().not()) {
                                (binding as FragmentSoundfileProfileBinding).listFound = true
                                listSoundFiles = it.data.data!!
                                soundFileAdapter?.updateList(listSoundFiles)
                            } else {
                                (binding as FragmentSoundfileProfileBinding).listFound = false
                            }
                        } else {
                            (binding as FragmentSoundfileProfileBinding).listFound = false
                            showToast(activity, it.data.message)
                        }
                    } else {
                        (binding as FragmentSoundfileProfileBinding).listFound = false
                        showToast(activity, it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    (binding as FragmentSoundfileProfileBinding).listFound = false
                }
                Resource.Status.LOADING -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        showLoader()
                    }
                }
            }
        })
    }
}