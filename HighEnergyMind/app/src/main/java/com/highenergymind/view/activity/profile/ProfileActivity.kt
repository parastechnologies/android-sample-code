package com.highenergymind.view.activity.profile

import android.widget.ImageView
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityProfileBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.firstUpper
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.editProfile.EditProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun getLayoutRes(): Int {
        return R.layout.activity_profile
    }

    override fun initView() {
        fullScreenStatusBar()
        setToolBar()
        clicks()
    }

    override fun onResume() {
        super.onResume()
        setDataLocally()
    }

    private fun setDataLocally() {
        val data = sharedPrefs.getUserData()
        binding.apply {
            ivProfile.glideImage(data?.userImg)
            tvFirstName.text = data?.firstName
            if (data?.lastName.isNullOrEmpty()){
                llLastName.gone()
                tvTitleLastName.gone()
            }else{
                llLastName.visible()
                tvTitleLastName.visible()
                tvLastName.text = data?.lastName
            }
            tvUserEmail.text = data?.email
            tvLanguage.text = data?.language?.firstUpper()
        }
    }

    private fun setToolBar() {
        binding.customTool.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.profile)
        }
    }

    private fun clicks() {
        binding.apply {
            btnEdit.setOnClickListener {
                intentComponent(EditProfileActivity::class.java)
            }
        }
    }

}