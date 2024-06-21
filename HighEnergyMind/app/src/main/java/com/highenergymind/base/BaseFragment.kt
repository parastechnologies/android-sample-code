package com.highenergymind.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.loader.LoaderDialogBox
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.login.LoginActivity

abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {

    open lateinit var mBinding: DB
    private lateinit var loadingDialogBox: LoaderDialogBox
    val validator by lazy {
        Validator()
    }

    /**
     * @param v
     * this function is use for hide the keyboard
     * */

     fun hideKeyboard(v: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }



    open fun init() {}

    @LayoutRes
    abstract fun getLayoutRes(): Int

    abstract fun initViewWithData()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        loadingDialogBox = LoaderDialogBox()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewWithData()
    }



    override fun onResume() {
        super.onResume()
        view?.let {
            hideKeyboard(it)
        }
    }
    fun progressDialog(visibility: Boolean) {
        if (visibility && !loadingDialogBox.isAdded) {
            loadingDialogBox.show(parentFragmentManager, "loader")
            parentFragmentManager.executePendingTransactions()
        } else {
            if (loadingDialogBox.isAdded) {
                loadingDialogBox.dismiss()
            }
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
                requireContext().intentComponent(LoginActivity::class.java, Bundle().also {
                    it.putString(ApiEndPoint.LOG_OUT, "")
                })
                requireActivity().finishAffinity()
                requireContext().showToast(response.result.errorMsg.toString())
            }
        }
    }

}