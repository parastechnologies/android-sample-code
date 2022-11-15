package com.app.muselink.ui.activities.settings

import android.os.Bundle
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.ui.activities.settings.support.ReportAProblemActivity
import com.app.muselink.ui.activities.settings.support.SubmitIdeaActivity
import com.app.muselink.util.finishActivity
import com.app.muselink.util.intentComponent
import kotlinx.android.synthetic.main.activity_support.*
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

class SupportActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_support
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar()
        setListeners()
    }

    private fun setListeners() {

        nmpReportProblem.setOnClickListener {
            intentComponent(ReportAProblemActivity::class.java, null)
        }

        nmpSubmitAnIdea.setOnClickListener {
            intentComponent(SubmitIdeaActivity::class.java, null)
        }

    }


    private fun setToolbar() {

        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }

        tvHeading.text = getString(R.string.manage_my_account)

    }

    override fun onBackPressed() {
        finishActivity()
    }
}