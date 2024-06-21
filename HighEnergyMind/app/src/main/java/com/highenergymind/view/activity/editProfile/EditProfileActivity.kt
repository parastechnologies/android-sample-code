package com.highenergymind.view.activity.editProfile

import android.Manifest
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.UserResponse
import com.highenergymind.databinding.ActivityEditProfileBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.checkCameraPermission
import com.highenergymind.utils.fileImageUri
import com.highenergymind.utils.firstUpper
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.getFilePath
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.requestBody
import com.highenergymind.utils.showToast
import com.highenergymind.utils.toRequestBodyProfileImage
import com.highenergymind.view.activity.home.HomeActivity
import com.highenergymind.view.sheet.PickerSheet
import com.highenergymind.view.sheet.selectage.SelectionBottomSheet
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {
    private var takeImageUri: Uri? = null
    private var isTakeImage: Boolean = false
    private val viewModel: EditProfileViewModel by viewModels()
    override fun getLayoutRes(): Int {
        return R.layout.activity_edit_profile
    }


    override fun initView() {
        fullScreenStatusBar()
        setToolBar()
        setCollectors()
        setDataLocally()
        clicks()
    }

    private fun setToolBar() {
        binding.customTool.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.edit_profile)
        }
    }

    private fun setCollectors() {
        viewModel.apply {
            lifecycleScope.launch {
                editProfileResponse.collectLatest {
                    handleResponse(it, { res ->
                        val response = res as UserResponse
                        response.data?.let { it1 -> sharedPrefs.saveUserData(it1) }
                        (application as ApplicationClass).getSelectedLanguage()
                        intentComponent(HomeActivity::class.java, Bundle().also { bnd ->
                            bnd.putInt(AppConstant.SCREEN_FROM, R.id.editProfileIV)
                        })
                        finishAffinity()
                    })
                }
            }
            lifecycleScope.launch {
                isLoading.collectLatest {
                    progressDialog(it)
                }
            }
        }
    }

    private fun clicks() {
        binding.apply {

            ivProfile.setOnClickListener {
                openPickerSheet()
            }
            editProfileIV.setOnClickListener {
                openPickerSheet()
            }
            etLanguage.setOnClickListener {
                openLanguageSheet()
            }
            ivDropDown.setOnClickListener {
                openLanguageSheet()
            }
            btnSave.setOnClickListener {
                if (validator.editProfileValidator(binding)) {
                    editProfile()
                }
            }
        }
    }

    private fun openLanguageSheet() {
        val selectAgeBottom =
            SelectionBottomSheet(
                listOf(getString(R.string.english), getString(R.string.german)),
                title = getString(R.string.your_language),
                desc = getString(R.string.select_your_language)
            )
        selectAgeBottom.callBack = { age ->
            binding.etLanguage.setText(age)
        }

        selectAgeBottom.show(
            supportFragmentManager,
            "ModalBottomSheetDialog.TAG"
        )
    }

    private fun setDataLocally() {
        val data = viewModel.sharedPrefs.getUserData()
        binding.apply {
            ivProfile.glideImage(data?.userImg)
            etFirstName.setText(data?.firstName)
            etLastName.setText(data?.lastName)
            etEmail.setText(data?.email)
            etLanguage.setText(data?.language?.firstUpper())
        }
    }


    private fun editProfile() {
        binding.apply {
            viewModel.apply {
                partMap[ApiConstant.NAME] = etFirstName.text?.trim().toString().requestBody()
                etLastName.text?.trim()?.toString()?.let { lastName ->
                    if (lastName.isNotEmpty()) {
                        partMap[ApiConstant.LAST_NAME] = lastName.requestBody()
                    }
                }
                partMap[ApiConstant.LANGUAGE] =
                    etLanguage.text?.trim().toString().lowercase().requestBody()
                editProfileApi()
            }
        }
    }

    private fun openPickerSheet() {
        PickerSheet { isCamera ->
            if (isCamera) {
                isTakeImage = true
                if (checkCameraPermission()) {
                    openFrontCamera()
                } else {
                    requestPermissionCam.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            cameraPermissionHigherArray
                        } else {
                            cameraPermissionLowerArray
                        }
                    )
                }
            } else {

                if (checkCameraPermission()) {
                    isTakeImage = false
                    if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(
                            this@EditProfileActivity
                        )
                    ) {
                        imagePickerNew.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        pickImage.launch("image/*")
                    }
                } else {
                    requestPermissionCam.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            cameraPermissionHigherArray
                        } else {
                            cameraPermissionLowerArray
                        }
                    )
                }
            }
        }.show(supportFragmentManager, "")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri

                imageConvertAndApiRequest(resultUri!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                showToast(error.message)
            }
        }
    }


    private val frontCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                photoClicked(takeImageUri)
            }
        }


    private fun openFrontCamera() {
        takeImageUri = fileImageUri(this@EditProfileActivity)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Specify the camera facing direction
        takePictureIntent.putExtra(
            "android.intent.extras.CAMERA_FACING",
            CameraCharacteristics.LENS_FACING_FRONT
        )
        takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        // Create a file to save the image
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takeImageUri)

        // Start the camera activity
        frontCamera.launch(takePictureIntent)
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            photoClicked(it)
        }
    }

    private val imagePickerNew =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                photoClicked(it)
            }
        }

    private fun photoClicked(uri: Uri?) {
        CropImage.activity(uri)
            .start(this@EditProfileActivity)
    }

    private fun imageConvertAndApiRequest(image: Uri) {
        lifecycleScope.launch {
            val imageFile = getFilePath(this@EditProfileActivity, image)
            val comImageFile = Compressor.compress(this@EditProfileActivity, imageFile)
            binding.ivProfile.glideImage(imageFile)
            toRequestBodyProfileImage(
                comImageFile
            )?.let { multipart ->
                viewModel.imagePart = multipart
            }
        }
    }

    private val cameraPermissionLowerArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val cameraPermissionHigherArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    private val requestPermissionCam =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (it[Manifest.permission.CAMERA] == true && it[Manifest.permission.READ_MEDIA_IMAGES] == true && it[Manifest.permission.READ_MEDIA_VIDEO] == true) {
                    if (isTakeImage) {

                        openFrontCamera()
                    } else {
                        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this@EditProfileActivity)) {
                            imagePickerNew.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            pickImage.launch("image/*")
                        }
                    }
                }
            } else {
                if (it[Manifest.permission.CAMERA] == true && it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true && it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                    if (isTakeImage) {

                        openFrontCamera()
                    } else {
                        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this@EditProfileActivity)) {
                            imagePickerNew.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            pickImage.launch("image/*")
                        }
                    }
                }
            }
        }


}