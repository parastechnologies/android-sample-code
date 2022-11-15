package com.app.muselink.ui.activities.settings.privacypolicy

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.util.finishActivity
import kotlinx.android.synthetic.main.activity_privacypolicy.*
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class ActivityPrivacyPolicy : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_privacypolicy
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.runOnUiThread {
            webviewPrivacy.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webviewPrivacy.settings.javaScriptEnabled = true
            webviewPrivacy.webViewClient = WebViewClient()
            readAndDisplayFileContentFromAssetsFolder()?.let {
                webviewPrivacy.loadDataWithBaseURL(null,it, "text/html", "UTF-8", null)
            }
            webviewPrivacy.setBackgroundColor(ContextCompat.getColor(this, R.color.color_pale_gray))
        }
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.visibility = View.VISIBLE
        tvHeading.text = getString(R.string.privacy_policy)
    }
    private fun readAndDisplayFileContentFromAssetsFolder(): String? {
        val mgr = assets
        val fileName: String?
        var sHTML: String? = null
        try {
            fileName = "privacy_policy.html"
            val `in` = mgr.open(fileName, AssetManager.ACCESS_BUFFER)
            sHTML = streamToString(`in`)
            `in`.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return sHTML
    }
    @Throws(IOException::class)
    fun streamToString(inputStream: InputStream?): String {
        if (inputStream == null) {
            return ""
        }
        return inputStream.readBytes().toString(Charset.defaultCharset())
    }
    override fun onBackPressed() {
        finishActivity()
    }
}