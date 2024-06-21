package com.mindbyromanzanoni.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.util.Date
import java.util.Locale

class PdfViewAndDownload {
    fun startDownload(context: Context, pdfUrl: String, pdfFileName: String) {
        val newFileName = pdfFileName.replace(" ", "_").trim().lowercase(Locale.ROOT)+Date().time+".pdf"
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
            .setTitle(newFileName)
            .setDescription("Downloading a PDF file")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true) // Set to true if you want to allow download over mobile data
            .setAllowedOverRoaming(true) // Set to true if you want to allow download while roaming

        val destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(destination, newFileName)
        request.setDestinationUri(Uri.fromFile(file))
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        ID = downloadManager.enqueue(request)
    }
    companion object {
        var ID: Long? = 0L
    }
}
class OnDownloadComplete : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        //Fetching the download id received with the broadcast
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        //Checking if the received broadcast is for our enqueued download by matching download id
        if (PdfViewAndDownload.ID == id) {
            Toast.makeText(context, "Download successfully", Toast.LENGTH_SHORT).show()
        }
    }
}

