package com.app.muselink.auth_integrations

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import kotlinx.android.synthetic.main.activity_webview_auth.*

class SoundCloudActivity : BaseActivity() {

    lateinit var code: String

    override fun getLayout(): Int {
        return R.layout.activity_webview_auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    fun initViews(){
        webView?.isVerticalScrollBarEnabled = false
        webView?.isHorizontalScrollBarEnabled = false
        webView?.settings?.javaScriptEnabled = true
        webView?.loadUrl(AuthConstants.authURLFull_SOUND_CLOUD)
        webView?.webViewClient = MyWVClient()
    }

    internal inner class MyWVClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (request.url.toString().startsWith(AuthConstants.REDIRECT_URI)) {
                handleUrl(request.url.toString())
                return true
            }
            return false
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(AuthConstants.REDIRECT_URI)) {
                handleUrl(url)
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    fun handleUrl(url: String) {
        if (url.contains("code")) {
            val temp = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            code = temp[1]
            Log.d("asasasas", code)
//            MyAsyncTask(code, this).execute()
        } else if (url.contains("error")) {
            val temp = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
    }


}