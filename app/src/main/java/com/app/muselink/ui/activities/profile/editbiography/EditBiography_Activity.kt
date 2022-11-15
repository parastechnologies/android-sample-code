package com.app.muselink.ui.activities.profile.editbiography

import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.ActivityEditBiographyBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

@AndroidEntryPoint
class EditBiography_Activity : BaseViewModelActivity<ActivityEditBiographyBinding,EditBioGraphyViewmodel>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.vm = viewModel
        viewModel?.lifeCycle = this
        viewModel?.biniding =binding
        viewModel?.getIntentData()
        viewModel?.setupObserversUpdateBioGraphy()
        setToolbar()
    }

    private fun setToolbar() {
        tvHeading.visibility = View.VISIBLE
        tvHeading.setText(getString(R.string.edt_biography))

        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }

    }

    override fun getViewModelClass(): Class<EditBioGraphyViewmodel> {
        return EditBioGraphyViewmodel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.activity_edit_biography_
    }

}