package com.app.muselink.ui.fragments.home.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.data.modals.responses.getDescription.GetDescriptionResponseModel
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.bottomsheets.description.DescriptionBottomSheet
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.ui.fragments.home.viewpager.soundfile.VislualizerFragmentHome
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sound_file.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@AndroidEntryPoint
class SoundFileFragment(val dashBoardFragment: DashBoardFragment) : BaseFragment() {
    var rootView: View? = null
    private val requestApi = MutableLiveData<HashMap<String, String>>()

    @Inject
    lateinit var repository: ApiRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_sound_file, container, false)
        return rootView
    }

    private val description = requestApi.switchMap { requestApi ->
        repository.description(requestApi)
    }

    val changeEmailResponse: LiveData<Resource<GetDescriptionResponseModel>> = description

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vislualizerFragmentHome = VislualizerFragmentHome(dashBoardFragment)
//        descriptionFragmentHome = DescriptionFragment()
        defaultViews()
        setupObservers()
        openFragment(vislualizerFragmentHome!!)
        btnDescription.setOnClickListener {
            val audioFile: ArrayList<ModalAudioFile>? = SingletonInstances.listAudioFiles
            if (audioFile != null && audioFile.size > 0) {
                val map: HashMap<String, String> = HashMap()
                map["audioId"] = SingletonInstances.currentAudioFilePlay!!.audioId.toString()
                requestApi.value = map
            } else {
                Toast.makeText(requireContext(), "No audio found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *
     * Get description observer
     * */
    private fun setupObservers() {
        description.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    hideLoader()
                    if (it.data != null) {
                        if (it.data.status.equals("200")) {
                            vislualizerFragmentHome?.pauseSongIfPlay()
                            DescriptionBottomSheet(it.data.data).show(
                                (activity as AppCompatActivity).supportFragmentManager,
                                "Description"
                            )
                        } else {
                            showToast(requireActivity(), it.data.message)
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                }
                Resource.Status.ERROR -> {
                    hideLoader()
                    showToast(requireActivity(), it.message)
                }

                Resource.Status.LOADING -> {
                    showLoader()
                }
            }
        })
    }


    fun playNextSong() {
        vislualizerFragmentHome?.playNextSong()
    }

    fun repeatSong() {
        vislualizerFragmentHome?.repeatSong()
    }

    override fun onStop() {
        super.onStop()
        vislualizerFragmentHome?.onStop()
    }

    var vislualizerFragmentHome: VislualizerFragmentHome? = null
//    var descriptionFragmentHome: DescriptionFragment? = null
    fun intializePlayer() {
        vislualizerFragmentHome?.intializePlayer()
    }

    fun setToDefaultSettings() {
        defaultViews()
    }

    private fun defaultViews() {
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).setMarginMainView(0, 0, 0, 0)
            (requireActivity() as HomeActivity).showHideBottomBar(true)
            dashBoardFragment.showHideViewForDesc(true)
        }
    }

    override fun onPause() {
        super.onPause()
        defaultViews()
    }

    /*fun openDescriptionFragment() {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_from_bottom,
            R.anim.slide_in_up,
            R.anim.slide_from_bottom,
            R.anim.slide_in_up
        )
        transaction.replace(R.id.containerSoundFile, DescriptionFragment())
        transaction.commit()
    }*/

    fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.containerSoundFile, fragment)
        transaction.commit()
    }

    /*fun showHideView(selectedView: View,unSelectView:View){
        selectedView.visibility = View.VISIBLE
        unSelectView.visibility = View.GONE
    }*/

    /*fun selectUnSelectTab(selctedCard: NeumorphCardView,unselectCard: NeumorphCardView){
        selctedCard.setShapeType(ShapeType.PRESSED)
        unselectCard.setShapeType(ShapeType.FLAT)
    }*/

}