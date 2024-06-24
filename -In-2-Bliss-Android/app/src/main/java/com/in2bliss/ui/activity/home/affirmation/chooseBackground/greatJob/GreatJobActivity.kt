package com.in2bliss.ui.activity.home.affirmation.chooseBackground.greatJob

import android.text.method.LinkMovementMethod
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityGreatJobBinding
import com.in2bliss.ui.activity.home.HomeActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GreatJobActivity : BaseActivity<ActivityGreatJobBinding>(
    layout = R.layout.activity_great_job
) {
    override fun init() {
        backPressed()

        binding.tvForMoreInformation.setText(R.string.great_job_5)
        binding.tvForMoreInformation.movementMethod = LinkMovementMethod.getInstance()

        settingRecyclerView()
        onClick()
    }

    private fun onClick() {
        binding.btnGoToMyAffirmations.setOnClickListener {
            intent(
                destination = HomeActivity::class.java,
                bundle = bundleOf(
                    AppConstant.CREATE_AFFIRMATION to AppConstant.CreatedAffirmationEdit.CREATE_AFFIRMATION.name
                )
            )
            finishAffinity()
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })
    }

    private fun settingRecyclerView() {
        binding.rvAffirmationList.adapter = GreatJobAdapter().apply {
            submitList(affirmationList())
        }
    }

    private fun affirmationList(): ArrayList<Int> {
        return arrayListOf(
            R.string.great_job_1,
            R.string.great_job_2,
            R.string.great_job_3,
            R.string.great_job_4
        )
    }
}