package com.mindbyromanzanoni.base

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mindbyromanzanoni.utils.OnDownloadComplete
import com.mindbyromanzanoni.utils.PdfViewAndDownload
import com.mindbyromanzanoni.utils.finishActivity

abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {
    private var onDownloadComplete = OnDownloadComplete()
    @LayoutRes
    abstract fun getLayoutRes(): Int
    val binding by lazy {
        DataBindingUtil.setContentView(this, getLayoutRes()) as DB
    }
    abstract fun initView()
    abstract fun viewModel()
    @RequiresApi(Build.VERSION_CODES.R)
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        initView()
        viewModel()
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onDownloadComplete,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),RECEIVER_EXPORTED)
        }else{
            registerReceiver(onDownloadComplete,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }
    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(onDownloadComplete)
        }catch (_:Exception){}
    }
    /*override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(onDownloadComplete)
        }catch (_:Exception){}

    }*/
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishActivity()
        }
    }
    fun hideKeyboard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}