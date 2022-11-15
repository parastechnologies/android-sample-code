package com.app.muselink.ui.activities.settings.pushnotification

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.app.muselink.R
import com.app.muselink.commonutils.CustomAlert
import com.app.muselink.model.ui.UserDetails
import com.app.muselink.databinding.ActiviyPushNotificationsBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.ToggleOnOff
import com.app.muselink.util.finishActivity
import com.app.muselink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activiy_push_notifications.*
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

@AndroidEntryPoint
class PushNotificationnActivity :
    BaseViewModelActivity<ActiviyPushNotificationsBinding, PushNotificationViewModel>(),
    PushNotificationViewModel.UserDetailsNavigator {

    override fun getLayout(): Int {
        return R.layout.activiy_push_notifications
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding?.vm = viewModel

        viewModel?.viewLifecycleOwner = this
        viewModel?.binding = binding
        viewModel?.setReferenceUserDetails(this)
        viewModel?.setupObservers()
        viewModel?.callApiGetAccountDetails()

        setToolbar()
//        setListeners()

        setupObservers()

    }


    fun showAlert(message: String) {

        val alert = CustomAlert(this)
        alert.setCustomAlert(this)
        alert.setTitle(getString(R.string.alert))
        alert.setCustomMessage(message)
        alert.hideNegativeButton()
        alert.setPositiveBtnCaption(getString(R.string.ok))
        alert.setCustomAlertListener(object : CustomAlert.CustomAlertListener {
            override fun onPositiveBtnClicked() {
                alert.cancelDialog()
            }

            override fun onNegativeBtnClicked() {
            }

            override fun onDismiss() {
            }

        })
        alert.showAlertDialog(this)

    }

    private fun setupObservers() {

        viewModel?._pushNotificationRes?.observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data != null) {
                        if (it.data.isSuccess()) {
//                            showAlert(it.data.message!!)
                            showToast(this, it.data.message)
                        } else {
                            showToast(this, it.data.message)
                        }
                    } else {
                        showToast(this, it.data?.message)
                    }
                }
                Resource.Status.ERROR -> {
                    showToast(this, it.message)
                }
                Resource.Status.LOADING -> {

                }
            }
        })

    }

    private fun setListeners() {

        switchNewAdmirer.setOnCheckedChangeListener(
            ToggleOnOff(
                switchNewAdmirerToggle,
                npcSwitchNewAdmirer
            )
        )
        switchNewMatch.setOnCheckedChangeListener(
            ToggleOnOff(
                switchNewMatchToggle,
                npcSwitchNewMatch
            )
        )
        switchNewMessage.setOnCheckedChangeListener(
            ToggleOnOff(
                switchNewMessageToggle,
                npcSwitchNewMessage
            )
        )
        switchUpload.setOnCheckedChangeListener(ToggleOnOff(switchUploadToggle, npcSwitchUpload))


    }

    val switchNewMessageToggle = object : ToggleOnOff.ToggleOnOffNavigator {

        override fun onChecked() {
            viewModel?.notificationType = SyncConstants.NotificationTypes.NEW_MESSAGE.value
            viewModel?.statusValue = 1
            viewModel?.changeNotificationStatus()
        }

        override fun onUnChecked() {
            viewModel?.notificationType = SyncConstants.NotificationTypes.NEW_MESSAGE.value
            viewModel?.statusValue = 0
            viewModel?.changeNotificationStatus()
        }

    }

    val switchNewMatchToggle = object : ToggleOnOff.ToggleOnOffNavigator {
        override fun onChecked() {
            viewModel?.notificationType = SyncConstants.NotificationTypes.NEW_MATCH.value
            viewModel?.statusValue = 1
            viewModel?.changeNotificationStatus()
        }

        override fun onUnChecked() {
            viewModel?.notificationType = SyncConstants.NotificationTypes.NEW_MATCH.value
            viewModel?.statusValue = 0
            viewModel?.changeNotificationStatus()
        }

    }

    val switchUploadToggle = object : ToggleOnOff.ToggleOnOffNavigator {
        override fun onChecked() {
            viewModel?.notificationType =
                SyncConstants.NotificationTypes.NOTIFICATION_SETTING_STATUS.value
            viewModel?.statusValue = 1
            viewModel?.changeNotificationStatus()
        }

        override fun onUnChecked() {
            viewModel?.notificationType =
                SyncConstants.NotificationTypes.NOTIFICATION_SETTING_STATUS.value
            viewModel?.statusValue = 0
            viewModel?.changeNotificationStatus()
        }

    }

    val switchNewAdmirerToggle = object : ToggleOnOff.ToggleOnOffNavigator {
        override fun onChecked() {
            viewModel?.notificationType = SyncConstants.NotificationTypes.NEW_ADMIRER.value
            viewModel?.statusValue = 1
            viewModel?.changeNotificationStatus()

        }

        override fun onUnChecked() {
            viewModel?.notificationType = SyncConstants.NotificationTypes.NEW_ADMIRER.value
            viewModel?.statusValue = 0
            viewModel?.changeNotificationStatus()

        }

    }

    private fun setToolbar() {
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.setText(getString(R.string.manage_my_account))
    }

    override fun getViewModelClass(): Class<PushNotificationViewModel> {
        return PushNotificationViewModel::class.java
    }


    override fun userDetails(userDetails: UserDetails) {

        if (userDetails.New_Admirer_Notification_Status.equals("1")) {
            npcSwitchNewAdmirer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenSwitch))
            switchNewAdmirer.isChecked = true
        } else {
            npcSwitchNewAdmirer.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            switchNewAdmirer.isChecked = false
        }

        if (userDetails.New_Match_Notification_Status.equals("1")) {
            switchNewMatch.isChecked = true
            npcSwitchNewMatch.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenSwitch))
        } else {
            switchNewMatch.isChecked = false
            npcSwitchNewMatch.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        }

        if (userDetails.New_Message_Notification_Status.equals("1")) {
            npcSwitchNewMessage.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenSwitch))
            switchNewMessage.isChecked = true
        } else {
            switchNewMessage.isChecked = false
            npcSwitchNewMessage.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        }

        if (userDetails.New_Match_File_Notification_Status.equals("1")) {
            npcSwitchUpload.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenSwitch))
            switchUpload.isChecked = true
        } else {
            switchUpload.isChecked = false
            npcSwitchUpload.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        }

        setListeners()


    }

    override fun onBackPressed() {
        finishActivity()
    }
}