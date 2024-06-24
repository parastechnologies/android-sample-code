package com.in2bliss.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.utils.extension.hideKeyboard

abstract class BaseActivity<VB : ViewDataBinding>(
    private val layout: Int
) : AppCompatActivity() {

    private lateinit var viewBinding: VB
    val binding: VB
        get() = viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, layout)
        init()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        ProgressDialog.hideProgress()
    }

    abstract fun init()
}