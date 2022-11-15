package com.app.muselink.ui.activities.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.RequestCodeConstants
import com.app.muselink.data.modals.responses.UserDetailModel
import com.app.muselink.helpers.PermissionHelper
import com.app.muselink.listener.DropBoxUserInfoInterface
import com.app.muselink.listener.DropboxInterface
import com.app.muselink.listener.NotificationCountListener
import com.app.muselink.singeltons.SingletonInstances
import com.app.muselink.ui.bottomsheets.SearchBottomSheet
import com.app.muselink.ui.bottomsheets.logintypes.LoginTypesBottomSheet
import com.app.muselink.ui.bottomsheets.logintypes.LoginTypesViewModel
import com.app.muselink.ui.bottomsheets.musiclinkpro.MuseLinkProBottomsheet
import com.app.muselink.ui.bottomsheets.signuptypes.SignUpTypesBottomSheet
import com.app.muselink.ui.bottomsheets.signuptypes.SignupTypesViewModel
import com.app.muselink.ui.dialogfragments.MatchDialog
import com.app.muselink.ui.fragments.chatscreen.chatfragment.ChatFragment
import com.app.muselink.ui.fragments.home.ProfileFragment
import com.app.muselink.ui.fragments.home.UploadFragment
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import com.app.muselink.ui.fragments.home.dashboarduserprofile.DashBoardUserProfileFragment
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.android.AuthActivity
import com.dropbox.core.v2.DbxClientV2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_bottom_bar_home.*
import soup.neumorphism.NeumorphCardView


const val EXTRA_NOTIFICATION_TYPE = "notification_type"
const val EXTRA_NOTIFICATION_TITLE = "notification_title"
const val EXTRA_NOTIFICATION = "is_from_notification"
const val EXTRA_NOTIFICATION_ID = "notification_id"
const val EXTRA_NOTIFICATION_SENDER_ID = "notification_sender_id"
const val EXTRA_NOTIFICATION_SENDER_IMAGE = "notification_sender_image"
const val EXTRA_NOTIFICATION_SENDER_NAME = "notification_sender_name"

@AndroidEntryPoint
class HomeActivity : BaseActivity(), PermissionHelper.OnRequestPermissionsResult, NotificationCountListener,DropboxInterface {

    private var ACCESS_TOKEN = "sl.BHh5B9u5HXJOXc__btTCMKRKlkS6edM6cUmlbaHtAc80S1IRobEaohfmT3ubRdEeilPppIpGkFNWHV5-VhCeq4bWTCbS07fk7WJCEdIbF7K6zAFdp7vQa9hGwLXhoQJDABDrLMM"

    var client:DbxClientV2?=null

    private var messageBroadcastReceiver = SortedListenerBroadCast()
    override fun getLayout() = R.layout.activity_home
    private var permissionsRequest: PermissionHelper? = null

    companion object{
        var dropBoxUserInfoInterface: DropBoxUserInfoInterface?=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBottomBarListeners()

        SignupTypesViewModel.signinDropbox=this
        LoginTypesViewModel.signinDropbox=this
        if (SharedPrefs.getString(AppConstants.PREFS_LOGIN_TYPE) == AppConstants.LoginType.USERPROFILE.value) {
            npmFlag.visibility = View.VISIBLE
        } else {
            npmFlag.visibility = View.GONE
        }
        imgBtnDashBoard.performClick()
        permissionsRequest = PermissionHelper(this)
        permissionsRequest?.checkAndRequestPermissionAudio(this)
        setListeners()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainContainer)
        if (fragment is UploadFragment) {
            fragment.onRequestPermissions(requestCode, permissions, grantResults)
        }
        permissionsRequest?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun setListeners() {
        npmSearch.setOnClickListener {
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            } else {
                if (SharedPrefs.isUserLogin()) {
                    if (SharedPrefs.subscriptionStatus()) {
                        SearchBottomSheet().show(supportFragmentManager, "SearchScreen")
                    } else {
                        MuseLinkProBottomsheet(this).show(
                            supportFragmentManager,
                            "MuseLinkProBottomSheet"
                        )
                    }
                }
            }
        }
    }
    private fun selectedUnSelectedCard(selectedCard: NeumorphCardView, unselectedCard1: NeumorphCardView, unSelectedCard2: NeumorphCardView, unselectedCard3: NeumorphCardView) {
        selectedCard.setShadowElevation(6.0f)
        unselectedCard1.setShadowElevation(0.0f)
        unSelectedCard2.setShadowElevation(0.0f)
        unselectedCard3.setShadowElevation(0.0f)
    }
    fun showHideBottomBar(IsShow: Boolean) {
        if (IsShow) {
            bottomBar.visibility = View.VISIBLE
        } else {
            bottomBar.visibility = View.GONE
        }
    }

    fun showHideSearchButton(IsShow: Boolean) {
        if (IsShow) {
            llTopViewSearch.visibility = View.VISIBLE
        } else {
            llTopViewSearch.visibility = View.GONE
        }
    }

    private fun hideShowViewSelection(
        selectImg: View,
        unSelectImg1: View,
        unSelectImg2: View,
        unSelectImg3: View
    ) {
        selectImg.visibility = View.VISIBLE
        unSelectImg1.visibility = View.GONE
        unSelectImg2.visibility = View.GONE
        unSelectImg3.visibility = View.GONE
    }

    private fun changeImageColorSelected(
        selectImg: AppCompatImageView,
        unSelectImg1: AppCompatImageView,
        unSelectImg2: AppCompatImageView,
        unSelectImg3: AppCompatImageView
    ) {
        selectImg.setColorFilter(
            ContextCompat.getColor(this, R.color.color_purple_100),
            PorterDuff.Mode.SRC_IN
        )
        unSelectImg1.setColorFilter(
            ContextCompat.getColor(this, R.color.color_icon_grey),
            PorterDuff.Mode.SRC_IN
        )
        unSelectImg2.setColorFilter(
            ContextCompat.getColor(this, R.color.color_icon_grey),
            PorterDuff.Mode.SRC_IN
        )
        unSelectImg3.setColorFilter(
            ContextCompat.getColor(this, R.color.color_icon_grey),
            PorterDuff.Mode.SRC_IN
        )
    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, fragment)
        transaction.commit()
    }

    fun setMarginMainView(marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) {
        val relativeParams: RelativeLayout.LayoutParams =
            rlHomeMainView.layoutParams as RelativeLayout.LayoutParams
        relativeParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        rlHomeMainView.layoutParams = relativeParams
    }

    private fun setBottomBarListeners() {
        imgBtnDashBoard.setOnClickListener {
            if (SharedPrefs.getString(AppConstants.PREFS_LOGIN_TYPE) == AppConstants.LoginType.USERPROFILE.value) {
                selectedUnSelectedCard(imgBtnDashBoard, imgBtnChat, imgBtnUpload, imgBtnProfile)
                changeImageColorSelected(imgDashBoard, imgchat, imgUpload, imgProfile)
                hideShowViewSelection(viewDashBoard, viewChat, viewUpload, viewProfile)
                openFragment(DashBoardUserProfileFragment())
            } else {
                selectedUnSelectedCard(imgBtnDashBoard, imgBtnChat, imgBtnUpload, imgBtnProfile)
                changeImageColorSelected(imgDashBoard, imgchat, imgUpload, imgProfile)
                hideShowViewSelection(viewDashBoard, viewChat, viewUpload, viewProfile)
                openFragment(DashBoardFragment())
            }
        }
        imgBtnChat.setOnClickListener {
            selectedUnSelectedCard(imgBtnChat, imgBtnDashBoard, imgBtnUpload, imgBtnProfile)
            changeImageColorSelected(imgchat, imgDashBoard, imgUpload, imgProfile)
            hideShowViewSelection(viewChat, viewDashBoard, viewUpload, viewProfile)
            openFragment(ChatFragment())
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            }
        }
        imgBtnUpload.setOnClickListener {
            selectedUnSelectedCard(imgBtnUpload, imgBtnChat, imgBtnDashBoard, imgBtnProfile)
            changeImageColorSelected(imgUpload, imgchat, imgDashBoard, imgProfile)
            hideShowViewSelection(viewUpload, viewChat, viewDashBoard, viewProfile)
            openFragment(UploadFragment())
            if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            }
        }
        imgBtnProfile.setOnClickListener {
            selectedUnSelectedCard(imgBtnProfile, imgBtnChat, imgBtnDashBoard, imgBtnUpload)
            changeImageColorSelected(imgProfile, imgchat, imgUpload, imgDashBoard)
            hideShowViewSelection(viewProfile, viewChat, viewUpload, viewDashBoard)
            openFragment(ProfileFragment())
           /* if (!SharedPrefs.isUserLogin()) {
                val signUpTypesBottomSheet = SignUpTypesBottomSheet()
                signUpTypesBottomSheet.show(supportFragmentManager, "AuthDialog")
                SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
            }*/
        }
    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodeConstants.REQUEST_CODE_INSTAGRAM_LOGIN) {
            if (SingletonInstances.bottomSheetDialogFragment != null) {
                val bottomSheetDialog = SingletonInstances.bottomSheetDialogFragment
                if (bottomSheetDialog is SignUpTypesBottomSheet) {
                    bottomSheetDialog.instagramResult(requestCode, resultCode, data)
                } else if (bottomSheetDialog is LoginTypesBottomSheet) {
                    bottomSheetDialog.instagramResult(requestCode, resultCode, data)
                }
            }
        } else if (SingletonInstances.bottomSheetDialogFragment != null) {
            if (data != null) {
                val bottomSheetDialog = SingletonInstances.bottomSheetDialogFragment
                if (bottomSheetDialog is SignUpTypesBottomSheet) {
                    bottomSheetDialog.facebookResult(requestCode, resultCode, data)
                } else if (bottomSheetDialog is LoginTypesBottomSheet) {
                    bottomSheetDialog.facebookResult(requestCode, resultCode, data)
                }
            }
        }
    }
    fun openUploadFragment() {
        selectedUnSelectedCard(imgBtnUpload, imgBtnChat, imgBtnDashBoard, imgBtnProfile)
        changeImageColorSelected(imgUpload, imgchat, imgDashBoard, imgProfile)
        hideShowViewSelection(viewUpload, viewChat, viewDashBoard, viewProfile)
        openFragment(UploadFragment())
        if (!SharedPrefs.isUserLogin()) {
            val signUpTypesBottomSheet = SignUpTypesBottomSheet()
            signUpTypesBottomSheet.show(supportFragmentManager, "AuthDialog")
            SingletonInstances.setBottomSheetDialogInstance(signUpTypesBottomSheet)
        }
    }
    override fun onPermissionsGranted() {}
    override fun onPermissionsDenied() {
        permissionsRequest?.checkAndRequestPermissionAudio(this)
    }

    override fun onResume() {
        super.onResume()
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val accessToken = Auth.getOAuth2Token()
        if (accessToken!=null){
            ACCESS_TOKEN=accessToken
            val config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build()
            client = DbxClientV2(config, ACCESS_TOKEN)
            dropBoxUserInfoInterface!!.getUserData(client?.users()?.currentAccount?.accountId.toString(),"dropbox",client?.users()?.currentAccount?.country.toString())
            Log.e("LoginAccountDetails","===>>"+client?.users()?.currentAccount?.email)
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageBroadcastReceiver, IntentFilter("custom-event-name"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageBroadcastReceiver)
    }

    inner class SortedListenerBroadCast : BroadcastReceiver() {
        override
        fun onReceive(context: Context?, intent: Intent) {
            try {
                Handler(mainLooper).postDelayed({
                    val matchDialog = MatchDialog()
                    val bundle = bundleOf(
                        AppConstants.receiverId to intent.getStringExtra(AppConstants.receiverId),
                        AppConstants.receiverName to intent.getStringExtra(AppConstants.receiverName)
                    )
                    matchDialog.arguments = bundle
                    supportFragmentManager.beginTransaction().add(
                        matchDialog, "MatchDialog"
                    ).commit()
                }, 500)
            } catch (e: Exception) {
            }
        }
    }

    override fun setCount(text: String?, type: String) {
        val frag: ChatFragment? =
            supportFragmentManager.findFragmentById(R.id.mainContainer) as ChatFragment?
        frag?.updateText(text.toString(), type)
    }

    override fun onClickLoginDropBox() {
        Auth.startOAuth2Authentication(this, "o9xgaqule1za9hg");
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build()
        client = DbxClientV2(config, ACCESS_TOKEN)


    }


}