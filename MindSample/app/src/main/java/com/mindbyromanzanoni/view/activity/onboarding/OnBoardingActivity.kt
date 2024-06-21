package com.mindbyromanzanoni.view.activity.onboarding

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayoutMediator
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityOnBoardingBinding
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.overrideImageStatusBar
import com.mindbyromanzanoni.view.activity.login.LoginActivity
import com.mindbyromanzanoni.view.activity.signup.SignupActivity
import com.mindbyromanzanoni.view.adapter.ViewPagerAdapter

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(),
    ViewPager.OnPageChangeListener {
    val adapter by lazy {
        ViewPagerAdapter()
    }
    override fun getLayoutRes(): Int = R.layout.activity_on_boarding
    override fun initView() {
        overrideImageStatusBar(this)
        setAdapter()
        onClickListener()
    }
    override fun viewModel() {}
    private fun setAdapter() {
        binding.apply {
            viewpager.adapter = adapter
            TabLayoutMediator(tabLayout, viewpager) { k, j ->
                k.text = ""
            }.attach()
            adapter.submitList( getOnBoardData() )
        }
    }
    data class OnBoardData(var title: String, var content: String, var image: Int)

    private fun onClickListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                launchActivity<LoginActivity> { }
            }
            btnGetStarted.setOnClickListener {
                launchActivity<SignupActivity> { }
            }
        }
    }
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    private fun getOnBoardData(): MutableList<OnBoardData> {
          return mutableListOf<OnBoardData>().also {
            it.add(OnBoardData("Daily Guided Meditations and Healing Videos.", "Experience daily healing meditations and videos for tranquility and wellness, healing and inner peace.", R.drawable.on_board_one))
            it.add(OnBoardData("Message Live With Specialists & AI by Roman", "Connect with knowledgeable specialists or AI trained by Roman for expert guidance and immediate emotional regulation.", R.drawable.on_board_two))
            it.add(OnBoardData("Download Infinite Healing Resources.", "Access a wealth of resources including Daily Exercises and Meal Inspirations to fuel your holistic journey towards wellness.", R.drawable.on_board_three))
        }
    }
}