package com.app.muselink.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.app.muselink.ui.base.activity.BaseViewModel

abstract class BaseViewModelFragment <B : ViewDataBinding, VM : BaseViewModel> :
    BaseFragmentBinding<B>() {

    protected var viewModel: VM? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(getViewModelClass())
    }

    protected abstract fun getViewModelClass(): Class<VM>

}