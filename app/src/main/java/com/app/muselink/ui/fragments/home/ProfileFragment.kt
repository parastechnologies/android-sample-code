package com.app.muselink.ui.fragments.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.activities.profile.camera.CameraActivity
import com.app.muselink.ui.activities.profile.settings.SettingActivity
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.ui.fragments.profile.aboutme.AboutMeProfileFragment
import com.app.muselink.ui.fragments.profile.soundfileprofile.SoundFileProfileFragment
import com.app.muselink.util.intentComponent
import com.app.muselink.util.loadImageUser
import kotlinx.android.synthetic.main.fragment_profile.*
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class ProfileFragment : BaseFragment() {
    private var rootView: View? = null
    private var imgUser: ImageView? = null
    private val broadcast by lazy { BroadcastListener() }
    private val soundFileProfileFragment: SoundFileProfileFragment by lazy { SoundFileProfileFragment() }
    private val aboutMeProfileFragment: AboutMeProfileFragment by lazy { AboutMeProfileFragment() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        imgUser = rootView?.findViewById(R.id.imguser)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        loginToContinue.setOnClickListener {
            val signUpTypesBottomSheet = SignUpTypesBottomSheet()
            signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
            SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
        }
        btnSoundFile.setOnClickListener {
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            } else {
                showHideView(viewSoundFile, viewAboutMe)
                selectUnSelectTab(btnSoundFile, btnAboutMe)
                openFragment(soundFileProfileFragment)
            }
        }
        btnAboutMe.setOnClickListener {
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            } else {
                showHideView(viewAboutMe, viewSoundFile)
                selectUnSelectTab(btnAboutMe, btnSoundFile)
                openFragment(aboutMeProfileFragment)
            }
        }
        neuSettings.setOnClickListener {
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(requireActivity().supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            } else {
                requireActivity().intentComponent(SettingActivity::class.java, null)
            }
        }
        nmcCameraProfile.setOnClickListener {
            val intent = Intent(requireActivity(), CameraActivity::class.java)
            startActivity(intent)
        }
        btnSoundFile.performClick()
    }
    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.containerProfile, fragment)
        transaction.commit()
    }
    private fun showHideView(selectedView: View, unSelectView: View) {
        selectedView.visibility = View.VISIBLE
        unSelectView.visibility = View.GONE
    }
    private fun selectUnSelectTab(selectedCard: NeumorphCardView, unselectCard: NeumorphCardView) {
        selectedCard.setShapeType(ShapeType.PRESSED)
        unselectCard.setShapeType(ShapeType.FLAT)
    }
    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcast, IntentFilter().also { it.addAction("LOGIN") })
        super.onResume()
        (requireActivity() as HomeActivity).showHideSearchButton(false)
        if (SharedPrefs.getUserProfilePic().isNullOrEmpty().not()) {
            imguser?.loadImageUser(SharedPrefs.getUserProfilePic())
        }
    }
    override fun onStop() {
        try {
            LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcast)
        } catch (e: Exception) {
        }
        super.onStop()
        (requireActivity() as HomeActivity).showHideSearchButton(true)

    }

    inner class BroadcastListener : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "LOGIN") {
                loginToContinue.visibility = View.GONE
                btnSoundFile.performClick()
            }
        }
    }
}
