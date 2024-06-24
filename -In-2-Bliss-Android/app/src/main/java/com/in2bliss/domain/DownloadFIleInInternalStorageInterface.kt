package com.in2bliss.domain

import android.content.Context
import com.in2bliss.data.model.downloadResponse.DownloadFile
import java.io.File

interface DownloadFIleInInternalStorageInterface {

    /**
     * Download and the file in internal storage the file url is passed
     * @param url url of the file which gonna download and save
     * @param context
     * @param downloadStatus how much download is complete
     * */
    suspend fun writeToInternalStorage(
        url: String,
        context: Context,
        downloadStatus: () -> Unit
    ): DownloadFile?

    /**
     * Read data from the internal storage
     * */
    fun readFromInternalStorage(
        context: Context
    ): List<File>

    /**
     * Delete file internal storage
     * @param context
     * @param fileName filename which want to delete
     * @param isComplete is file delete status
     * */
    fun deleteFile(
        fileName: String,
        context: Context,
        isMusicDirectory :Boolean ,
        isComplete: (Boolean) -> Unit
    )
}