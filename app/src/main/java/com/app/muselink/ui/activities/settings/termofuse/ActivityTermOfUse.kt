package com.app.muselink.ui.activities.settings.termofuse

import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.util.finishActivity
import kotlinx.android.synthetic.main.activity_term_of_use.*
import kotlinx.android.synthetic.main.layout_toolbar_with_back_button.*
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class ActivityTermOfUse : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_term_of_use
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.runOnUiThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webviewTerm.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webviewTerm.settings.javaScriptEnabled = true
            webviewTerm.loadUrl("https://www.muselink.app/")

        }

        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
        tvHeading.visibility = View.VISIBLE
        tvHeading.text = getString(R.string.term_use)

    }

    private fun readAndDisplayFileContentFromAssetsFolder(): String? {
        val mgr = assets
        var fileName: String? = null
        var sHTML: String? = null
        try {
            fileName = "tandc.html"
            val `in` = mgr.open(fileName, AssetManager.ACCESS_BUFFER)
            sHTML = StreamToString(`in`)
            `in`.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return sHTML
    }

    @Throws(IOException::class)
    fun StreamToString(inputStream: InputStream?): String {
        if (inputStream == null) {
            return ""
        }
        val content = inputStream.readBytes().toString(Charset.defaultCharset())
        return content
    }

    override fun onBackPressed() {
        finishActivity()
    }

}