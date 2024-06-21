package com.mindbyromanzanoni.view.activity.eventDetails

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.text.Html
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.eventDetails.Details
import com.mindbyromanzanoni.data.response.eventDetails.WhoLikesFavouriteList
import com.mindbyromanzanoni.databinding.ActivityEventDetailBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.PdfViewAndDownload
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.convertDateFormat
import com.mindbyromanzanoni.utils.convertTimeFormat
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.launchActivityVideoZoom
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.shareEvent
import com.mindbyromanzanoni.utils.shimmerAnimationEffect
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.startZoomMeeting
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.utils.writeExternalStoragePermission
import com.mindbyromanzanoni.videoOrAudioControls.MediaCache
import com.mindbyromanzanoni.view.activity.landscapeVideoPlay.ImageViewActivity
import com.mindbyromanzanoni.view.activity.openPdfViewer.OpenPdfActivity
import com.mindbyromanzanoni.view.bottomsheet.comments.BottomSheetComments
import com.mindbyromanzanoni.view.bottomsheet.likes.BottomSheetLikes
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("SourceLockedOrientationActivity")
@AndroidEntryPoint
class EventDetailActivity : BaseActivity<ActivityEventDetailBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@EventDetailActivity
    private var position: Int = -1
    private var eventDetails: Details? = null
    private var player: ExoPlayer? = null
    private lateinit var htmlString: String
    private var pdfViewOrDownload: PdfViewAndDownload? = null

    companion object {
        lateinit var listCallBackSheet: (position: Int, Boolean, commentCount: Int, likeCount: Int, isLikedStatus: Int) -> Unit
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_event_detail
    }

    override fun initView() {
        setToolbar()
        getIntentData()
        initialisePdfViewClass()
        clickListeners()
        observeDataFromViewModal()
        apiHit()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun callBackPdfViewDownload() {
        writeExternalStoragePermission(applicationContext, {
            pdfViewOrDownload?.startDownload(
                applicationContext,
                eventDetails?.docFileName.toString(),
                eventDetails?.title.toString()
            )
        }, { })
    }

    private fun initialisePdfViewClass() {
        pdfViewOrDownload = PdfViewAndDownload()
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun clickListeners() {
        binding.apply {
            tvLikedBy.setOnClickListener {
                val bottomSheetLikes = BottomSheetLikes(viewModal.eventId.get().toString())
                bottomSheetLikes.show(supportFragmentManager, "")
            }
            ivShare.setOnClickListener {
                (ivShare.context as Activity).shareEvent(
                    viewModal.eventId.get().toString().toInt(),
                    4
                )
            }
            tvViewAllComments.setOnClickListener {
                val bottomSheetComments =
                    BottomSheetComments(viewModal.eventId.get().toString(), type = 4)
                bottomSheetComments.show(supportFragmentManager, "")
                bottomSheetComments.dismissCallBackSheet = { status, count ->
                    if (status) {
                        eventDetails?.totalComments = count
                        if (count <= 0) {
                            tvViewAllComments.text = getString(R.string.no_comment_yet)
                        } else {
                            tvViewAllComments.text = getString(R.string.view_all).plus("comments")
                        }
                        listCallBackSheet.invoke(
                            position,
                            true,
                            count,
                            eventDetails?.totalFavourites ?: 0,
                            0
                        )
                    }
                }
            }
            ivFavourite.setOnClickListener {
                viewModal.type.set(4)
                if (eventDetails?.isFavoritedbyUser == true) {
                    viewModal.isFavourite.set(false)
                } else {
                    viewModal.isFavourite.set(true)
                }
                RunInScope.ioThread {
                    viewModal.hitUpdateFavouriteEventStatusApi()
                }
            }
            layout1.setOnClickListener {
                startZoomMeeting(this@EventDetailActivity, "2324")
            }

            ivPlayOrPause.setOnClickListener {
                if (player?.isPlaying == true) {
                    handlingPlayIcon(false)
                } else if (player?.isPlaying == false) {
                    handlingPlayIcon(true)
                }
                ivPlayOrPause.gone()
            }
            /* videoView.setOnClickListener {
                 if (player?.isLoading == false) {
                     if (player?.isPlaying == true) {
                         handlingPlayIcon(false)
                     } else if (player?.isPlaying == false) {
                         handlingPlayIcon(true)
                     }
                 }
             }*/
            binding.arrowDown.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            icZoom.setOnClickListener {
                if (eventDetails?.isImage == false) {
                    val orientation = getResources().configuration.orientation
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        binding.ivShare.gone()
                    } else {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        binding.ivShare.visible()
                    }
                } else {
                    val bundle = bundleOf(AppConstants.VIDEO_URL to eventDetails?.mediaPath)
                    launchActivityVideoZoom<ImageViewActivity>(0, bundle) { }
                }
            }
            icDownloadPdf.setOnClickListener {
                callBackPdfViewDownload()
            }
            relativeLayout.setOnClickListener {
                val bundle = bundleOf(AppConstants.PDF_URL to eventDetails?.docFileName)
                launchActivity<OpenPdfActivity>(0, bundle) { }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustFullScreen(newConfig)
    }

    private fun adjustFullScreen(config: Configuration) {
        val insetsController = ViewCompat.getWindowInsetsController(window.decorView)
        insetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            insetsController?.hide(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.ctlRoot.gone()
            binding.ivFavourite.gone()
            binding.layout1.gone()
            binding.arrowDown.visible()
            binding.ivShare.gone()
            setFullScreenLayout()
        } else {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.ctlRoot.visible()
            binding.ivFavourite.visible()
            binding.layout1.visible()
            binding.arrowDown.gone()
            binding.ivShare.visible()
            setNormalLayout()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            if (player?.isPlaying == true) {
                player?.pause()
            } else {
                player?.play()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                updatePictureInPictureParams()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.icZoom.gone()
            binding.arrowDown.gone()
            val filter = IntentFilter()
            filter.addAction("PLAY_PAUSE")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(receiver, filter)
            }
            // binding.videoView.useController = false

        } else {
            binding.icZoom.visible()
            unregisterReceiver(receiver)
            // binding.videoView.useController = true
            binding.arrowDown.visible()
        }
    }

    private fun setFullScreenLayout() {
        binding.videoView.setLayoutParams(
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        binding.icZoom.setImageResource(R.drawable.portrait_svg)
    }

    private fun setNormalLayout() {
        binding.videoView.setLayoutParams(
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                this.resources.getDimension(com.intuit.sdp.R.dimen._180sdp).toInt()
            )
        )
        binding.icZoom.setImageResource(R.drawable.ic_fullscreen_icn)
    }

    private fun isVideo(mediaPath: String?): Boolean {
        val splitArray: List<String>? = mediaPath?.split(".")
        val extension = splitArray?.get(splitArray.size - 1)
        return extension.equals("mp4") || extension.equals("flv") || extension.equals("m4a") || extension.equals(
            "3gp"
        ) || extension.equals("mkv")
    }

    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            viewModal.eventId.set(intent.getInt(AppConstants.EVENT_ID).toString())
            position = intent.getInt(AppConstants.POSITION)
        }
    }

    private fun apiHit() {
        RunInScope.ioThread {
            viewModal.hitEventDetailsApi()
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.event_details)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.eventDetailsSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            eventDetails = data.data
                            setDataApi(data.data)
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(activity, msg) }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModal.favouriteEventStatusSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if (eventDetails?.isFavoritedbyUser == true) {
                                eventDetails?.isFavoritedbyUser = false
                                binding.apply {
                                    eventDetails?.totalFavourites =
                                        eventDetails?.totalFavourites?.minus(1)!!
                                    ivFavourite.setBackgroundResource(R.drawable.ic_fav_dislike)
                                }
                                val index =
                                    eventDetails?.usersListWhoFavouriteEvent?.indexOfLast { it.userId == viewModal.sharedPrefs.getUserData()?.userId }
                                        ?: -1
                                if (index != -1) {
                                    eventDetails?.usersListWhoFavouriteEvent?.removeAt(index)
                                }
                                listCallBackSheet.invoke(
                                    position,
                                    true,
                                    eventDetails?.totalComments ?: 0,
                                    eventDetails?.totalFavourites ?: 0,
                                    1
                                )
                            } else {
                                binding.apply {
                                    eventDetails?.totalFavourites =
                                        eventDetails?.totalFavourites?.plus(1)!!
                                    ivFavourite.setBackgroundResource(R.drawable.like_pic)
                                }
                                eventDetails?.isFavoritedbyUser = true
                                listCallBackSheet.invoke(
                                    position,
                                    true,
                                    eventDetails?.totalComments ?: 0,
                                    eventDetails?.totalFavourites ?: 0,
                                    2
                                )
                                eventDetails?.usersListWhoFavouriteEvent?.add(
                                    WhoLikesFavouriteList(
                                        userId = viewModal.sharedPrefs.getUserData()?.userId ?: -1,
                                        userImage = viewModal.sharedPrefs.getUserData()?.userImage
                                            ?: ""
                                    )
                                )
                            }
                            initLatestLikeRecyclerView(
                                eventDetails?.usersListWhoFavouriteEvent ?: arrayListOf()
                            )
                            likeUser()
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(activity, msg) }
                    }
                }
            }
        }
        viewModal.showLoading.observe(activity) {
            if (it) {
                binding.apply {
                    layoutOtherView.gone()
                    shimmerCommentList.shimmerAnimationEffect(it)
                }
            } else {
                binding.apply {
                    layoutOtherView.visible()
                    shimmerCommentList.shimmerAnimationEffect(it)
                }
            }
        }
    }

    private fun setDataApi(data: Details) {
        binding.apply {
            if (data.docFileName?.isNotBlank() == true) {
                relativeLayout.visible()
                tvEmail.text =
                    data.title.replace(" ", "_").trim().lowercase(Locale.ROOT).plus(".pdf")
            } else {
                relativeLayout.gone()
            }
            tvUsername.text = data.title
            tvDesc.text = data.eventDesc
            tvDate.text = if (data.eventDate != null) {
                val formattedDate = if (data.eventDate.isNullOrEmpty().not()) {
                    try {
                        data.eventDate?.convertDateFormat("yyyy-dd-MM", "dd/MM/yyyy") ?: "-"
                    } catch (e: Exception) {
                        "-"
                    }
                } else {
                    "-"
                }
                getString(R.string.date_, formattedDate)
            } else {
                getString(R.string.date_, " -")
            }
            tvTime.text = if (data.eventTime.isNullOrEmpty().not()) {
                if (data.eventTime != null) {
                    val formattedTime = data.eventTime?.convertTimeFormat("HH:mm:ss", "hh:mm a")
                    getString(R.string.time_, formattedTime)
                } else {
                    getString(R.string.time_, " -")
                }
            } else {
                getString(R.string.time_, " -")
            }
            tvViewAllComments.text = getString(R.string.view_all).plus("comments")
            likeUser()
            if (data.isFavoritedbyUser) {
                ivFavourite.setBackgroundResource(R.drawable.like_pic)
            } else {
                ivFavourite.setBackgroundResource(R.drawable.ic_fav_dislike)
            }
            if (data.zoomLink != null) {
                layout1.visible()
            }
            initLatestLikeRecyclerView(data.usersListWhoFavouriteEvent ?: arrayListOf())
            if (data.isImage) {
                videoView.gone()
                ivPlayOrPause.gone()
                ivHome.visible()
                videoProgress.gone()
                ivHome.setImageFromUrl(data.mediaPath, null)
            } else {
                videoView.visible()
                ivPlayOrPause.visible()
                ivHome.gone()
                initPlayer(data.videoHlsLink ?: "")
              //  ivPlayOrPause.setBackgroundResource(R.drawable.ic_play_icon)
              //  ivPlayOrPause.visible()
            }
        }
    }

    private fun likeUser() {
        binding.apply {
            val totalCount = eventDetails?.totalFavourites ?: 0

            /** cant show my like count in this that's why remove 1*/
            /*  if (eventDetails.isFavoritedbyUser) {
                  countLikesByOther = countLikesByOther.minus(1)
                  htmlString = "Liked by <font color=\"#31B5A0\">${countLikesByOther} </font> others"
              } else {
                  htmlString =
                      "Liked by <font color=\"#31B5A0\">${eventDetails?.totalFavourites?:0} </font> others"
              }*/
            val like = if (totalCount <= 1) "Likes" else "Like"
            htmlString = "<font color=\"#31B5A0\">${totalCount} </font> $like"
            tvLikedBy.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    /** set recycler view Meditation  List */
    @SuppressLint("SetTextI18n")
    private fun initLatestLikeRecyclerView(data: ArrayList<WhoLikesFavouriteList>) {
        if (data.size <= 0) return
        binding.apply {
            when (data.size) {
                1 -> {
                    image1.visible()
                    image2.gone()
                    image3.gone()
                    image4.gone()
                    rlImage.gone()
                    image1.setImageFromUrl(R.drawable.no_image_placeholder, data[0].userImage)
                }

                2 -> {
                    image1.visible()
                    image2.visible()
                    image3.gone()
                    image4.gone()
                    rlImage.gone()
                    image1.setImageFromUrl(R.drawable.no_image_placeholder, data[0].userImage)
                    image2.setImageFromUrl(R.drawable.no_image_placeholder, data[1].userImage)
                }

                3 -> {
                    image1.visible()
                    image2.visible()
                    image3.visible()
                    image4.gone()
                    rlImage.gone()
                    image1.setImageFromUrl(R.drawable.no_image_placeholder, data[0].userImage)
                    image2.setImageFromUrl(R.drawable.no_image_placeholder, data[1].userImage)
                    image3.setImageFromUrl(R.drawable.no_image_placeholder, data[2].userImage)
                }

                4 -> {
                    image1.visible()
                    image2.visible()
                    image3.visible()
                    image4.visible()
                    rlImage.gone()
                    image1.setImageFromUrl(R.drawable.no_image_placeholder, data[0].userImage)
                    image2.setImageFromUrl(R.drawable.no_image_placeholder, data[1].userImage)
                    image3.setImageFromUrl(R.drawable.no_image_placeholder, data[2].userImage)
                    image4.setImageFromUrl(R.drawable.no_image_placeholder, data[3].userImage)
                }

                else -> {
                    image1.visible()
                    image2.visible()
                    image3.visible()
                    image4.visible()
                    rlImage.visible()
                    image1.setImageFromUrl(R.drawable.no_image_placeholder, data[0].userImage)
                    image2.setImageFromUrl(R.drawable.no_image_placeholder, data[1].userImage)
                    image3.setImageFromUrl(R.drawable.no_image_placeholder, data[2].userImage)
                    image4.setImageFromUrl(R.drawable.no_image_placeholder, data[3].userImage)
                    tvImageCount.text = "+${data.size - 4}"
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun setUri(mediaPath: String?) {
        val metaData = MediaMetadata.Builder()
            .setTitle("Title")
            .setAlbumTitle("Album")
            .build()
        val mediaMetaData = MediaItem.Builder()
            .setUri(mediaPath?.toUri())
            .setMediaMetadata(metaData)
            .build()
        player?.setMediaItem(mediaMetaData)
        player?.prepare()
    }

    @OptIn(UnstableApi::class)
    private fun initPlayer(mediaPath: String?) {
        val loadControl: LoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(8 * 1024, 64 * 1024, 1024, 1024)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        player = ExoPlayer.Builder(applicationContext)
            .setLoadControl(loadControl)
            .build()
            .apply { addListener(playerListener) }
        setUri(mediaPath)
        binding.videoView.player = player
        binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_prev).visibility =
            View.GONE
        binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_next).visibility =
            View.GONE
        // binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_settings).visibility = View.GONE
    }

    private fun releasePlayer() {
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }

    private fun pause() {
        player?.playWhenReady = false
    }

    private fun play() {
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun restartPlayer() {
        player?.seekTo(0)
        player?.playWhenReady = true
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_ENDED -> {
                    handlingPlayIcon(false)
                }

                Player.STATE_READY -> {
                    handlingPlayIcon(true)
                }

                Player.STATE_BUFFERING -> {
                    handlingPlayIcon(playStatus = false, isBuffering = true)
                }

                Player.STATE_IDLE -> {
                    handlingPlayIcon(playStatus = false, isBuffering = true)
                }
            }
        }
    }

    private fun handlingPlayIcon(playStatus: Boolean, isBuffering: Boolean = false) {
        binding.apply {
            if (isBuffering) {
                ivPlayOrPause.setBackgroundResource(0)
                ivPlayOrPause.gone()
                videoProgress.visible()
            } else {
                if (playStatus) {
                    ivPlayOrPause.setBackgroundResource(R.drawable.ic_pause_icon)
                    ivPlayOrPause.gone()
                    play()
                } else {
                    pause()
                    ivPlayOrPause.setBackgroundResource(R.drawable.ic_play_icon)
                    ivPlayOrPause.gone()
                }
                videoProgress.gone()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun minimize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            enterPictureInPictureMode(updatePictureInPictureParams())
        } else {
            finishActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun updatePictureInPictureParams(): PictureInPictureParams {
        val icon = if (player?.isPlaying == true) {
            Icon.createWithResource(this, android.R.drawable.ic_media_pause)
        } else {
            Icon.createWithResource(this, android.R.drawable.ic_media_play)
        }
        val broadcast = PendingIntent.getBroadcast(
            this,
            0,
            Intent("PLAY_PAUSE"),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val remoteAction = RemoteAction(icon, "", "", broadcast)
        val actions = ArrayList<RemoteAction>()
        actions.add(remoteAction)
        val aspectRatio = Rational(binding.videoView.width, binding.videoView.height)
        val visibleRect = Rect()
        binding.videoView.getGlobalVisibleRect(visibleRect)
        val params = PictureInPictureParams.Builder().setAspectRatio(aspectRatio)
            .setSourceRectHint(visibleRect)
            .setActions(actions)
            .setAutoEnterEnabled(true)
            .build()
        setPictureInPictureParams(params)
        return params
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        viewModal.mediaController?.releaseMediaController()
    }
    override fun onRestart() {
        super.onRestart()
        if (!isInPictureInPictureMode) {
            binding.toolbar.ivBack.visible()
        }
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        @SuppressLint("SourceLockedOrientationActivity")
        override fun handleOnBackPressed() {
            val orientation = getResources().configuration.orientation
            if (player?.isPlaying == true || player?.isLoading == true) {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                minimize()
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                finishActivity()
            }
        }
    }

    override fun onPause() {
        if (player?.isPlaying == true || player?.isLoading == true) {
            val orientation = getResources().configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            minimize()
        }
        super.onPause()
    }
}