package com.app.muselink.ui.base.activity

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.app.muselink.base.BaseActivityBinding

abstract class BaseViewModelActivity<B : ViewDataBinding, VM : BaseViewModel> :
    BaseActivityBinding<B>() {
    var TAG: String? = null
    protected var viewModel: VM? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = javaClass.simpleName
        viewModel = ViewModelProvider(this).get(getViewModelClass())
    }
    protected abstract fun getViewModelClass(): Class<VM>
}