package com.mindbyromanzanoni.view.activity.openPdfViewer

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityOpenPdfBinding
import com.mindbyromanzanoni.utils.PdfViewAndDownload
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.utils.writeExternalStoragePermission
import com.rajat.pdfviewer.PdfEngine
import com.rajat.pdfviewer.PdfQuality
import com.rajat.pdfviewer.PdfRendererView
import java.util.Date

class OpenPdfActivity : BaseActivity<ActivityOpenPdfBinding>(), PdfRendererView.StatusCallBack {
    private var menuItem: MenuItem? = null
    private var fileUrl: String? = ""
    override fun getLayoutRes(): Int = R.layout.activity_open_pdf
    private var pdfViewOrDownload: PdfViewAndDownload? = PdfViewAndDownload()
    override fun initView() {
        engine = PdfEngine.INTERNAL
        getInitData()
        init()
    }

    private fun getInitData() {
        val intent = intent.extras
        if (intent != null) {
            fileUrl = intent.getString(AppConstants.PDF_URL)
        }
    }

    override fun viewModel() {}

    companion object {
        var engine = PdfEngine.INTERNAL
        var enableDownload = true
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.toolbar.tvToolTitle.text = "Resource"
        binding.toolbar.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.ivNotification.visible()
        binding.toolbar.ivNotification.setImageResource(R.drawable.ic_download)
        loadFileFromNetwork(fileUrl)
        binding.toolbar.ivNotification.setOnClickListener {
            writeExternalStoragePermission(applicationContext, {
                pdfViewOrDownload?.startDownload(
                    applicationContext,
                    fileUrl.toString(),
                    "Resource_" + Date().time
                )
            }, {})
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuItem?.isVisible = enableDownload
        return true
    }

    private fun loadFileFromNetwork(fileUrl: String?) {
        initPdfViewer(fileUrl, engine)
    }

    private fun initPdfViewer(fileUrl: String?, engine: PdfEngine) {
        if (TextUtils.isEmpty(fileUrl)) onPdfError()
        try {
            binding.pdfView.initWithUrl(fileUrl!!, PdfQuality.NORMAL, engine)
            binding.pdfView.statusListener = this
        } catch (e: Exception) {
            onPdfError()
        }
    }

    override fun onDownloadStart() {
        super.onDownloadStart()
        binding.progress.visible()

    }
    override fun onDownloadSuccess() {
        super.onDownloadSuccess()
        binding.progress.gone()

    }
    private fun onPdfError() {
        Toast.makeText(this, "Pdf has been corrupted", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.pdfView.closePdfRender()
    }

}