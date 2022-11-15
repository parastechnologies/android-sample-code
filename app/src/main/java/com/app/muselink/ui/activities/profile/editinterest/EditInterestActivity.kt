package com.app.muselink.ui.activities.profile.editinterest
import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.ActivityEditInterestBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*
@AndroidEntryPoint
class EditInterestActivity :
    BaseViewModelActivity<ActivityEditInterestBinding, EditInterestViewModal>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar()
        binding?.vm = viewModel
        viewModel?.lifeCycle = this
        viewModel?.biniding = binding
        viewModel?.setupObserversPersonalInterest()
        viewModel?.setupObserversUpdateInterest()
    }
    private fun setToolbar() {
        tvHeading.visibility = View.VISIBLE
        tvHeading.text = getString(R.string.edit_interets)
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
    }
    override fun getViewModelClass(): Class<EditInterestViewModal> {
        return EditInterestViewModal::class.java
    }
    override fun getLayout(): Int {
        return R.layout.activity_edit_interest_
    }
}