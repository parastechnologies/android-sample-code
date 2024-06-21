package com.highenergymind.view.activity.onboard

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.highenergymind.R
import com.highenergymind.adapter.OnboardAdapter
import com.highenergymind.base.BaseActivity
import com.highenergymind.databinding.ActivityOnBoardingBinding
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.disclaimer.DisclaimerActivity
import com.highenergymind.view.activity.onboardexplore.OnBoardingExploreActivity
import com.highenergymind.view.activity.privacypolicy.PrivacyPolicyActivity
import com.highenergymind.view.activity.termsconditions.TermsConditionsActivity

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {


    private var runnable: Runnable? = null
    private var handler: Handler? = null


    override fun getLayoutRes(): Int {
        return R.layout.activity_on_boarding
    }

    override fun initView() {
        fullScreenStatusBar(isTextBlack = false)
        setViewPager()
        setClickableSpan()
    }

    private fun setClickableSpan() {
        val stringBuilder = SpannableStringBuilder()
        stringBuilder.append(getString(R.string.terms_and_conditions)).append(" | ")
            .append(getString(R.string.privacy_policy)).append(" | ")
            .append(getString(R.string.disclaimer)).append(" | ")
            .append(getString(R.string.restore_purchase))
        stringBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    intentComponent(TermsConditionsActivity::class.java)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText=false
                    ds.color=ContextCompat.getColor(this@OnBoardingActivity,R.color.white)
                }
            },
            0,
            getString(R.string.terms_and_conditions).length ,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        stringBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    intentComponent(PrivacyPolicyActivity::class.java)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText=false
                    ds.color=ContextCompat.getColor(this@OnBoardingActivity,R.color.white)
                }
            },
            stringBuilder.indexOf(getString(R.string.privacy_policy)),
            stringBuilder.indexOf(getString(R.string.privacy_policy)) + getString(R.string.privacy_policy).length ,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        stringBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    intentComponent(DisclaimerActivity::class.java)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText=false
                    ds.color=ContextCompat.getColor(this@OnBoardingActivity,R.color.white)
                }
            },
            stringBuilder.indexOf(getString(R.string.disclaimer)),
            stringBuilder.indexOf(getString(R.string.disclaimer))+getString(R.string.disclaimer).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.termsCondionTV.text = stringBuilder
        binding.termsCondionTV.movementMethod = LinkMovementMethod.getInstance();
        binding.termsCondionTV.highlightColor = Color.TRANSPARENT;

    }

    private fun setViewPager() {

        val adapter = OnboardAdapter(supportFragmentManager)
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)


        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.termsCondionTV.visible()
                } else {
                    binding.termsCondionTV.gone()

                }

                if (position == 3) {
                    intentComponent(OnBoardingExploreActivity::class.java, null)
//                    overridePendingTransition(R.anim.left_to_right, R.anim.slide_out_left)
                    finish()

                }

            }
        })

        startAutoSlider(binding.viewPager.currentItem, binding.viewPager)

    }

    private fun startAutoSlider(count: Int, viewPager: ViewPager) {
        Handler(Looper.getMainLooper()).postDelayed({
            var pos: Int = viewPager.getCurrentItem()
            pos = pos + 1
            if (pos >= count) pos = 0
            viewPager.setCurrentItem(pos)
            runnable?.let { handler?.postDelayed(it, 3000) }

        }, 1500)

    }

}