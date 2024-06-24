package com.in2bliss.ui.activity.auth.stepFour

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityStepFourBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.auth.stepFive.StepFiveActivity
import com.in2bliss.utils.extension.checkCameraPermissionForCaptureImage
import com.in2bliss.utils.extension.checkingGalleryPermissionForPickImage
import com.in2bliss.utils.extension.compressImage
import com.in2bliss.utils.extension.getFileFromUri
import com.in2bliss.utils.extension.getUriForCamera
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class StepFourActivity : BaseActivity<ActivityStepFourBinding>(
    layout = R.layout.activity_step_four
) {

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var sharedPreference: SharedPreference
    private val viewModel: StepFourViewModel by viewModels()

    override fun init() {
        onClick()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.addProfileResponse.collectLatest { it ->
                handleResponse(
                    response = it,
                    context = this@StepFourActivity,
                    success = {response->
                        val userData = sharedPreference.userData
                        userData?.data?.profileStatus = 3
                        userData?.data?.profilePicture=response.profilePicture
                        sharedPreference.userData = userData
                        intent(
                            destination = StepFiveActivity::class.java
                        )
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvSkip.setOnClickListener {
            intent(
                destination = StepFiveActivity::class.java
            )
        }

        binding.cvCamera.setOnClickListener {
            checkCameraPermissionForCaptureImage(
                context = this,
                permissionGranted = {
                    captureImage()
                },
                permissionNotGranted = { permissionList ->
                    cameraPermission.launch(permissionList)
                }
            )
        }

        binding.cvGallery.setOnClickListener {
            checkingGalleryPermissionForPickImage(
                context = this,
                permissionGranted = {
                    captureGalleryImage()
                },
                permissionNotGranted = { permissionList ->
                    galleryPermission.launch(permissionList)
                }
            )
        }

        binding.btnContinue.setOnClickListener {
            if (viewModel.imageFile == null) {
                showToast(
                    message = getString(R.string.please_select_image)
                )
                return@setOnClickListener
            }
            viewModel.retryApiRequest(
                apiName = ApiConstant.PICTURE_ADD
            )
        }

        binding.tvSkip.setOnClickListener {
            intent(
                destination = StepFiveActivity::class.java
            )
        }
    }

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (result[Manifest.permission.CAMERA] == true) captureImage()
                return@registerForActivityResult
            }
            if (result[Manifest.permission.CAMERA] == true &&
                result[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
            ) captureImage()
        }

    private val galleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.READ_EXTERNAL_STORAGE] == true) captureGalleryImage()
        }

    private fun captureImage() {
        viewModel.tempImageUri = getUriForCamera(
            context = this
        )
        captureImage.launch(viewModel.tempImageUri)
    }

    private val captureImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                viewModel.imageUri = viewModel.tempImageUri
                viewModel.imageUri?.let { image ->
                    getFileFromUri(
                        context = this,
                        uri = image,
                        file = { file ->
                            viewModel.imageFile = file
                        }
                    )
                    compressImageAndApiRequest(
                        image = image
                    )
                }
            }
        }


    private fun captureGalleryImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pickImageRequest.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            return
        }
        pickGalleryImage.launch("image/*")
    }

    private val pickImageRequest =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            setData(
                uri = uri
            )
        }

    private val pickGalleryImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            setData(
                uri = uri
            )
        }

    private fun setData(uri: Uri?) {
        viewModel.imageUri = uri
        uri?.let { image ->

            getFileFromUri(
                context = this,
                uri = uri,
                file = { file ->
                    viewModel.imageFile = file
                }
            )
            compressImageAndApiRequest(
                image = image
            )
        }
    }

    private fun compressImageAndApiRequest(image: Uri) {
        lifecycleScope.launch {
            viewModel.imageFile = compressImage(
                imageFile = viewModel.imageFile,
                activity = this@StepFourActivity
            )
            withContext(Dispatchers.Main) {
                binding.ivUpload.glide(
                    requestManager = requestManager,
                    image = image,
                    error = R.drawable.ic_upload_profile,
                    placeholder = R.drawable.ic_upload_profile
                )
            }
        }
    }
}

