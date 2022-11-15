package com.app.muselink.ui.activities.profile.editgoals

import android.os.Bundle
import android.view.View
import com.app.muselink.R
import com.app.muselink.databinding.ActivityEditGoalsBinding
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*

@AndroidEntryPoint
class EditGoalsActivity : BaseViewModelActivity<ActivityEditGoalsBinding, EditGoalsViewModal>() {

    override fun getViewModelClass(): Class<EditGoalsViewModal> {
        return EditGoalsViewModal::class.java
    }
    override fun getLayout(): Int {
        return R.layout.activity_edit_goals_
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar()
        binding?.vm = viewModel
        viewModel?.lifeCycle = this
        viewModel?.biniding =binding
        viewModel?.setupObserversGoals()
        viewModel?.setupObserversUpdateGoals()
        viewModel?.setRecycleView()
    }
    private fun setToolbar() {
        tvHeading.visibility = View.VISIBLE
        tvHeading.text = getString(R.string.edit_goals)
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
    }


}