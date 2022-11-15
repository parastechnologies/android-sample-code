package com.app.muselink.ui.activities.settings.manageaccount
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.app.muselink.R
import com.app.muselink.model.ui.UserDetails
import com.app.muselink.databinding.ActivityManageAccountBinding
import com.app.muselink.ui.activities.settings.blockaccount.BlockedAccountActiivty
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import com.app.muselink.ui.bottomsheets.ShareAccountBottomsheet
import com.app.muselink.util.SyncConstants
import com.app.muselink.util.ToggleOnOff
import com.app.muselink.util.finishActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_manage_account.*
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*
import soup.neumorphism.NeumorphCardView

@AndroidEntryPoint
class ManageAccountActivity :
    BaseViewModelActivity<ActivityManageAccountBinding, ManageAccountViewmodel>(),
    ManageAccountViewmodel.UserDetailsNavigator {
    override fun getLayout(): Int {
        return R.layout.activity_manage_account
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.vm = viewModel
        viewModel?.viewLifecycleOwner = this
        viewModel?.binding = binding
        viewModel?.setupObservers()
        viewModel?.setReferenceUserDetails(this)
        viewModel?.setupObserversAccountStatus()
        viewModel?.callApiGetAccountDetails()
        setToolbar()
        setListeners()
    }
    private fun setToolbar() {
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.text = getString(R.string.manage_my_account)
    }
    private fun setListeners() {
        nmpBlockAccounts.setOnClickListener {
            val intent = Intent(this, BlockedAccountActiivty::class.java)
            startActivity(intent)
        }
        llRadioUserMatched.setOnClickListener {
            selectedRadio(radio3, radio1, radio2)
            selectedUnSelectedCard(cardRadio3, cardRadio1, cardRadio2)
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.DIRECT_MESSAGE_STATUS.value, 2)
        }
        llRadioPremiumUser.setOnClickListener {
            selectedRadio(radio2, radio1, radio3)
            selectedUnSelectedCard(cardRadio2, cardRadio1, cardRadio3)
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.DIRECT_MESSAGE_STATUS.value, 1)
        }
        llRadioEveryone.setOnClickListener {
            selectedRadio(radio1, radio2, radio3)
            selectedUnSelectedCard(cardRadio1, cardRadio2, cardRadio3)
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.DIRECT_MESSAGE_STATUS.value, 0)
        }
        npmShareAccount.setOnClickListener {
            ShareAccountBottomsheet(this).show(supportFragmentManager, "ShareAccount")
        }
    }
    private fun selectedRadio(
        imageSelected: AppCompatImageView,
        imageUnSelected: AppCompatImageView,
        imageUnSelected1: AppCompatImageView){
        imageSelected.setBackgroundResource(R.drawable.drwable_circle_radio)
        imageUnSelected.setBackgroundResource(android.R.color.transparent)
        imageUnSelected1.setBackgroundResource(android.R.color.transparent)
    }
    private fun selectedUnSelectedCard(
        selectedView: NeumorphCardView?,
        unSelectedView1: NeumorphCardView?,
        unSelectedView2: NeumorphCardView?){
        selectedView?.setStrokeColor(ContextCompat.getColorStateList(this,R.color.color_purple_100))
        unSelectedView1?.setStrokeColor(ContextCompat.getColorStateList(this,android.R.color.transparent))
        unSelectedView2?.setStrokeColor(ContextCompat.getColorStateList(this, android.R.color.transparent))
    }
    private val switchNoYesToggle = object : ToggleOnOff.ToggleOnOffNavigator {
        override fun onChecked() {
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.SOUDN_FILE_STATUS.value,1)
        }
        override fun onUnChecked() {
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.SOUDN_FILE_STATUS.value,0)
        }
    }
    private val switchHideShowToggle = object : ToggleOnOff.ToggleOnOffNavigator {
        override fun onChecked() {
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.ACCOUNT_STATUS.value, 1)
        }
        override fun onUnChecked() {
            viewModel?.callApiAccountStatus(SyncConstants.AccountStatus.ACCOUNT_STATUS.value, 0)
        }
    }
    override fun getViewModelClass(): Class<ManageAccountViewmodel> {
        return ManageAccountViewmodel::class.java
    }
    override fun userDetails(userDetails: UserDetails) {
        if (userDetails.Account_Status.equals("1")) {
            npcSwitchHideShow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenSwitch))
            switchHideShow.isChecked = true
        } else {
            npcSwitchHideShow.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            switchHideShow.isChecked = false
        }
        if (userDetails.Sound_File_Status.equals("1")) {
            npcSwitchNoYes.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreenSwitch))
            switchNoYes.isChecked = true
        } else {
            switchNoYes.isChecked = false
            npcSwitchNoYes.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        }
        when {
            userDetails.Direct_Message_Status.equals("0") -> {
                selectedRadio(radio1, radio2, radio3)
                selectedUnSelectedCard(cardRadio1, cardRadio2, cardRadio3)
            }
            userDetails.Direct_Message_Status.equals("1") -> {
                selectedRadio(radio2, radio1, radio3)
                selectedUnSelectedCard(cardRadio2, cardRadio1, cardRadio3)
            }
            userDetails.Direct_Message_Status.equals("2") -> {
                selectedRadio(radio3, radio1, radio2)
                selectedUnSelectedCard(cardRadio3, cardRadio1, cardRadio2)
            }
        }
        switchHideShow.setOnCheckedChangeListener(ToggleOnOff(switchHideShowToggle, npcSwitchHideShow))
        switchNoYes.setOnCheckedChangeListener(ToggleOnOff(switchNoYesToggle, npcSwitchNoYes))
    }

    override fun onBackPressed() {
        finishActivity()
    }

}