package com.highenergymind.view.activity.unlockFeature

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.highenergymind.R
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.LayoutUnLockFeatureBinding
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.freetrail.FreeTrailActivity
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.sheet.redeemCode.RedeemCodeSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnlockFeatureActivity : BaseActivity<LayoutUnLockFeatureBinding>() {
    val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    override fun getLayoutRes(): Int {
        return R.layout.layout_un_lock_feature
    }

    override fun initView() {
        fullScreenStatusBar(isTextBlack = false)
        getBundleData()
        clicks()
    }

    private fun getBundleData() {
        binding.apply {
            if (intent.hasExtra(AppConstant.IS_FROM_SIGN_UP)) {
                redeemCodeTV.visible()
                ivClose.visible()
            } else {
                ivClose.gone()
                redeemCodeTV.visible()
            }
        }
    }

    private fun clicks() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }

            ivClose.setOnClickListener {
                intentComponent(HomeActivity::class.java, intent.extras)
            }
            btnFree.setOnClickListener {
                val intent = Intent(this@UnlockFeatureActivity, FreeTrailActivity::class.java)
                intent.putExtras(this@UnlockFeatureActivity.intent.extras ?: Bundle())
                activityResult.launch(intent)
//                intentComponent(FreeTrailActivity::class.java, intent.extras)
            }
            redeemCodeTV.setOnClickListener {
                RedeemCodeSheet().show(supportFragmentManager, "")
            }
        }
    }
}