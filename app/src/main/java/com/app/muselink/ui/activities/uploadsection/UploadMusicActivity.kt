package com.app.muselink.ui.activities.uploadsection

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.audiofx.Visualizer
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.IntentConstant
import com.app.muselink.data.modals.responses.GetRoleData
import com.app.muselink.ffmpeg.AudioTrimmer
import com.app.muselink.ffmpeg.FFMpegCallback
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.adapter.AdapterCategories
import com.app.muselink.ui.dialogfragments.UploadStep1
import com.app.muselink.util.showToast
import com.app.muselink.widgets.audioPlayer.ExoPlayerAudio
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_upload_music.*
import kotlinx.android.synthetic.main.layout_select_role.*
import kotlinx.android.synthetic.main.toolbar_black.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.roundToInt


@AndroidEntryPoint
class UploadMusicActivity : BaseActivity() {

    var adapterCategories: AdapterCategories? = null
    var adapterCategories2: AdapterCategories? = null
    val listCategories = ArrayList<GetRoleData>()
    private val initArrayList = ArrayList<GetRoleData>()
    val arrayListTest = ArrayList<GetRoleData>()
    private var mIsPlaying = false
    private var isSongLoaded = false
    var musicPath: String? = ""
    private var hasRecordedSoundFile: Boolean? = false
    val viewModel: UploadMusicViewModel by lazy { ViewModelProvider(this).get(UploadMusicViewModel::class.java) }
    private var exoPlayerAudio: ExoPlayerAudio? = null
    var startedPos = 0
    private var endedPos = 0
    var seconds: Long? = 0
    var marginLeftLine = 0
    private var viewEnd: View? = null
    var secondsOfSong = 15
    var defaultSecondsOfSong = 15
    var startSecondsOfSong = 0
    private var totalWidth: Long? = 0
    private var oneSecondPixel: Long? = 0
    var secondWidth: Long? = 0
    var hashMapViews = HashMap<Long, View>()
    var timerStarted = false
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var mTimerHandler : Handler?=null


    private fun startTimer() {
        try {
            timerStarted = true
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    mTimerHandler?.post {
                        try {
                            handler.sendEmptyMessage(0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            timer?.schedule(timerTask, 0, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


     var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                count++
                if (count == (secondsOfSong - startSecondsOfSong)) {
                    stopPlayAudio()
                }
            }
        }
    }

    fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
            timer = null
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_upload_music
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTimerHandler = Handler(mainLooper)
        if (!SharedPrefs.getBoolean(AppConstants.PREFS_MUSIC_TUTORIAL_1)){
            addFragment()
        }
        tvSelect.text = resources.getString(R.string.highlight_5_15_seconds)
        musicPath = intent.extras?.getString(IntentConstant.MusicPath, "")
        hasRecordedSoundFile = intent.extras?.getBoolean(IntentConstant.hasRecordedSoundFile, false)
        exoPlayerAudio = ExoPlayerAudio(
            this,
            exoPlayerAudioNavigator,
            AppConstants.SongType.TRIM.value
        )



        setAdapter()
        setToolbar()
        setListeners()
        viewModel.getRole.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    if (it.data?.data!!.isNotEmpty()) {
                        listCategories.clear()
                        for (i in it.data.data!!.indices) {
                            if (i <= 4) {
                                initArrayList.add(it.data.data!![i])
                            } else {
                                arrayListTest.add(it.data.data!![i])
                            }
                        }
                        if (it.data.data!!.size > 5) {
                            val modalCategory = GetRoleData()
                            modalCategory.IsSelected = false
                            modalCategory.Role_Name = getString(R.string.more)
                            initArrayList.add(modalCategory)
                        }
                        setRecyclerList(initArrayList)
                    }
                }
                Resource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                }
                Resource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
        try {
            val duration = getDuration(File(musicPath!!))
            seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
            Handler(mainLooper).postDelayed({
                startedPos = rlParent.width / 2
                audioWaveform.setSongSeconds(seconds!!.toInt())
                audioWaveform.setViewWidth(rlParent.width)
                setWaveFormView()
                llSeelctionView.visibility = View.VISIBLE
                llSelectionViewCenter.visibility = View.VISIBLE
                exoPlayerAudio?.playeraudioSeekTo(0)
            }, 2000)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        exoPlayerAudio?.initSingleSong(musicPath!!,true)
    }
    val exoPlayerAudioNavigator = object : ExoPlayerAudio.ExoPlayerAudioNavigator {
        override fun onSongCompleted() {
            exoPlayerAudio?.playPreviousSong()
        }
        override fun onPlayerReady() {}
        override fun onNextSongPlayed() {}
        override fun nextSongNotExist() {}
        override fun playPreviousSound() {}
        override fun previousSongNotExist() {}
        override fun repeatSong() {}
        override fun getCurrentSongPos(value: String?) {}
        override fun updateProgress(value: Float) {}
    }

    private fun getPixelsAccordingValue(): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3.toFloat(), r.displayMetrics
        ).roundToInt()
    }

    private fun updateDisplayWaves() {
        for (i in 0 until totalWidth!! / oneSecondPixel!!) {
            if (i in startSecondsOfSong..secondsOfSong) {
                if (hashMapViews.containsKey(i)) {
                    hashMapViews[i]
                        ?.setBackgroundResource(R.drawable.viewline_waveform_selection)
                } else {
                    viewLineWaveForm = View(this)
                    val paramLine: LinearLayout.LayoutParams = LinearLayout.LayoutParams(oneSecondPixel!!.toInt(), listHeight[i.toInt()])
                    paramLine.setMargins(marginLeftLine, 0, 0, 0)
                    viewLineWaveForm?.layoutParams = paramLine
                    viewLineWaveForm?.setBackgroundResource(R.drawable.viewline_waveform_selection)
                    llUploadViews.addView(viewLineWaveForm)
                    hashMapViews[i] = viewLineWaveForm!!
                }
            } else {
                if (hashMapViews.containsKey(i)) {
                    hashMapViews[i]?.setBackgroundResource(R.drawable.view_line_waveform)
                } else {
                    viewLineWaveForm = View(this)
                    val paramLine: LinearLayout.LayoutParams =
                        LinearLayout.LayoutParams(oneSecondPixel!!.toInt(), listHeight[i.toInt()])
                    paramLine.setMargins(marginLeftLine, 0, 0, 0)
                    viewLineWaveForm?.layoutParams = paramLine
                    viewLineWaveForm?.setBackgroundResource(R.drawable.view_line_waveform)
                    llUploadViews.addView(viewLineWaveForm)
                    hashMapViews[i] = viewLineWaveForm!!
                }
            }
        }

    }

    private fun selectionSongLine() {
        val lp = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.marginStart = (defaultSecondsOfSong * secondWidth!!.toInt() + rlParent.width / 2) - 20
        llSeelctionView.layoutParams = lp

        val params = rlViewTransparency.layoutParams as RelativeLayout.LayoutParams
        params.setMargins(rlParent.width / 2, 5, 0, 0)
        params.height = rlParent.height / 2
        params.width = defaultSecondsOfSong * secondWidth!!.toInt()

    }

    private var dX = 0f
    private var dY = 0f

    private var viewLineWaveForm: View? = null

    fun updateViewEnd() {
        val paramEnd: LinearLayout.LayoutParams = LinearLayout.LayoutParams(rlParent.width / 2 - (defaultSecondsOfSong * secondWidth!!.toInt()), rlParent.height)
        viewEnd?.layoutParams = paramEnd
        viewEnd?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
    }

    private var selectionEndWidth = 0

    var sample: IntArray? = null





    private fun createVisualizer() {
        val rate: Int = Visualizer.getMaxCaptureRate()
        val audioOutput = Visualizer(exoPlayerAudio?.getAudioPlayer()!!.audioSessionId) // get output audio stream
        audioOutput.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer?,
                waveform: ByteArray,
                samplingRate: Int
            ) {
                val intensity = (waveform[0].toFloat() + 128f) / 256
            }

            override fun onFftDataCapture(
                visualizer: Visualizer?,
                fft: ByteArray?,
                samplingRate: Int
            ) {
            }
        }, rate, true, false) // waveform not freq data
        audioOutput.enabled = true
    }


    val listHeight = ArrayList<Int>()

    @SuppressLint("ClickableViewAccessibility")
    private fun setWaveFormView() {
        oneSecondPixel = getPixelsAccordingValue().toLong()
        marginLeftLine = getPixelsAccordingValue()
        totalWidth = oneSecondPixel!! * seconds!!
        secondWidth = oneSecondPixel!! + marginLeftLine
        selectionEndWidth = (defaultSecondsOfSong * secondWidth!!.toInt() + rlParent.width / 2) - 20

        selectionSongLine()

        val view = View(this)
        val param: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(rlParent.width / 2, rlParent.height)
        view.layoutParams = param
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        llUploadViews.addView(view)

        val r = Random()
        for (i in 0 until seconds!!.toInt()) {
            val i1 = r.nextInt(rlParent.height - 20) + 20
            listHeight.add(i1)
        }

        updateDisplayWaves()

        //Add Transparent End
        viewEnd = View(this)
        updateViewEnd()
        llUploadViews.addView(viewEnd)

        endedPos = startedPos + (seconds!! * secondWidth!!).toInt()

        startHorizontal.setOnScrollChangeListener { _, scrollX, _, oldScrollX, _ ->
            stopPlayAudio()
            val seconds = scrollX / secondWidth!!
            if (scrollX > oldScrollX) {
                if (seconds >= 1) {
                    startSecondsOfSong = seconds.toInt()
                    secondsOfSong = defaultSecondsOfSong + seconds.toInt()
                    exoPlayerAudio?.playeraudioSeekTo((startSecondsOfSong * 1000).toLong())
                    updateDisplayWaves()
                    updateViewEnd()
                }
            } else {
                val minusValue = startSecondsOfSong - seconds.toInt()
                startSecondsOfSong -= minusValue
                secondsOfSong -= minusValue
                exoPlayerAudio?.playeraudioSeekTo((startSecondsOfSong * 1000).toLong())
                updateDisplayWaves()
                updateViewEnd()
            }
        }
        llSeelctionView.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    stopPlayAudio()
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    isMove = true
                    stopPlayAudio()
                    val x = event.rawX + dX
                    if (x < 0) {
                        val minusSeconds = abs((x / secondWidth!!).toInt())
                        val seconds = defaultSecondsOfSong - minusSeconds
                        if (seconds > 4) {
                            defaultSecondsOfSong = seconds
                            secondsOfSong -= minusSeconds
                            if (defaultSecondsOfSong >= 5) {
                                tvSeconds.text = defaultSecondsOfSong.toString()
                            }
                            selectionSongLine()
                            updateDisplayWaves()
                            updateViewEnd()
                        }
                    } else {
                        val addSeconds = abs((x / secondWidth!!).toInt())
                        val seconds = defaultSecondsOfSong + addSeconds
                        if (seconds <= 15) {
                            defaultSecondsOfSong = seconds
                            secondsOfSong += addSeconds
                            tvSeconds.text = defaultSecondsOfSong.toString()
                            selectionSongLine()
                            updateDisplayWaves()
                            updateViewEnd()
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isMove) {
                        isMove = false
                        playAudioAfterSelection()
                    }
                }
            }
            true
        }
        isSongLoaded = true
    }
    var isMove = false
    private fun playAudioAfterSelection() {
        Handler(mainLooper).postDelayed({
            startPlayAudio()
        }, 300)
    }
    private fun getDuration(file: File): String {
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(file.absolutePath)
            return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                .toString()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        return ""
    }
    var count = 1
    private fun startPlayAudio(){
        count = 1
        startTimer()
        mIsPlaying = true
        imgPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause))
        exoPlayerAudio?.playeraudioSeekTo((startSecondsOfSong * 1000).toLong())
        exoPlayerAudio?.startPlayAudio()
    }
    fun stopPlayAudio() {
        stopTimer()
        mIsPlaying = false
        imgPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play_icon))
        exoPlayerAudio?.pausePlayAudio()
    }
    private fun setListeners() {
        imgcloseMoreview.setOnClickListener {
            setRecyclerList(initArrayList)
        }
        btnNext.setOnClickListener {
            if (isSongLoaded) {
                cutAudio()
            } else {
                showToast(this, getString(R.string.please_wait_song_is_loading))
            }
        }
        nmcPausePlayUpload.setOnClickListener {
            if (isSongLoaded) {
                if (!mIsPlaying) {
                    startPlayAudio()
                } else {
                    stopPlayAudio()
                }
            } else {
                showToast(this, getString(R.string.please_wait_song_is_loading))
            }
        }
        npmRepeatSong.setOnClickListener {
            if (isSongLoaded) {
                startPlayAudio()
            } else {
                showToast(this, getString(R.string.please_wait_song_is_loading))
            }
        }

    }
    fun addFragment() {
        supportFragmentManager.beginTransaction().add(UploadStep1(), "Upload2").commit()
    }
    private fun setToolbar() {
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onPause() {
        if (mIsPlaying){
            exoPlayerAudio?.pausePlayAudio()
        }
        super.onPause()
    }
    override fun onBackPressed() {
        if (mIsPlaying){
            exoPlayerAudio?.pausePlayAudio()
        }
        super.onBackPressed()

    }
    private fun setAdapter() {
        rvCategories?.layoutManager = GridLayoutManager(this, 3)
        adapterCategories = AdapterCategories(this, adapterCategoriesNavigator)
        rvCategories!!.adapter = adapterCategories

        rvRoleCategoriesAll?.layoutManager = GridLayoutManager(this, 3)
        adapterCategories2 = AdapterCategories(this, adapterCategoriesMoreNavigator)
        rvRoleCategoriesAll!!.adapter = adapterCategories2
    }
    private val adapterCategoriesNavigator =
        object : AdapterCategories.AdapterCategoriesNavigator {
            override fun onClickCategory(position: Int) {
                if (listCategories[position].Role_Name!! == getString(R.string.more)) {
                    showMoreRoleView(arrayListTest)
                } else {
                    listCategories[position].IsSelected =
                        !listCategories[position].IsSelected
                    adapterCategories?.notifyDataSetChanged()
                }
            }
        }
    private fun setRecyclerList(data: ArrayList<GetRoleData>?) {
        showHideMoreView(false)
        listCategories.clear()
        listCategories.addAll(data!!)
        adapterCategories?.setData(listCategories)
    }
    private fun showHideMoreView(IsShow: Boolean) {
        if (IsShow) {
            rlMoreView.visibility = View.VISIBLE
        } else {
            rlMoreView.visibility = View.GONE
        }
    }
    private fun showMoreRoleView(data: ArrayList<GetRoleData>?) {
        showHideMoreView(true)
        listCategories.removeAt(listCategories.size - 1)
        listCategories.addAll(data!!)
        adapterCategories2?.setData(listCategories)
    }
    private val adapterCategoriesMoreNavigator =
        object : AdapterCategories.AdapterCategoriesNavigator {
            override fun onClickCategory(position: Int) {
                if (listCategories[position].Role_Name!! == getString(R.string.more)) {
                    showMoreRoleView(arrayListTest)
                } else {
                    listCategories[position].IsSelected =
                        !listCategories[position].IsSelected
                    adapterCategories2?.notifyDataSetChanged()
                }
            }
        }
    private val arrayListCategories = ArrayList<String>()
    private fun cutAudio() {
        arrayListCategories.clear()
        for (model in listCategories) {
            if (model.IsSelected) {
                arrayListCategories.add(model.Project_Role_Id!!)
            }
        }
        if (arrayListCategories.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_role), Toast.LENGTH_SHORT)
                .show()
        } else {
            stopPlayAudio()
            processSong()
        }
    }
     private fun processSong() {
        @Suppress("DEPRECATION")
        val file = File(Environment.getExternalStorageDirectory().path, "MuseLink/audio/trimmed.mp3")
        if (file.exists()){
            file.delete()
        }
        val startTime = "00:00:" + String.format("%02d", startSecondsOfSong)
        val endTime = "00:00:" + String.format("%02d", secondsOfSong)
        AudioTrimmer.with(this)
            .setFile(File(musicPath!!))
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setOutputPath(makeDirectory()!!)
            .setOutputFileName("trimmed.mp3")
            .setCallback(audioTrimmer)
            .trim()
    }
    private var destinationRoot: File? = null
    @Suppress("DEPRECATION")
    private fun makeDirectory(): String? {
       // destinationRoot = File(getExternalFilesDirs(null).toString(),"MuseLink/audio")
       destinationRoot = File(Environment.getExternalStorageDirectory().path, "MuseLink/audio")
        if (destinationRoot?.exists() == false) {
            destinationRoot?.mkdirs()
        }
        return destinationRoot?.path
    }
    private val audioTrimmer = object : FFMpegCallback {
        override fun onProgress(progress: String) {
            runOnUiThread {
                Log.e("onProgress", "==$progress")
            }

        }
        override fun onSuccess(convertedFile: File, type: String) {
            runOnUiThread {
                afterSavingRingtone(convertedFile.path)
            }
         }
        override fun onFailure(error: Exception) {
            runOnUiThread {
                Log.e("onFailure", "==$error")
            }


        }
        override fun onNotAvailable(error: Exception) {

            runOnUiThread {
                Log.e("error", "===$error")
            }


        }
        override fun onFinish() {
            runOnUiThread {
                Log.e("onFinish", "===$")
            }


        }
    }

    private fun afterSavingRingtone(
        outPath: String
    ) {
        btnNext.isEnabled = true
        progressNext.visibility = View.GONE
        val bundle = bundleOf(
            IntentConstant.MusicPath to musicPath,
            IntentConstant.Role to arrayListCategories,
            IntentConstant.MusicPath15Sec to outPath
        )
        val intent = Intent(this, UploadActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }


}
