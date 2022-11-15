package com.app.muselink.ui.base.dialogfragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.app.muselink.ui.base.activity.BaseViewModel

abstract class BaseViewModelDialogFragment<B : ViewDataBinding, VM : BaseViewModel> :
    BaseDialogFragment<B>() {

    protected var viewModel: VM? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(getViewModelClass())
    }

    protected abstract fun getViewModelClass(): Class<VM>
}