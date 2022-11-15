package com.app.muselink.ui.fragments.home

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.os.bundleOf
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.IntentConstant
import com.app.muselink.constants.RequestCodeConstants
import com.app.muselink.helpers.PermissionHelper
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.activities.uploadsection.UploadMusicActivity
import com.app.muselink.ui.bottomsheets.BottomsheetAudioRecording
import com.app.muselink.ui.bottomsheets.musiclinkpro.MuseLinkProBottomsheet
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.ui.fragments.home.dashboard.DashboardViewModel.Companion.NON_PREMIUM_MAX_UPLOADED_COUNTS
import com.app.muselink.ui.fragments.home.dashboard.DashboardViewModel.Companion.PREMIUM_MAX_UPLOADED_COUNTS
import com.app.muselink.ui.fragments.home.dashboard.DashboardViewModel.Companion.UPLOADED_AUDIO_COUNT
import com.app.muselink.util.FileUtils
import com.app.muselink.util.showToast
import kotlinx.android.synthetic.main.fragment_upload.*
import java.io.File
import java.io.FileOutputStream

class UploadFragment : BaseFragment(), View.OnClickListener,
    PermissionHelper.OnRequestPermissionsResult {
    private var rootView: View? = null
    private var permissionsRequest: PermissionHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsRequest = PermissionHelper(requireActivity())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_upload, container, false)
        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        animateUploadIcon(0.0f, 0.0f)
    }
    private fun setListeners() {
        llCamera.setOnClickListener(this)
        llLibrary.setOnClickListener(this)
        llSoundCloud.setOnClickListener(this)
        llNavigate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llCamera -> {
                if (!SharedPrefs.isUserLogin()) {
                    val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                    signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
                    SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                     //   if (!Environment.isExternalStorageManager()){
                        if (!Environment.isExternalStorageManager()){
                            val intent = Intent()
                            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                            return
                        }
                    }
                    if (SharedPrefs.subscriptionStatus()) {
                        if (PREMIUM_MAX_UPLOADED_COUNTS-UPLOADED_AUDIO_COUNT==0 ){
                            showToast(requireActivity(),"Your Audio upload limit finish")
                        }else{
                            if (permissionsRequest?.checkPermissionStorage()!!) {
                                val audioRecordBottomSheet = BottomsheetAudioRecording(requireActivity())
                                audioRecordBottomSheet.show(childFragmentManager,"")
                            } else {
                                permissionsRequest?.checkAndRequestStoragePermission(this)
                            }
                        }
                    }else{
                        if (NON_PREMIUM_MAX_UPLOADED_COUNTS-UPLOADED_AUDIO_COUNT==0){
                            MuseLinkProBottomsheet(requireActivity()).show(childFragmentManager, "MuseLinkProBottomSheet")
                        }else{
                            if (permissionsRequest?.checkPermissionStorage()!!) {
                                val audioRecordBottomSheet = BottomsheetAudioRecording(requireActivity())
                                audioRecordBottomSheet.show(childFragmentManager,"")
                            } else {
                                permissionsRequest?.checkAndRequestStoragePermission(this)
                            }
                        }

                    }
                }
            }
            R.id.llSoundCloud -> {
                openBottomSheet(!SharedPrefs.isUserLogin())
            }
            R.id.llLibrary -> {
                if (!SharedPrefs.isUserLogin()) {
                    val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                    signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
                    SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()){
                                val intent = Intent()
                                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                                intent.data = uri
                                startActivity(intent)
                                return
                            }
                        }
                    if (SharedPrefs.subscriptionStatus()) {
                        if (PREMIUM_MAX_UPLOADED_COUNTS-UPLOADED_AUDIO_COUNT==0 ){
                            showToast(requireActivity(),"Your Audio upload limit finish")
                        }else{
                            if (permissionsRequest?.checkPermissionStorage()!!) {
                                chooseAudioFile()
                            } else {
                                permissionsRequest?.checkAndRequestStoragePermission(this)
                            }
                        }
                    } else {
                        if (NON_PREMIUM_MAX_UPLOADED_COUNTS-UPLOADED_AUDIO_COUNT==0){
                            MuseLinkProBottomsheet(requireActivity()).show(childFragmentManager, "MuseLinkProBottomSheet")
                        }else{
                            if (permissionsRequest?.checkPermissionStorage()!!) {
                                chooseAudioFile()
                            } else {
                                permissionsRequest?.checkAndRequestStoragePermission(this)
                            }
                        }
                    }
                }
            }
            R.id.llNavigate -> {
                openBottomSheet(!SharedPrefs.isUserLogin())
            }
        }
    }

    fun onRequestPermissions(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        permissionsRequest?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsGranted() {
        chooseAudioFile()
    }

    override fun onPermissionsDenied() {
        permissionsRequest?.checkAndRequestStoragePermission(this)
    }

    @Suppress("DEPRECATION")
    private fun chooseAudioFile() {
        val intentUpload = Intent()
        intentUpload.type = "audio/*"
        intentUpload.action = Intent.ACTION_GET_CONTENT
        intentUpload.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intentUpload, RequestCodeConstants.REQUEST_CODE_SELECT_AUDIO_FILE)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCodeConstants.REQUEST_CODE_SELECT_AUDIO_FILE) {
                var path: String? = ""
                val uri: Uri? = data?.data
                val uriString = uri?.toString()
                var displayName = ""
                if (uriString!!.contains("msf:") || uriString.replace("%3A", ":").contains("msf:")) {
                    val cursor: Cursor?
                    try {
                        cursor =
                            requireActivity().contentResolver.query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                        val fileByte =
                            (requireActivity().contentResolver?.openInputStream(uri))?.readBytes()
                        val originFileAnd10 = File(
                            requireActivity().getExternalFilesDir(null),
                            displayName
                        )
                        path = originFileAnd10.path
                        try {
                            val fos = FileOutputStream(path)
                            fos.write(fileByte)
                            fos.close()
                            cursor?.close()
                        } catch (e: Exception) {
                        }
                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    path = FileUtils.getPath(requireContext(), uri)
                }
                val bundle = bundleOf(IntentConstant.MusicPath to path,IntentConstant.hasRecordedSoundFile to false)
                val intent = Intent(requireActivity(), UploadMusicActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }
    private fun openBottomSheet(isOpen: Boolean) {
        if (isOpen) {
            val signUpTypesBottomSheet = SignUpTypesBottomSheet()
            signUpTypesBottomSheet.show(childFragmentManager, "AuthDialog")
            SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
        } else {
            if (permissionsRequest?.checkPermissionStorage()!!) {
                chooseAudioFile()
            } else {
                permissionsRequest?.checkAndRequestStoragePermission(this)
            }
//            val audioRecordBottomSheet = BottomsheetAudioRecording(requireActivity())
//            audioRecordBottomSheet.show(childFragmentManager,"")
        }
    }
    private fun animateUploadIcon(startPosX: Float, startPosY: Float) {
        val animation = TranslateAnimation(
            startPosX, -20.0f,
            startPosY, 20.0f)
        animation.duration = 700
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val animation1 = TranslateAnimation(
                    -20.0f, -20.0f,
                    20.0f, -20.0f
                )
                animation1.duration = 700
                animation1.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val animation2 = TranslateAnimation(
                            -20.0f, 20.0f,
                            -20.0f, -20.0f
                        )
                        animation2.duration = 700
                        animation2.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {}
                            override fun onAnimationEnd(animation: Animation?) {
                                val animation3 = TranslateAnimation(
                                    20.0f, 20.0f,
                                    -20.0f, 20.0f
                                )
                                animation3.duration = 700
                                animation3.setAnimationListener(object :
                                    Animation.AnimationListener {
                                    override fun onAnimationRepeat(animation: Animation?) {

                                    }

                                    override fun onAnimationEnd(animation: Animation?) {
                                        animateUploadIcon(20.0f, 20.0f)
                                    }

                                    override fun onAnimationStart(animation: Animation?) {
                                    }

                                })
                                imgUploadAnim.startAnimation(animation3)
                            }

                            override fun onAnimationStart(animation: Animation?) {
                            }

                        })
                        imgUploadAnim.startAnimation(animation2)
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                })
                imgUploadAnim.startAnimation(animation1)

            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        imgUploadAnim.startAnimation(animation)

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as HomeActivity).showHideSearchButton(false)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as HomeActivity).showHideSearchButton(true)

    }


}