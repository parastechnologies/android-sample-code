package com.in2bliss.data.downloadFileInInternalStorage

import android.content.Context
import android.os.Environment
import com.in2bliss.R
import com.in2bliss.data.model.downloadResponse.DownloadFile
import com.in2bliss.domain.DownloadFIleInInternalStorageInterface
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.io.File
import java.io.FileOutputStream

class DownloadFileInInternalStorageImp : DownloadFIleInInternalStorageInterface {

    /**
     * Download and the file in internal storage the file url is passed
     * @param url url of the file which gonna download and save
     * @param context
     * @param downloadStatus how much download is complete
     * */
    override suspend fun writeToInternalStorage(
        url: String,
        context: Context,
        downloadStatus: () -> Unit
    ): DownloadFile? {
        return try {

            var downloadFileData: DownloadFile? = null

            val fileName =
                if (File(url).name.contains(url.takeLast(3))) {
                    File(url).name
                } else "${File(url).name}.${url.takeLast(3)}"

            val file = File(context.filesDir, fileName)

            val request = Request.Builder()
                .url(url)
                .build()
            val client = OkHttpClient.Builder()
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    downloadFileData = DownloadFile(
                        filePath = null,
                        fileName = null
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    val inputStream = response.body.byteStream()
                    FileOutputStream(file).use { outPutStream ->
                        outPutStream.write(inputStream.readBytes())
                    }
                    downloadFileData = DownloadFile(
                        fileName = fileName,
                        filePath = file.path
                    )
                }
            })

            while (downloadFileData == null) {
                downloadStatus.invoke()
                delay(100)
            }

            return downloadFileData

        } catch (exception: Exception) {

            exception.printStackTrace()
            DownloadFile(
                fileName = null,
                filePath = null
            )
        }
    }

    /**
     * Read data from the internal storage
     * */
    override fun readFromInternalStorage(context: Context): List<File> {
        val files = context.filesDir.listFiles()
        return files?.filter { it.canRead() && it.isFile && it.path.endsWith(".mp3") } ?: listOf()
    }

    /**
     * Delete file internal storage
     * @param context
     * @param fileName filename which want to delete
     * @param isComplete is file delete status
     * */
    override fun deleteFile(
        fileName: String,
        context: Context,
        isMusicDirectory: Boolean,
        isComplete: (Boolean) -> Unit
    ) {
        try {
            val directory =
                if (isMusicDirectory) File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                    context.getString(
                        R.string.app_name
                    )
                ) else context.filesDir
            val file = File(directory, fileName)
            file.delete()
            isComplete.invoke(true)
        } catch (exception: Exception) {
            isComplete.invoke(false)
        }
    }
}