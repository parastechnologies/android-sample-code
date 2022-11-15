package com.app.muselink.util

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

fun View.springAnimation(startTime: Float) {
    val frogSpringForce = SpringForce().apply {
        dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        stiffness = 400f
    }
    val frogSpringAnimationScaleX = SpringAnimation(this, DynamicAnimation.SCALE_X).apply {
        spring = frogSpringForce
    }
    val frogSpringAnimationScaleY = SpringAnimation(this, DynamicAnimation.SCALE_Y).apply {
        spring = frogSpringForce
    }
    val scalingFactor = 0.5f
    this.scaleX = scalingFactor
    this.scaleY = scalingFactor
    val finalPosition = 1f
    val time = (startTime * 1000).toLong()
    Handler(Looper.getMainLooper()).postDelayed({
        this.visibility = View.VISIBLE
        frogSpringAnimationScaleX.animateToFinalPosition(finalPosition)
        frogSpringAnimationScaleY.animateToFinalPosition(finalPosition)
    }, time)
}

fun View.springAnimationForEqilizerView(startTime: Float) {
    val frogSpringForce = SpringForce().apply {
        dampingRatio = 0.1f
        stiffness = 50f
    }
    val frogSpringAnimationScaleX = SpringAnimation(this, DynamicAnimation.SCALE_X).apply {
        spring = frogSpringForce
    }
    val frogSpringAnimationScaleY = SpringAnimation(this, DynamicAnimation.SCALE_Y).apply {
        spring = frogSpringForce
    }
    val time = (startTime * 1000).toLong()
    Handler(Looper.getMainLooper()).postDelayed({
        val scalingFactor = .97f
        this.scaleX = scalingFactor
        this.scaleY = scalingFactor
        val finalPosition = 1f
        this.visibility = View.VISIBLE
        frogSpringAnimationScaleX.animateToFinalPosition(finalPosition)
        frogSpringAnimationScaleY.animateToFinalPosition(finalPosition)
    }, time)
}

fun View.springAnimation_(startTime: Float) {

    val frogSpringForce = SpringForce().apply {
        dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        stiffness = 400f
    }
    val frogSpringAnimationScaleX = SpringAnimation(this, DynamicAnimation.SCALE_X).apply {
        spring = frogSpringForce
    }
    val frogSpringAnimationScaleY = SpringAnimation(this, DynamicAnimation.SCALE_Y).apply {
        spring = frogSpringForce
    }

    val time = (startTime * 1000).toLong()
    Handler(Looper.getMainLooper()).postDelayed({
        val scalingFactor = .8f
        this.scaleX = scalingFactor
        this.scaleY = scalingFactor
        val finalPosition = 1f
        this.visibility = View.VISIBLE
        frogSpringAnimationScaleX.animateToFinalPosition(finalPosition)
        frogSpringAnimationScaleY.animateToFinalPosition(finalPosition)
    }, time)
}

fun View.springAnimationSingleXAxis(startTime: Float) {

    val frogSpringForce = SpringForce().apply {
        dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        stiffness = 400f
    }
    val frogSpringAnimationScaleX = SpringAnimation(this, DynamicAnimation.SCALE_X).apply {
        spring = frogSpringForce
    }
//    val frogSpringAnimationScaleY = SpringAnimation(this, DynamicAnimation.SCALE_Y).apply {
//        spring = frogSpringForce
//    }

    val time = (startTime * 1000).toLong()
    Handler(Looper.getMainLooper()).postDelayed({
        val scalingFactor = .8f
        this.scaleX = scalingFactor
        this.scaleY = scalingFactor
        val finalPosition = 1f
        this.visibility = View.VISIBLE
        frogSpringAnimationScaleX.animateToFinalPosition(finalPosition)
//        frogSpringAnimationScaleY.animateToFinalPosition(finalPosition)
    }, time)
}

fun View.springAnimationwithListener(startTime: Float,springAnimationwithListenerNavigator :SpringAnimationwithListenerNavigator) {


    springAnimationwithListenerNavigator.onStartAnimation()
    val frogSpringForce = SpringForce().apply {
        dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        stiffness = 400f
    }
    val frogSpringAnimationScaleX = SpringAnimation(this, DynamicAnimation.SCALE_X).apply {
        spring = frogSpringForce
    }
    val frogSpringAnimationScaleY = SpringAnimation(this, DynamicAnimation.SCALE_Y).apply {
        spring = frogSpringForce
    }

    frogSpringAnimationScaleY.addEndListener(object : DynamicAnimation.OnAnimationEndListener{
        override fun onAnimationEnd(
            animation: DynamicAnimation<out DynamicAnimation<*>>?,
            canceled: Boolean,
            value: Float,
            velocity: Float
        ) {

        }

    })

    frogSpringAnimationScaleX.addEndListener(object : DynamicAnimation.OnAnimationEndListener{
        override fun onAnimationEnd(
            animation: DynamicAnimation<out DynamicAnimation<*>>?,
            canceled: Boolean,
            value: Float,
            velocity: Float
        ) {
            springAnimationwithListenerNavigator.onEndAnimation()

        }

    })

    val time = (startTime * 1000).toLong()
    Handler(Looper.getMainLooper()).postDelayed({
        val scalingFactor = .8f
        this.scaleX = scalingFactor
        this.scaleY = scalingFactor
        val finalPosition = 1f
        this.visibility = View.VISIBLE
        frogSpringAnimationScaleX.animateToFinalPosition(finalPosition)
        frogSpringAnimationScaleY.animateToFinalPosition(finalPosition)
    }, time)
}

interface SpringAnimationwithListenerNavigator{
    fun onEndAnimation()
    fun onStartAnimation()
}


