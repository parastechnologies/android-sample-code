package com.in2bliss.workManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.in2bliss.R
import com.in2bliss.data.model.audioConversion.AudioConversion
import com.in2bliss.domain.AudioConverterStatusListenerInterface
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.convertAudio
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AudioConverterWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    @Assisted private val audioConverterStatusImpl: AudioConverterStatusListenerInterface
) : CoroutineWorker(
    context,
    workerParameters
) {

    private var toastMessage = "Converting audio please wait ..."
    private var notificationId: Int? = null
    private var channelId: String? = null

    override suspend fun doWork(): Result {
        showToast()

        try {
            setForeground(foregroundInfo())
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        updateAudioConversionStatus(
            isAudioConversionComplete = false,
            isFailed = false
        )

        val fileUri = inputData.getString(AppConstant.AUDIO) ?: ""

        val convertedData = convertAudio(
            filepath = fileUri.toUri(),
            activity = context
        )

        toastMessage =
            if (convertedData.status == 0) "Audio conversion complete" else "Audio conversion failed"
        showToast()

        createNotification(
            isConversionComplete = true
        )
        updateAudioConversionStatus(
            isAudioConversionComplete = true,
            isFailed = convertedData.status != 0,
            convertedFileName = convertedData.fileName,
            convertedFileUri = convertedData.fileUri
        )

        return Result.success()
    }

    /**
     * Updating the audio conversion status
     * @param isAudioConversionComplete is audio conversion complete
     * */
    private fun updateAudioConversionStatus(
        isAudioConversionComplete: Boolean,
        convertedFileUri: Uri? = null,
        convertedFileName: String? = null,
        isFailed: Boolean
    ) {

        audioConverterStatusImpl.changeAudioConvertStatus(
            AudioConversion(
                isConversionComplete = isAudioConversionComplete,
                convertedFileUri = convertedFileUri,
                isFailed = isFailed,
                convertedFilePath = convertedFileName
            )
        )
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return foregroundInfo()
    }

    private fun foregroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            notificationId ?: 0,
            createNotification()
        )
    }

    /**
     * Creating notification for foreground info and normal notify notification after complete
     * @param isConversionComplete is conversion complete
     * */
    private fun createNotification(
        isConversionComplete: Boolean = false
    ): Notification {

        val notification = getNotification(
            isConversionComplete = isConversionComplete
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(notificationChannel)

        if (isConversionComplete) {
            notificationManager.notify((0..10000).random(), notification)
        }

        return notification
    }

    /**
     * Creating notification
     * @param isConversionComplete is conversion complete
     * */
    private fun getNotification(
        isConversionComplete: Boolean
    ): Notification {
        notificationId = (1..10000).random()
        channelId = context.getString(R.string.app_name)

        return NotificationCompat.Builder(context, channelId ?: "")
            .setContentTitle(if (isConversionComplete) toastMessage else "Converting Audio ...")
            .setOngoing(!isConversionComplete)
            .setAutoCancel(!isConversionComplete)
            .setSmallIcon(R.mipmap.ic_app_logo)
            .build()
    }

    private suspend fun showToast() {
        withContext(Dispatchers.Main) {
//            Toast.makeText(
//                context, toastMessage, Toast.LENGTH_SHORT
//            ).show()
        }
    }
}