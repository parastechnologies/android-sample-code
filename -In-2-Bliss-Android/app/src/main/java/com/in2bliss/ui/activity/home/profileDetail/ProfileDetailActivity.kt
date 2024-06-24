package com.in2bliss.ui.activity.home.profileDetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.data.sharedPreferences.SharedPreference
import com.in2bliss.databinding.ActivityProfileDetailBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.imagePicker
import com.in2bliss.utils.extension.checkCameraPermissionForCaptureImage
import com.in2bliss.utils.extension.checkingGalleryPermissionForPickImage
import com.in2bliss.utils.extension.compressImage
import com.in2bliss.utils.extension.getFileFromUri
import com.in2bliss.utils.extension.getUriForCamera
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailActivity :
    BaseActivity<ActivityProfileDetailBinding>(R.layout.activity_profile_detail) {

    val viewModel: ProfileDetailViewModel by viewModels()


    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun init() {
        viewModel.retryApiRequest(ApiConstant.PROFILE_DETAIL)
        binding.tbToolbar.tvTitle.text = getString(R.string.edit_profile)
        setDataOnView()
        onClick()
        observe()
    }

    private fun setDataOnView() {
        binding.etName.setText(sharedPreference.userData?.data?.fullName.toString())
        binding.ivProfile.glide(
            requestManager = requestManager,
            image = BuildConfig.PROFILE_BASE_URL.plus(sharedPreference.userData?.data?.profilePicture),
            placeholder = R.drawable.ic_user_placholder,
            error = R.drawable.ic_user_placholder
        )
    }


    private fun onClick() {
        binding.etName.doAfterTextChanged { text ->
            binding.ivNameCancel.visibility((text?.length ?: "".length) > 0)
        }
        binding.ivNameCancel.setOnClickListener {
            binding.etName.setText("")
        }
        binding.ivUpload.setOnClickListener {
            pickImage()
        }
        binding.btnContinue.setOnClickListener {
            if (binding.etName.text.isNullOrBlank()) {
                showToast(getString(R.string.please_enter_your_full_name))
            } else {
                viewModel.name = binding.etName.text.toString()
                viewModel.retryApiRequest(ApiConstant.PROFILE_UPDATE)
            }
        }
        binding.tbToolbar.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.editProfileResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ProfileDetailActivity,
                    success = { response ->
                        val data = sharedPreference.userData
                        if (response.data?.profilePicture != null) {
                            data?.data?.profilePicture = response.data.profilePicture
                        }
                        data?.data?.fullName = binding.etName.text.toString()
                        sharedPreference.userData = data
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
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

    private fun pickImage() {
        imagePicker(
            context = this,
            select = { selectedType ->
                if (selectedType == 0) {
                    checkCameraPermissionForCaptureImage(
                        context = this,
                        permissionGranted = {
                            captureCameraImage()
                        },
                        permissionNotGranted = { permissionList ->
                            cameraPermission.launch(permissionList)
                        }
                    )
                    return@imagePicker
                }

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
        )
    }

    private val galleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.READ_EXTERNAL_STORAGE] == true) captureGalleryImage()
        }


    private val pickImageRequest =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.imageUri = uri
            uri?.let { imageUri ->
                setImageAndApiRequest(
                    imageUri = imageUri
                )
            }
        }

    private fun captureGalleryImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pickImageRequest.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            return
        }
        pickGalleryImage.launch("image/*")
    }

    private val pickGalleryImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            viewModel.imageUri = uri
            uri?.let { imageUri ->
                setImageAndApiRequest(
                    imageUri = imageUri
                )
            }
        }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (result[Manifest.permission.CAMERA] == true) captureCameraImage()
            return@registerForActivityResult
        }
        if (result[Manifest.permission.CAMERA] == true && result[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) captureCameraImage()
    }


    private fun captureCameraImage() {
        viewModel.tempImageUri = getUriForCamera(
            context = this
        )
        cameraImage.launch(viewModel.tempImageUri)
    }


    private val cameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                viewModel.imageUri = viewModel.tempImageUri
                viewModel.imageUri?.let { imageUri ->
                    setImageAndApiRequest(
                        imageUri = imageUri
                    )
                }
            }
        }

    private fun setImageAndApiRequest(imageUri: Uri) {
        getFileFromUri(
            context = this,
            uri = imageUri,
            file = { file ->
                viewModel.imageFile = file

                lifecycleScope.launch {

                    viewModel.imageFile = compressImage(
                        imageFile = viewModel.imageFile,
                        activity = this@ProfileDetailActivity
                    )

                    withContext(Dispatchers.Main) {
                        binding.ivProfile.glide(
                            requestManager = requestManager,
                            image = imageUri,
                            error = R.drawable.ic_error_place_holder,
                            placeholder = R.drawable.ic_error_place_holder
                        )

                    }
                }
            }
        )
    }
}