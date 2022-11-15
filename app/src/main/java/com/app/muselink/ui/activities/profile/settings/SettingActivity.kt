package com.app.muselink.ui.activities.profile.settings
import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.ActivitySettingBinding
import com.app.muselink.ui.activities.settings.SupportActivity
import com.app.muselink.ui.activities.settings.manageaccount.ManageAccountActivity
import com.app.muselink.ui.activities.settings.pushnotification.PushNotificationnActivity
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import com.app.muselink.ui.bottomsheets.InviteFriendBottomsheet
import com.app.muselink.util.finishActivity
import com.app.muselink.util.intentComponent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_setting.*

@AndroidEntryPoint
class SettingActivity : BaseViewModelActivity<ActivitySettingBinding, SettingsViewModal>(),
    View.OnClickListener {
    override fun getLayout(): Int {
        return R.layout.activity_setting
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backPressSettings.setOnClickListener {
            onBackPressed()
        }
        binding?.vm = viewModel
        viewModel?.viewLifecycleOwner = this
        viewModel?.setupObserversDeleteAccount()
        viewModel?.setupObserversLogout()
        setListeners()
    }
    private fun setListeners() {
        tvManageAccount.setOnClickListener(this)
        tvPushNotification.setOnClickListener(this)
        tvSupport.setOnClickListener(this)
        inviteFriend.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.inviteFriend -> {
                InviteFriendBottomsheet(this).show(supportFragmentManager, "InviteFriend");
            }
            R.id.tvManageAccount -> {
                intentComponent(ManageAccountActivity::class.java,null)
            }
            R.id.tvPushNotification -> {
                intentComponent(PushNotificationnActivity::class.java,null)
            }
            R.id.tvSupport -> {
                intentComponent(SupportActivity::class.java,null)
            }
        }
    }
    override fun getViewModelClass(): Class<SettingsViewModal> {
        return SettingsViewModal::class.java
    }
    override fun onBackPressed() {
        finishActivity()
    }
}