package com.app.muselink.ui.activities.selectExplore

import android.os.Bundle
import android.widget.ImageView
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.util.intentComponent
import com.app.muselink.util.springAnimation_

class SelectExploreActivity : BaseActivity() {
    lateinit var ivUserFile: ImageView
    lateinit var ivSoundFile: ImageView
    override fun getLayout() = R.layout.activity_select_explore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        onClickListener()
    }
    /**
     * Initialize View
     * */
    private fun initView() {
        ivUserFile = findViewById(R.id.ivUserFile)
        ivSoundFile = findViewById(R.id.ivSoundFile)
        ivUserFile.springAnimation_(.5f)
        ivSoundFile.springAnimation_(.5f)
    }
    /**
     * OnItemClick listener
     * */
    private fun onClickListener() {
        ivUserFile.setOnClickListener {
            SharedPrefs.save(AppConstants.PREFS_LOGIN_TYPE, AppConstants.LoginType.USERPROFILE.value)
            intentComponent(HomeActivity::class.java, bundle = null)
            finish()
        }
        ivSoundFile.setOnClickListener {
            SharedPrefs.save(AppConstants.PREFS_LOGIN_TYPE, AppConstants.LoginType.SOUND_FILE.value)
            intentComponent(HomeActivity::class.java, bundle = null)
            finish()
        }
    }
}