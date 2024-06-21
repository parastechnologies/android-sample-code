package com.highenergymind.view.activity.logout

import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityLogoutBinding
import com.highenergymind.utils.fullScreenStatusBar

class LogoutActivity : BaseActivity<ActivityLogoutBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_logout
    }

    override fun initView() {
        fullScreenStatusBar()
    }


}