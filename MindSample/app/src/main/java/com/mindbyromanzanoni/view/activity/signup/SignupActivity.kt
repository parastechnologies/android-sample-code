package com.mindbyromanzanoni.view.activity.signup

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivitySignupBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar.hideProgress
import com.mindbyromanzanoni.utils.MyProgressBar.showProgress
import com.mindbyromanzanoni.utils.applyTextWatcherAndFilter
import com.mindbyromanzanoni.utils.applyTextWatcherForPasswordValidate
import com.mindbyromanzanoni.utils.checkCameraPermissionForCaptureImage
import com.mindbyromanzanoni.utils.checkingGalleryPermissionForPickImage
import com.mindbyromanzanoni.utils.compressImage
import com.mindbyromanzanoni.utils.constant.AppConstants.SCREEN_TYPE
import com.mindbyromanzanoni.utils.constant.AppConstants.SIGN_UP
import com.mindbyromanzanoni.utils.constant.AppConstants.USER_EMAIL
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.getFileFromUri
import com.mindbyromanzanoni.utils.getUriForCamera
import com.mindbyromanzanoni.utils.imagePicker
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.view.activity.login.LoginActivity
import com.mindbyromanzanoni.view.activity.verificationCode.VerificationCodeActivity
import com.mindbyromanzanoni.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class SignupActivity : BaseActivity<ActivitySignupBinding>() {
    private val viewModal: AuthViewModel by viewModels()
    var activity = this@SignupActivity

    @Inject
    lateinit var validator: Validator

    override fun getLayoutRes() = R.layout.activity_signup

    override fun initView() {
        getTextWatcher()
        onClickListener()
        setToolbar()
        observeDataFromViewModal()
    }


    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun getTextWatcher() {
        binding.etEmail.applyTextWatcherAndFilter(validator, binding.ivTick)
        binding.etPassword.applyTextWatcherForPasswordValidate(validator, binding.tvDec)
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            binding.toolbar.tvToolTitle.text = getString(R.string.lets_start)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }


    private fun pickImage() {
        imagePicker(activity,{ selectedType ->
                if (selectedType == 0) {
                    checkCameraPermissionForCaptureImage(this,{
                        captureCameraImage()
                    }, { permissionList ->
                        cameraPermission.launch(permissionList)
                    })
                    return@imagePicker
                }

                checkingGalleryPermissionForPickImage(activity, {
                        captureGalleryImage()
                    }, { permissionList ->
                        galleryPermission.launch(permissionList)
                    }
                )
            }
        )
    }

    private val galleryPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.READ_EXTERNAL_STORAGE] == true) captureGalleryImage()
        }


    private val pickImageRequest = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModal.imageUri = uri
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

    private val pickGalleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            viewModal.imageUri = uri
            uri?.let { imageUri ->
                setImageAndApiRequest(imageUri)
            }
        }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (result[Manifest.permission.CAMERA] == true) captureCameraImage()
            return@registerForActivityResult
        }
        if (result[Manifest.permission.CAMERA] == true && result[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) captureCameraImage()
    }


    private fun captureCameraImage() {
        viewModal.tempImageUri = getUriForCamera(this)
        cameraImage.launch(viewModal.tempImageUri)
    }


    private val cameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                viewModal.imageUri = viewModal.tempImageUri
                viewModal.imageUri?.let { imageUri ->
                    setImageAndApiRequest( imageUri)
                }
            }
        }


    private fun setImageAndApiRequest(imageUri: Uri) {
        getFileFromUri( this, imageUri) { file ->
            viewModal.image = file
            lifecycleScope.launch {
                viewModal.image = compressImage(viewModal.image,activity)

                withContext(Dispatchers.Main) {
                    binding.cvUserImage.setImageFromUrl(
                        R.drawable.no_image_placeholder,
                        imageUri.toString(),
                    )
                }
            }
        }
    }

    /** set on click listener*/
    private fun onClickListener() {
        binding.apply {
            tvSignup.setOnClickListener {
                launchActivity<LoginActivity> { }
            }
            btnCreateAccount.setOnClickListener {
                if (validator.validateRegistration(activity,binding)){
                    viewModal.countryCode.set(binding.countryCodePicker.selectedCountryCodeWithPlus)
                    RunInScope.ioThread {
                        viewModal.hitRegisterApi()
                    }
                }
            }
            pickImage.setOnClickListener {
                pickImage()
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.signUpResponseSharedFlow.collectLatest {isResponse->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            val bundle = bundleOf(SCREEN_TYPE to SIGN_UP, USER_EMAIL to viewModal.email.get().toString())
                            launchActivity<VerificationCodeActivity>(0, bundle) { }
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

        viewModal.showLoading.observe(activity) {
            if (it) {
                showProgress(activity)
            } else {
                hideProgress()
            }
        }
    }
}