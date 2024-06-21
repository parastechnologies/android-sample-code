package com.mindbyromanzanoni.view.activity.edificationVideoPlayer

import android.annotation.SuppressLint
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
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
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
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.edification.EdificationTypeListResponse
import com.mindbyromanzanoni.databinding.ActivityEdificationVideoPlayerBinding
import com.mindbyromanzanoni.databinding.RowitemEdificationlistBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.imagePicker
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EdificationVideoPlayerActivity : BaseActivity<ActivityEdificationVideoPlayerBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    var activity = this@EdificationVideoPlayerActivity
    private var getJsonData = ""
    private var edificationListItemResponse: EdificationTypeListResponse? = null
    private var filteredArrayList: ArrayList<EdificationTypeListResponse>? = null
    private var edificationTypeList: ArrayList<EdificationTypeListResponse>? = null
    private var player: ExoPlayer? = null
    override fun getLayoutRes(): Int = R.layout.activity_edification_video_player
    override fun initView() {
        getIntentData()
        setToolbar()
        setOnClickListener()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setOnClickListener() {
        binding.apply {
            ivPlayOrPause.setOnClickListener {
                if (player?.isPlaying == true) {
                    handlingPlayIcon(false)
                } else if (player?.isPlaying == false) {
                    handlingPlayIcon(true)
                }
                ivPlayOrPause.gone()
                icImageThumb.gone()
                videoProgress.visible()
            }
            icZoom.setOnClickListener {
                val orientation = getResources().configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    binding.arrowDown.visible()
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    binding.arrowDown.gone()
                }
            }
            binding.arrowDown.setOnClickListener {
                binding.arrowDown.gone()
                onBackPressedDispatcher.onBackPressed()
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
            binding.mainViewCl.visibility = View.GONE
            binding.toolbar.ctlRoot.gone()
            setFullScreenLayout()
        } else {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
            binding.mainViewCl.visibility = View.VISIBLE
            binding.toolbar.ctlRoot.visible()
             setNormalLayout()
        }
    }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?) {
            if (player?.isPlaying==true){
                player?.pause()
            }else{
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
        newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.icZoom.gone()
            val filter = IntentFilter()
            filter.addAction("PLAY_PAUSE")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            }else{
                registerReceiver(receiver, filter)
            }
            binding.videoView.useController=false
            binding.arrowDown.gone()
         } else {
            binding.icZoom.visible()
            binding.arrowDown.gone()
            unregisterReceiver(receiver)
            binding.videoView.useController=true
         }
    }
    override fun viewModel() {}
    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.now_playing)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }
    private fun getArrayListFromString(list: String): ArrayList<EdificationTypeListResponse>? {
        val gson = Gson()
        return if (list.isNotBlank()) {
            val type = object : TypeToken<List<EdificationTypeListResponse>>() {}.type
            return gson.fromJson(list, type)
        } else {
            null
        }
    }
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            val screenType = intent.getString(AppConstants.SCREEN_TYPE)
            if (screenType == AppConstants.HOME_SCREEN) {
                val eventId = intent.getInt(AppConstants.EVENT_ID).toString()
                observeDataFromViewModal()
                RunInScope.ioThread {
                    viewModal.responseOnTheBasesOfCategory(eventId = eventId, "1")
                }
            } else {
                getJsonData = intent.getString(AppConstants.EDIFICATION_DATA).toString()
                edificationListItemResponse =
                    Gson().fromJson(getJsonData, EdificationTypeListResponse::class.java)
                val list = intent.getString(AppConstants.EDIFICATION_TYPE_LIST)
                edificationTypeList = getArrayListFromString(list.toString())
                viewModal.categoryId.set(intent.getString(AppConstants.EDIFICATION_CAT_ID).toString())
            }
            setData(edificationListItemResponse)
            setList()
        }
    }
    private fun setList() {
        val filteredList = edificationTypeList?.filter { it.categoryId != edificationListItemResponse?.categoryId }
        filteredArrayList = filteredList?.let { ArrayList(it) }
        filteredArrayList?.let { initMeditationRecyclerView(it) }
    }
    private fun setData(edificationListItemResponse: EdificationTypeListResponse?) {
        initPlayer(edificationListItemResponse?.videoHlsLink.toString())
        binding.apply {
            tvSongName.text = edificationListItemResponse?.title
            tvSongNameDec.text = edificationListItemResponse?.content
            icImageThumb.setImageFromUrl(edificationListItemResponse?.videoThumbImage, videoProgress)
            ivPlayOrPause.setBackgroundResource(R.drawable.ic_play_icon)
            ivPlayOrPause.visible()
        }
    }
    /** set recycler view Meditation  List */
    private fun initMeditationRecyclerView(data: ArrayList<EdificationTypeListResponse>) {
        binding.rvCategoryEdification.adapter = categoryEdificationListAdapter
        categoryEdificationListAdapter.submitList(data)
    }

    private val categoryEdificationListAdapter =
        object : GenericAdapter<RowitemEdificationlistBinding, EdificationTypeListResponse>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.rowitem_edificationlist
            }

            override fun onBindHolder(
                holder: RowitemEdificationlistBinding,
                dataClass: EdificationTypeListResponse,
                position: Int
            ) {
                holder.apply {
                    tvName.text = dataClass.title
                    tvDec.text = dataClass.content
                    tvDuration.text = dataClass.duration
                    ivImage.setImageFromUrl(dataClass.videoThumbImage, progressBar)
                    root.setOnClickListener {
                        filterList(dataClass)
                    }
                }
            }
        }
    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(dataClass: EdificationTypeListResponse) {
        filteredArrayList?.clear()
        val newList = edificationTypeList?.filter { it.categoryId != dataClass.categoryId }
        newList?.forEach {
            filteredArrayList?.add(it)
        }
        releasePlayer()
        setData(dataClass)
        categoryEdificationListAdapter.notifyDataSetChanged()
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.edificationListSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            initMeditationRecyclerView(data.data)
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModal.allTypeResponse.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val responseData = isResponse.data
                        if (responseData?.isSuccess() == true) {
                            edificationListItemResponse = EdificationTypeListResponse(
                                content = responseData.data?.content ?: "",
                                title = responseData.data?.title ?: "",
                                videoName = responseData.data?.videoName ?: "",
                                videoHlsLink = responseData.data?.videoHlsLink ?: "",
                                videoThumbImage = responseData.data?.videoThumbImage ?: "")
                            setData(edificationListItemResponse)
                        } else {
                            finishActivity()
                        }
                    }
                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(this@EdificationVideoPlayerActivity, msg)
                        }
                    }
                }
            }
        }
        /* viewModal.showLoading.observe(activity) {
             if (it) {
                 binding.rvCategoryEdification.gone()
                 binding.shimmerCommentList.apply {
                     visible()
                     startShimmer()
                 }
             } else {
                 binding.shimmerCommentList.apply {
                     RunInScope.mainThread {
                         stopShimmer()
                         gone()
                         binding.rvCategoryEdification.visible()
                     }
                 }
             }
         }*/
    }
    @OptIn(UnstableApi::class)
    private fun initPlayer(mediaPath: String) {
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
//      binding.videoView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(androidx.media3.ui.R.id.exo_settings).visibility =View.GONE
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

    private fun setUri(mediaPath: String) {
        val metaData = MediaMetadata.Builder()
            .setTitle("Title")
            .setAlbumTitle("Album")
            .build()
        val mediaMetaData = MediaItem.Builder()
            .setUri(mediaPath.toUri())
            .setMediaMetadata(metaData)
            .build()
        player?.setMediaItem(mediaMetaData)
    }

    private fun setFullScreenLayout() {
        binding.videoView.setLayoutParams(FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        binding.icZoom.setImageResource(R.drawable.portrait_svg)
        binding.arrowDown.visible()
    }
    private fun setNormalLayout() {
        binding.videoView.setLayoutParams(
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                this.resources.getDimension(com.intuit.sdp.R.dimen._180sdp).toInt()
            )
        )
        binding.icZoom.setImageResource(R.drawable.ic_fullscreen_icn)
        binding.arrowDown.gone()
    }
    private fun minimize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            enterPictureInPictureMode(updatePictureInPictureParams())
        }else{
            finishActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun updatePictureInPictureParams(): PictureInPictureParams {
        val icon = if (player?.isPlaying==true) {
            Icon.createWithResource(this, android.R.drawable.ic_media_pause)
        } else {
            Icon.createWithResource(this,android.R.drawable.ic_media_play)
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
            if (player?.isPlaying == true|| player?.isLoading==true){
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }
                minimize()
            }else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }else{
                finishActivity()
            }
        }
    }
    override fun finish() {
        player?.release()
        super.finish()
    }
    override fun onPause() {
        if (player?.isPlaying == true|| player?.isLoading==true) {
            val orientation = getResources().configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
            minimize()
        }
        super.onPause()
    }
}