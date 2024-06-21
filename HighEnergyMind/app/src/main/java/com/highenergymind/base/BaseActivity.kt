package com.highenergymind.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.api.ResponseResult
import com.highenergymind.api.ResponseWrapper
import com.highenergymind.di.ApplicationClass
import com.highenergymind.loader.LoaderDialogBox
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.LocaleHelper
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.showToast
import com.highenergymind.view.activity.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {


    private var isAnimate: Boolean = false
    private lateinit var loadingDialogBox: LoaderDialogBox
    val validator by lazy {
        Validator()
    }

    fun hideKeyboard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    lateinit var binding: DB

    abstract fun initView()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocalization()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//Set Portrait
        binding = DataBindingUtil.setContentView(this, getLayoutRes()) as DB
        loadingDialogBox = LoaderDialogBox()
        initView()
    }

    private fun setLocalization() {
        LocaleHelper.setLocale(this, (application as ApplicationClass).selectedLanguage)
    }

    fun progressDialog(visibility: Boolean) {
        if (visibility && !loadingDialogBox.isAdded) {
            loadingDialogBox.show(supportFragmentManager, "loader")
            supportFragmentManager.executePendingTransactions()
        } else {
            if (loadingDialogBox.isAdded) {
                loadingDialogBox.dismiss()
            }
        }
    }

    fun handleResponse(
        response: ResponseResult<ResponseWrapper>,
        success: (Any?) -> Unit, isShowToast: Boolean = true, failure: (() -> Unit)? = null
    ) {
        when (response) {
            is ResponseResult.SUCCESS -> {
                success(response.result.data)
            }

            is ResponseResult.FAILURE -> {
                failure?.invoke()
                Log.i("TAGGGG", "handleResponse: " + response.msg.errorMsg.toString())
                showToast(response.msg.errorMsg.toString())
            }

            is ResponseResult.ERROR -> {
                if (isShowToast) {
                    showToast(response.result.errorMsg.toString())
                }
                Log.e("error", "handleResponse: ${response.result.errorMsg}")
            }

            is ResponseResult.UNAUTHORIZED -> {
                intentComponent(LoginActivity::class.java, Bundle().also {
                    it.putString(ApiEndPoint.LOG_OUT, "")
                })
                finishAffinity()
                showToast(response.result.errorMsg.toString())
            }
        }
    }

}