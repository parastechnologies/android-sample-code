package com.app.muselink.ui.activities.welcome

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.ui.activities.selectExplore.SelectExploreActivity
import com.app.muselink.ui.fragments.welcome.FirstFragment
import com.app.muselink.ui.fragments.welcome.SecondFragment
import com.app.muselink.util.finishActivity
import com.app.muselink.util.intentComponent
import soup.neumorphism.NeumorphButton
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class WelcomeActivity : BaseActivity() {
    var next: NeumorphButton? = null
    var card1: NeumorphCardView? = null
    var card2: NeumorphCardView? = null
    var card3: NeumorphCardView? = null
    var image1: ImageView? = null
    var image2: ImageView? = null
    var image3: ImageView? = null
    var currentIndex = 0
    override fun getLayout(): Int {
        return R.layout.activity_welcome
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        init()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, FirstFragment())
        transaction.commit()
    }

    var screentype = ""

    private fun getIntentData() {
        if (intent != null) {
            if (intent.extras != null) {
                val bundle = intent.extras
                if (bundle!!.containsKey("screentype")) {
                    screentype = intent.getStringExtra("screentype").toString()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        next = findViewById(R.id.next)
        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        image3 = findViewById(R.id.image3)
        next = findViewById(R.id.next)
        next?.setOnClickListener {
            next?.setShapeType(ShapeType.BASIN)
            if (currentIndex == 2) {
                if (screentype == "setting") {
                    finish()
                } else {
                    SharedPrefs.setFirstTime(AppConstants.IS_FIRST_TIME, true)
                    intentComponent(SelectExploreActivity::class.java, null)
                    finishActivity()
                }
            } else {
                Handler(mainLooper).post {
                    currentIndex++
                    pagerListener(currentIndex)
                    val fragment: Fragment? =
                        supportFragmentManager.findFragmentById(R.id.frame_container)
                    if (fragment is FirstFragment) {
                        (fragment as FirstFragment).onMoveToSecondScreen()
                    } else if (fragment is SecondFragment) {
                        (fragment as SecondFragment).onMoveToThirdScreen()
                    }
                }
            }
            Handler(mainLooper).postDelayed({
                next?.setShapeType(ShapeType.FLAT)
            }, 300)
        }
    }

    private fun pagerListener(position: Int) {
        when (position) {
            0 -> {
                selectedUnSelectedCard(card1, card2, card3)
                selectedImage(image1, image2, image3)
                next?.text = resources.getText(R.string.next)
            }
            1 -> {
                selectedUnSelectedCard(card2, card1, card3)
                selectedImage(image2, image1, image3)
                next?.text = resources.getText(R.string.next)
            }
            else -> {
                selectedUnSelectedCard(card3, card1, card2)
                selectedImage(image3, image1, image2)
                next?.text = resources.getText(R.string.start)
            }
        }
    }

    private fun selectedImage(
        selectedImage: ImageView?,
        unselected1: ImageView?,
        unselected2: ImageView?
    ) {
        selectedImage?.setBackgroundResource(R.drawable.drawable_circle_purple)
        unselected1?.setBackgroundResource(android.R.color.transparent)
        unselected2?.setBackgroundResource(android.R.color.transparent)
    }

    private fun selectedUnSelectedCard(
        selectedView: NeumorphCardView?,
        unSelectedView1: NeumorphCardView?,
        unSelectedView2: NeumorphCardView?
    ) {
        selectedView?.setStrokeColor(
            ContextCompat.getColorStateList(
                this@WelcomeActivity,
                R.color.color_watermelon
            )
        )
        unSelectedView1?.setStrokeColor(
            ContextCompat.getColorStateList(
                this@WelcomeActivity,
                android.R.color.transparent
            )
        )
        unSelectedView2?.setStrokeColor(
            ContextCompat.getColorStateList(this@WelcomeActivity, android.R.color.transparent)
        )
    }
}
