package com.in2bliss.ui.activity.home.affirmation.createAffirmationBottomSheet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.FragmentCreateAffirmationBottomSheetBinding
import com.in2bliss.domain.AudioConverterStatusListenerInterface
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.ChooseBackgroundActivity
import com.in2bliss.ui.activity.home.affirmation.myRecordings.MyRecordingActivity
import com.in2bliss.ui.activity.home.affirmation.recordAudio.RecordAudioActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.audioConversionWithWorkManger
import com.in2bliss.utils.extension.checkForAudioPickerPermission
import com.in2bliss.utils.extension.getFileName
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.writeExternalStoragePermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateAffirmationBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var audioConverterStatusListenerInterface: AudioConverterStatusListenerInterface

    private var binding: FragmentCreateAffirmationBottomSheetBinding? = null
    private var createAffirmation: CreateAffirmation? = null
    private var audioUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAffirmationBottomSheetBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = dialog as BottomSheetDialog
        bottomSheet.behavior.peekHeight = 1800

        gettingBundleData()
        onClick()
        observer()
    }

    private fun observer() {

        /** Audio conversion status listener */
        lifecycleScope.launch {
            audioConverterStatusListenerInterface.getAudioConvertStatus()
                .collectLatest { conversionStatus ->
                    if (conversionStatus.isConversionComplete || conversionStatus.isFailed) {
                        ProgressDialog.hideProgress()
                    } else {
                        ProgressDialog.showProgress(
                            activity = requireActivity()
                        )
                    }

                    if (conversionStatus.isConversionComplete && conversionStatus.isFailed.not()) {
                        navigate(
                            fileName = conversionStatus.convertedFilePath,
                            fileUri = conversionStatus.convertedFileUri
                        )
                    }
                }
        }
    }

    private fun gettingBundleData() {
        arguments?.getString(AppConstant.CREATE_AFFIRMATION)?.let { createAffirmationDetails ->
            createAffirmation =
                Gson().fromJson(createAffirmationDetails, CreateAffirmation::class.java)
        }

        binding?.tvSkip?.visibility(
            isVisible = createAffirmation?.isEdit ?: false
        )
    }

    private fun onClick() {
        binding?.cvRecordAudio?.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(createAffirmation)
            )
            requireActivity().intent(
                destination = RecordAudioActivity::class.java,
                bundle = bundle
            )
        }

        binding?.tvUploadRecording?.setOnClickListener {
            checkForAudioPickerPermission(
                context = requireContext(),
                permissionGranted = {
                    pickAudio.launch("audio/*")
                },
                permissionNotGranted = {
                    requestPermission.launch(it)
                }
            )
        }

        binding?.tvSkip?.setOnClickListener {
            val bundle = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(createAffirmation),
                AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.CREATE_AFFIRMATION.name
            )
            requireActivity().intent(
                destination = ChooseBackgroundActivity::class.java,
                bundle = bundle
            )
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
            if (isPermissionGranted) {
                pickAudio.launch("audio/*")
            }
        }

    private val pickAudio =
        registerForActivityResult(ActivityResultContracts.GetContent()) { audioUri ->
            audioUri?.let { uri ->
                getFileName(
                    uri = uri,
                    context = requireActivity()
                ) { fileName ->

                    this.audioUri = uri

                    if (fileName.contains(".mp3")) {
                        navigate(
                            fileName = null,
                            fileUri = audioUri
                        )
                        return@getFileName
                    }

                    writeExternalStoragePermission(
                        context = requireContext(),
                        isPermissionGranted = {
                            audioConversion()
                        },
                        isPermissionNotGranted = { permission ->
                            writeStoragePermission.launch(permission)
                        }
                    )
                }
            }
        }

    private val writeStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) audioConversion()
        }

    private fun audioConversion() {
        audioConversionWithWorkManger(
            activity = requireActivity(),
            fileUri = audioUri
        )
    }

    private fun navigate(
        fileUri: Uri?,
        fileName: String?
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            createAffirmation?.audioFileStringUri = fileUri.toString()
            createAffirmation?.convertedFileName = fileName
            createAffirmation?.audioType = ApiConstant.AffirmationAudioType.UPLOAD

            val bundle = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(createAffirmation)
            )
            requireActivity().intent(
                destination = MyRecordingActivity::class.java,
                bundle = bundle
            )
        }
    }
}