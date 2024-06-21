package com.highenergymind.base

import androidx.fragment.app.DialogFragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.highenergymind.R
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.loader.LoaderDialogBox
import com.highenergymind.utils.showToast

@Suppress("UNREACHABLE_CODE")
abstract class BaseDialogFragment<DB : ViewDataBinding> : DialogFragment() {
    open lateinit var mBinding: DB
    private lateinit var loadingDialogBox: LoaderDialogBox

    @LayoutRes
    abstract fun getLayoutRes(): Int

    abstract fun init()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        loadingDialogBox = LoaderDialogBox()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        init()
        return mBinding.root
    }




    fun progressDialog(visibility: Boolean) {
        if (visibility) {
            loadingDialogBox.show(parentFragmentManager, "loaderFragment")
        } else {
            loadingDialogBox.dismiss()
        }
    }
    fun handleResponse(
        response: ResponseResult<ResponseWrapper>,
        success: (Any?) -> Unit, failure: (() -> Unit)? = null
    ) {
        when (response) {
            is ResponseResult.SUCCESS -> {
                success(response.result.data)
            }

            is ResponseResult.FAILURE -> {
                failure?.invoke()
                Log.i("TAGGGG", "handleResponse: "+response.msg.errorMsg.toString())
                requireContext().showToast(response.msg.errorMsg.toString())
            }

            is ResponseResult.ERROR -> {
                requireContext().showToast(response.result.errorMsg.toString())
                Log.e("error", "handleResponse: ${response.result.errorMsg}")
            }

            is ResponseResult.UNAUTHORIZED -> {
                requireContext().showToast(response.result.errorMsg.toString())
            }
        }
    }
}