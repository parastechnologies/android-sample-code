package com.in2bliss.ui.activity.home.affirmation.recordAudio

import android.Manifest
import android.net.Uri
import android.os.Build
import android.text.method.ScrollingMovementMethod
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.ActivityRecordAudioBinding
import com.in2bliss.domain.AudioConverterStatusListenerInterface
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.ui.activity.home.affirmation.myRecordings.MyRecordingActivity
import com.in2bliss.ui.activity.messageDialogBox
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.audioConversionWithWorkManger
import com.in2bliss.utils.extension.checkingForRecordAudiPermission
import com.in2bliss.utils.extension.convertMilliseconds
import com.in2bliss.utils.extension.getFileName
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordAudioActivity : BaseActivity<ActivityRecordAudioBinding>(
    layout = R.layout.activity_record_audio
) {
    @Inject
    lateinit var audioConverterStatusListenerInterface: AudioConverterStatusListenerInterface
    private val viewModel: RecordAudioActivityViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.record_audio)
        binding.tvTranscript.movementMethod = ScrollingMovementMethod()
        binding.data = viewModel
        gettingBundleData()
        viewModel.initializeMediaRecorder()
        onClick()
        observer()
    }

    private fun gettingBundleData() {
        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { createAffirmation ->
            viewModel.createAffirmation =
                Gson().fromJson(createAffirmation, CreateAffirmation::class.java)
            viewModel.transcript.set(viewModel.createAffirmation?.transcript)
            viewModel.title.set(viewModel.createAffirmation?.affirmationTitle)
            binding.tvNoTranscriptAdded.visibility(
                isVisible = viewModel.createAffirmation?.transcript.isNullOrEmpty()
            )
        }
    }

    private fun observer() {
        /** Is recording observer */
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.mediaRecorder?.isRecording()?.collectLatest { isRecording ->
                binding.icPlayPause.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@RecordAudioActivity,
                        if (isRecording) R.drawable.ic_pause else R.drawable.ic_voice
                    )
                )
            }
        }
        /** Getting media recorder amplitude */
        lifecycleScope.launch {
            viewModel.mediaRecorder?.getAmplitude()?.collectLatest { amplitude ->
                binding.viewRecording.addAmplitude(
                    amplitude = amplitude
                )
            }
        }
        /** Recorder progress */
        lifecycleScope.launch {
            viewModel.mediaRecorder?.getRecorderProgress()?.collectLatest { recorderProgress ->
                viewModel.createAffirmation?.audioDuration = recorderProgress
                convertMilliseconds(
                    timeInMilli = recorderProgress,
                    convertedTime = { hour, minute, seconds ->
                        viewModel.duration.set(
                            "${String.format("%02d", hour)}:${
                                String.format("%02d", minute)
                            }:${String.format("%02d", seconds)}"
                        )
                    }
                )
            }
        }

        /** Audio conversion status listener */
        lifecycleScope.launch {
            audioConverterStatusListenerInterface.getAudioConvertStatus()
                .collectLatest { conversionStatus ->
                    if (conversionStatus.isConversionComplete || conversionStatus.isFailed) {
                        ProgressDialog.hideProgress()
                    } else {
                        ProgressDialog.showProgress(
                            activity = this@RecordAudioActivity
                        )
                    }
                    if (conversionStatus.isConversionComplete && conversionStatus.isFailed.not()) {
                        navigate(
                            fileUri = conversionStatus.convertedFileUri,
                            fileName = conversionStatus.convertedFilePath
                        )
                    }
                }
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.icPlayPause.setOnClickListener {
            checkingForRecordAudiPermission(
                context = this,
                isPermissionGranted = {
                    playAndPauseRecording()
                },
                isPermissionNotGranted = { permissionList ->
                    permissionRequest.launch(permissionList)
                }
            )
        }

        binding.btnContinue.setOnClickListener {
            if (viewModel.mediaRecorder?.isRecording()?.value == true) {
                messageDialogBox(
                    message = getString(R.string.recording_is_in_progress_please_stop_recording_first)
                ) {}
                return@setOnClickListener
            }

            if (viewModel.mediaRecorder?.isMediaRecorderInitialized()?.not() == true) {
                showToast(
                    message = getString(R.string.please_record_your_affirmation)
                )
                return@setOnClickListener
            }
            navigateOrAudioConversion()
        }
    }

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (it[Manifest.permission.RECORD_AUDIO] == true) playAndPauseRecording()
                return@registerForActivityResult
            }

            if (it[Manifest.permission.RECORD_AUDIO] == true &&
                it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
            ) playAndPauseRecording()
        }

    /**
     * Play , pause and resume audio recorder
     * */
    private fun playAndPauseRecording() {
        if (viewModel.mediaRecorder?.isMediaRecorderInitialized()?.not() == true) {
            viewModel.mediaRecorder?.startRecordAudio(
                context = this
            )
            return
        }

        /** Pause or resume recorder */
        if (viewModel.mediaRecorder?.isRecording()?.value == true) {
            viewModel.mediaRecorder?.pauseAudioRecorder()
        } else viewModel.mediaRecorder?.resumeAudioRecorder()
    }

    /**
     * Navigating to my recording activity with create affirmation data
     * */
    private fun navigateOrAudioConversion() {
        viewModel.mediaRecorder?.stopAudioRecorder()?.let { uri ->
            getFileName(
                uri = uri,
                context = this
            ) { fileName ->

                if (fileName.contains(".mp3")) {
                    navigate(
                        fileName = null,
                        fileUri = uri
                    )
                }else {
                    audioConversionWithWorkManger(
                        activity = this,
                        fileUri = uri
                    )
                }
            }
        }
    }

    private fun navigate(
        fileUri: Uri?,
        fileName: String?
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.createAffirmation?.audioFileStringUri =
                if (viewModel.mediaRecorder == null) {
                    null
                } else fileUri.toString()
            viewModel.createAffirmation?.convertedFileName = fileName
            viewModel.createAffirmation?.audioType =
                ApiConstant.AffirmationAudioType.RECORDED
            intent(
                destination = MyRecordingActivity::class.java,
                bundle = bundleOf(
                    AppConstant.CREATE_AFFIRMATION to Gson().toJson(
                        viewModel.createAffirmation,
                        CreateAffirmation::class.java
                    )
                )
            )
            finish()
        }
    }
}