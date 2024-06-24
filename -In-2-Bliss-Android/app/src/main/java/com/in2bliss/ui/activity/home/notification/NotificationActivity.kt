package com.in2bliss.ui.activity.home.notification


import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.databinding.ActivityNotificationBinding
import com.in2bliss.ui.activity.home.fragment.notification.read.ReadFragment
import com.in2bliss.ui.activity.home.fragment.notification.unread.UnReadFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>(
    layout = R.layout.activity_notification
) {

    override fun init() {
        loadFragment(UnReadFragment())
        onClick()
    }


    private fun onClick() {
        binding.apply {
            toolBar.tvTitle.text = getString(R.string.notifications)
            toolBar.ivBack.setOnClickListener { finish() }
            tvWritten.setOnClickListener {
                setTextColorAndView(
                    binding.tvWritten,
                    binding.tvRecorded,
                    binding.view,
                    binding.view2
                )
                loadFragment(UnReadFragment())
            }
            binding.tvRecorded.setOnClickListener {
                setTextColorAndView(
                    binding.tvRecorded,
                    binding.tvWritten,
                    binding.view2,
                    binding.view
                )
                loadFragment(ReadFragment())
            }
        }
    }

    private fun setTextColorAndView(
        textView1: AppCompatTextView,
        textView2: AppCompatTextView,
        view1: View,
        view2: View
    ) {
        textView1.setTextColor(
            ContextCompat.getColor(
                this@NotificationActivity,
                R.color.dark_purple_12046A
            )
        )
        textView2.setTextColor(
            ContextCompat.getColor(
                this@NotificationActivity,
                R.color.inactive_purple_7168A6
            )
        )
        view1.setBackgroundColor(
            ContextCompat.getColor(
                this@NotificationActivity,
                R.color.prime_blue_418FF6
            )
        )
        view2.setBackgroundColor(
            ContextCompat.getColor(
                this@NotificationActivity,
                android.R.color.transparent
            )
        )
    }

    private fun loadFragment(fragment: Fragment) {
        val fa = supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
        fa.commit()
    }

}

