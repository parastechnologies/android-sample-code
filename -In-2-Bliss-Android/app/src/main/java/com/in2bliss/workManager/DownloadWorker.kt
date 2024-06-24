package com.in2bliss.workManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.downloadResponse.DownloadFile
import com.in2bliss.data.model.musicDetails.MusicDetails
import com.in2bliss.data.roomDataBase.OfflineAudioEntity
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.domain.DownloadFIleInInternalStorageInterface
import com.in2bliss.domain.DownloadStatusListenerInterface
import com.in2bliss.domain.RoomDataBaseInterface
import com.in2bliss.ui.activity.home.profileManagement.download.DownloadActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.gettingAudioUrl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val downloadStatusListener: DownloadStatusListenerInterface,
    @Assisted private val roomDatabase: RoomDataBaseInterface,
    @Assisted private val downloadHelper: DownloadFIleInInternalStorageInterface,
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    @Assisted private val sharedPreference: SharedPreference
) : CoroutineWorker(
    context,
    workerParameters
) {

    private var notificationId: Int? = null
    private var channelId: String? = null
    private var musicDetails: MusicDetails? = null
    private var categoryName: AppConstant.HomeCategory? = null
    private var toastMessage = "Downloading ..."

    override suspend fun doWork(): Result {

        musicDetails = try {
            val data = inputData.getString(AppConstant.MUSIC_DETAILS)
            Gson().fromJson(data, MusicDetails::class.java)
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }

        showToast()
        updateDownloadStatus(
            isDownloading = true,
            isFailed = false
        )

        gettingCategoryType()

        withContext(Dispatchers.IO) {
            if (isBackMusicIsEmptyOrNot()) {
                downloadFiles(
                    musicUrl = getMusicCompleteUrl(),
                    imageUrl = getImageCompleteUrl(),
                    backMusic = getBackMusicUrl(),
                    backImageUrl = getBackImageUrl()
                )
            } else {
                downloadFiles(
                    musicUrl = getMusicCompleteUrl(),
                    imageUrl = getImageCompleteUrl()
                )
            }


        }

        creatingNotification(
            isDownloadNotification = true
        )
        showToast()
        return Result.success()
    }

    private suspend fun showToast() {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context, toastMessage, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun gettingCategoryType() {
        categoryName = when (inputData.getString(AppConstant.CATEGORY_NAME)) {
            AppConstant.HomeCategory.GUIDED_AFFIRMATION.name -> AppConstant.HomeCategory.GUIDED_AFFIRMATION
            AppConstant.HomeCategory.GUIDED_MEDITATION.name -> AppConstant.HomeCategory.GUIDED_MEDITATION
            AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
            AppConstant.HomeCategory.GUIDED_SLEEP.name -> AppConstant.HomeCategory.GUIDED_SLEEP
            AppConstant.HomeCategory.MUSIC.name -> AppConstant.HomeCategory.MUSIC
            AppConstant.HomeCategory.DOWNLOAD.name -> AppConstant.HomeCategory.DOWNLOAD
            AppConstant.HomeCategory.WISDOM_INSPIRATION.name -> AppConstant.HomeCategory.WISDOM_INSPIRATION
            else -> null
        }
    }

    private fun getImageCompleteUrl(): String {
        return getImageUrl(
            category = categoryName,
            image = musicDetails?.musicBackground
        )
    }

    private fun getBackImageUrl(): String {
        return BuildConfig.MUSIC_BASE_URL.plus(musicDetails?.musicCustomizeDetail?.backgroundMusicImage)
    }

    private fun getBackMusicUrl(): String {
        return if (musicDetails?.musicCustomizeDetail?.backgroundMusicUrl != null) {
            BuildConfig.MUSIC_BASE_URL.plus(musicDetails?.musicCustomizeDetail?.backgroundMusicUrl)
        } else {
            if (categoryName == AppConstant.HomeCategory.CREATE_AFFIRMATION) {
                BuildConfig.MUSIC_BASE_URL.plus(musicDetails?.backgroundMusicUrl)
            } else {
                getImageUrl(
                    category = categoryName,
                    image = musicDetails?.backgroundMusicUrl
                )
            }
        }
    }


    private fun getMusicCompleteUrl(): String {
        return gettingAudioUrl(
            category = categoryName,
            audio = musicDetails?.musicUrl
        )
    }

    private fun isBackMusicIsEmptyOrNot(): Boolean {
        return !musicDetails?.backgroundMusicUrl.isNullOrEmpty()
    }

    /**
     * Downloading music and image
     * @param musicUrl
     * @param imageUrl
     * */
    private suspend fun downloadFiles(
        musicUrl: String,
        imageUrl: String,
    ) {

        /**
         *  If audio url is null or empty terminating the download
         *  */
        if (musicDetails?.musicUrl.isNullOrEmpty()) {
            toastMessage = "Download failed"
            updateDownloadStatus(
                isDownloading = false,
                isFailed = true
            )
            return
        }

        withContext(Dispatchers.IO) {
            val music = async {
                downloadHelper.writeToInternalStorage(
                    url = musicUrl,
                    context = context,
                    downloadStatus = {
                        updateDownloadStatus(
                            isDownloading = true,
                            isFailed = false
                        )
                    }
                )
            }


            val image = async {
                downloadHelper.writeToInternalStorage(
                    url = imageUrl,
                    context = context,
                    downloadStatus = {
                        updateDownloadStatus(
                            isDownloading = true,
                            isFailed = false
                        )
                    }
                )
            }

            val isDownloadSuccess =
                (music.await()?.filePath != null && music.await()?.fileName != null)

            if (isDownloadSuccess) {
                saveInRoomDatabase(
                    imageData = image.await(),
                    musicData = music.await()
                )
            }

            toastMessage = if (isDownloadSuccess) "Download Complete" else "Download failed"

            updateDownloadStatus(
                isDownloading = false,
                isFailed = isDownloadSuccess.not()
            )
        }
    }


    /**
     * Downloading music and image
     * @param musicUrl
     * @param imageUrl
     * */
    private suspend fun downloadFiles(
        musicUrl: String,
        imageUrl: String,
        backMusic: String? = null,
        backImageUrl: String? = null
    ) {

        /**
         *  If audio url is null or empty terminating the download
         *  */
        if (musicDetails?.musicUrl.isNullOrEmpty()) {
            toastMessage = "Download failed"
            updateDownloadStatus(
                isDownloading = false,
                isFailed = true
            )
            return
        }

        withContext(Dispatchers.IO) {
            val music = async {
                downloadHelper.writeToInternalStorage(
                    url = musicUrl,
                    context = context,
                    downloadStatus = {
                        updateDownloadStatus(
                            isDownloading = true,
                            isFailed = false
                        )
                    }
                )
            }

            val backgroundMusic = async {
                downloadHelper.writeToInternalStorage(
                    url = backMusic ?: "",
                    context = context,
                    downloadStatus = {
                        updateDownloadStatus(
                            isDownloading = true,
                            isFailed = false
                        )
                    }
                )
            }


            val image = async {
                downloadHelper.writeToInternalStorage(
                    url = imageUrl,
                    context = context,
                    downloadStatus = {
                        updateDownloadStatus(
                            isDownloading = true,
                            isFailed = false
                        )
                    }
                )
            }
            val backImageUrl = async {
                downloadHelper.writeToInternalStorage(
                    url = backImageUrl ?: "",
                    context = context,
                    downloadStatus = {
                        updateDownloadStatus(
                            isDownloading = true,
                            isFailed = false
                        )
                    }
                )
            }

            val isDownloadSuccess =
                (music.await()?.filePath != null && music.await()?.fileName != null)

            if (isDownloadSuccess) {
                saveInRoomDatabase(
                    imageData = image.await(),
                    musicData = music.await(),
                    backMusic = backgroundMusic.await(),
                    backImageUrl = backImageUrl.await()
                )
            }

            toastMessage = if (isDownloadSuccess) "Download Complete" else "Download failed"

            updateDownloadStatus(
                isDownloading = false,
                isFailed = isDownloadSuccess.not()
            )
        }
    }

    /**
     * Updating downloading status
     * @param isDownloading is download is in progress or completed
     * @param isFailed is error occurred while downloading
     * */
    private fun updateDownloadStatus(
        isDownloading: Boolean,
        isFailed: Boolean
    ) {
        var status =
            if (isDownloading) AppConstant.DownloadStatus.DOWNLOADING else AppConstant.DownloadStatus.DOWNLOAD_COMPLETE
        if (isFailed) status = AppConstant.DownloadStatus.NOT_DOWNLOAD

        downloadStatusListener.setDownloadStatus(
            status = status,
            musicDetails = musicDetails
        )
    }

    /**
     * Saving data in room data base
     * @param imageData containing image data to save
     * @param musicData containing music data to save
     * */
    private suspend fun saveInRoomDatabase(
        imageData: DownloadFile?,
        musicData: DownloadFile?,
        backMusic: DownloadFile? = null,
        backImageUrl: DownloadFile? = null
    ) {
        try {
            withContext(Dispatchers.IO) {
                val backGroundMusicTitle: String? =
                    if (musicDetails?.musicCustomizeDetail?.backgroundMusicTitle != null) {
                        musicDetails?.musicCustomizeDetail?.backgroundMusicTitle.toString()
                    } else {
                        musicDetails?.backgroundMusicTitle
                    }

                val data = OfflineAudioEntity(
                    id = musicDetails?.musicId ?: 0,
                    title = musicDetails?.musicTitle.orEmpty(),
                    description = musicDetails?.musicDescription.orEmpty(),
                    musicFilePath = musicData?.filePath.orEmpty(),
                    musicFileName = musicData?.fileName.orEmpty(),
                    musicImageFileName = imageData?.fileName.orEmpty(),
                    musicImageFilePath = imageData?.filePath.orEmpty(),
                    musicUrl = musicDetails?.musicUrl.orEmpty(),
                    isDelete = false,
                    userId = sharedPreference.userData?.data?.id ?: 0,
                    backMusicFilePath = backMusic?.filePath.orEmpty(),
                    backMusicFileName = backMusic?.fileName.orEmpty(),
                    backMusicImageFilePath = backImageUrl?.filePath.orEmpty(),
                    backMusicImageFileName = backImageUrl?.fileName.orEmpty(),
                    backgroundMusicTitle = backGroundMusicTitle,
                    downloadCategoryName = categoryName,
                    isCustomizationEnabled = musicDetails?.isCustomizationEnabled ?: false,
                    customHourAffirmation = musicDetails?.musicCustomizeDetail?.affirmationHour,
                    customMinAffirmation = musicDetails?.musicCustomizeDetail?.affirmationMinute,
                    customHourMusic = musicDetails?.musicCustomizeDetail?.affirmationHour,
                    customMinMusic = musicDetails?.musicCustomizeDetail?.affirmationMinute,

                    )
                roomDatabase.insert(
                    data = data
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return foregroundInfo()
    }

    private fun foregroundInfo(): ForegroundInfo {

        notificationId = (0..10000).random()
        channelId = context.getString(R.string.app_name)

        return ForegroundInfo(
            notificationId ?: 1,
            creatingNotification()
        )
    }

    private fun creatingNotification(
        isDownloadNotification: Boolean = false
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context, 0, Intent(
                context, DownloadActivity::class.java
            ), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = creatingNotificationBuilder(
            isDownloadNotification = isDownloadNotification,
            pendingIntent = pendingIntent
        )
        val notificationManger =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManger.createNotificationChannel(notificationChannel)

        if (isDownloadNotification) {
            notificationManger.notify((0..10000).random(), notification)
        }
        return notification
    }

    private fun creatingNotificationBuilder(
        isDownloadNotification: Boolean = false,
        pendingIntent: PendingIntent
    ): Notification {
        val notification = NotificationCompat.Builder(context, channelId.orEmpty())
            .setContentTitle(if (isDownloadNotification) toastMessage else "Downloading ...")
            .setSmallIcon(R.mipmap.ic_app_logo)
            .setOngoing(!isDownloadNotification)
            .setAutoCancel(!isDownloadNotification)
        if (isDownloadNotification) notification.setContentIntent(pendingIntent)
        return notification.build()
    }
}