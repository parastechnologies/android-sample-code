package com.in2bliss.ui.activity.home.affirmation.chooseBackground

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityChooseBackgroundBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.affirmationCreated.AffirmationCreatedActivity
import com.in2bliss.ui.activity.home.affirmationDetails.AffirmationDetailActivity
import com.in2bliss.ui.activity.imagePicker
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.checkCameraPermissionForCaptureImage
import com.in2bliss.utils.extension.checkingGalleryPermissionForPickImage
import com.in2bliss.utils.extension.compressImage
import com.in2bliss.utils.extension.getFileFromUri
import com.in2bliss.utils.extension.getUriForCamera
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseBackgroundActivity : BaseActivity<ActivityChooseBackgroundBinding>(
    layout = R.layout.activity_choose_background
) {

    private val viewModel: ChooseBackgroundViewModel by viewModels()

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.choose_a_background)
        binding.toolBar.tvSkip.gone()
        getIntentData()
        settingRecyclerView()
        onClick()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
//            viewModel.chooseBackgroundInResponse.collectLatest {
//                handleResponse(
//                    response = it,
//                    context = this@ChooseBackgroundActivity,
//                    success = { response ->
//                        viewModel.adapterChooseBackground.submitList(response.data) {
//                            binding.tvNoDatFound.visibility(
//                                isVisible = viewModel.adapterChooseBackground.currentList.isEmpty()
//                            )
//                        }
//                    },
//                    errorBlock = {
//                        binding.tvNoDatFound.visibility(
//                            isVisible = true
//                        )
//                    },
//                    showToast = false,
//                    error = { message, apiName ->
//                        alertDialogBox(
//
            //                            message = message,
//                            retry = {
//                                viewModel.retryApiRequest(
//                                    apiName = apiName
//                                )
//                            }
//                        )
//                    }
//                )
//            }

            viewModel.adapterChooseBackground.addLoadStateListener {
                when (it.refresh) {
                    is LoadState.Error -> {
                        binding.pbProgress.gone()
                    }

                    is LoadState.Loading -> {
                        binding.pbProgress.visible()
                    }

                    is LoadState.NotLoading -> {
                        binding.pbProgress.gone()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uploadData.collectLatest {
                handleResponse(
                    response = it,
                    context = this@ChooseBackgroundActivity,
                    success = { response ->
                        viewModel.selectedImage = response.fileName
                        navigate()
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    }
                )
            }
        }
    }

    private fun navigate(
        isBackgroundImage: Boolean = true
    ) {
        viewModel.initialSelectedImage = viewModel.createAffirmation?.affirmationBackground

        viewModel.createAffirmation?.affirmationBackground =
            if (isBackgroundImage) viewModel.selectedImage else {
                if (viewModel.createAffirmation?.isEdit == true) viewModel.initialSelectedImage else null
            }

        viewModel.createAffirmation?.isGalleryImage = viewModel.isGalleryImageSelected

        val bundle = bundleOf(
            AppConstant.CREATE_AFFIRMATION to Gson().toJson(viewModel.createAffirmation)
        )
        intent(
            destination = AffirmationCreatedActivity::class.java,
            bundle = bundle
        )
    }

    /**
     * get intent data [getIntentData]
     * */
    private fun getIntentData() {
        intent.getStringExtra(AppConstant.CATEGORY_NAME).let { categoryName ->
            viewModel.categoryType = when (categoryName) {
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                else -> null
            }
        }
        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { createAffirmation ->
            viewModel.createAffirmation =
                Gson().fromJson(createAffirmation, CreateAffirmation::class.java)
        }

        when (viewModel.categoryType) {
            AppConstant.HomeCategory.JOURNAL -> {
                binding.isJournal.gone()
                lifecycleScope.launch {
                    viewModel.getJournalBackground().collectLatest { textAffirmationList ->
                        lifecycleScope.launch {
                            viewModel.adapterChooseBackground.submitData(textAffirmationList)
                        }
                    }
                }
//                viewModel.retryApiRequest(
//                    apiName = ApiConstant.JOURNAL_BACKGROUND_IMAGES
//                )
            }

            AppConstant.HomeCategory.CREATE_AFFIRMATION -> {
                lifecycleScope.launch {
                    viewModel.getAffirmationBackground().collectLatest { textAffirmationList ->
                        lifecycleScope.launch {
                            viewModel.adapterChooseBackground.submitData(textAffirmationList)
                        }
                    }
                }
//                viewModel.retryApiRequest(
//                    apiName = ApiConstant.AFFIRMATION_BACKGROUND_IMAGES
//                )
            }

            else -> {}
        }
    }

    private fun settingRecyclerView() {
        val layoutManger = GridLayoutManager(this, 2)
        binding.rvChooseBackground.layoutManager = layoutManger
        binding.rvChooseBackground.itemAnimator = null
        binding.rvChooseBackground.adapter = viewModel.adapterChooseBackground

        viewModel.adapterChooseBackground.callBack = { image ->
            viewModel.isGalleryImageSelected = false
            viewModel.selectedImage = image
        }
    }

    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            when (viewModel.categoryType) {
                AppConstant.HomeCategory.JOURNAL -> {
                    val intent = Intent()
                    intent.putExtra(AppConstant.IMAGE_STRING, viewModel.selectedImage)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                AppConstant.HomeCategory.CREATE_AFFIRMATION -> {
                    if (viewModel.isGalleryImageSelected) {
                        viewModel.retryApiRequest(
                            apiName = ApiConstant.UPLOAD
                        )
                        return@setOnClickListener
                    }
                    if (viewModel.selectedImage == null && viewModel.tempImageUri==null) {
                        showToast(
                            message = getString(R.string.please_select_image)
                        )
                        return@setOnClickListener
                    }
                    navigate()
                }

                else -> {
                    val bundle = bundleOf(
                        AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.CREATE_AFFIRMATION.name
                    )
                    intent(
                        destination = AffirmationDetailActivity::class.java,
                        bundle = bundle
                    )
                }
            }
        }

        binding.cvGallery.setOnClickListener {
            pickImage()
        }

        binding.tvSkip.setOnClickListener {
            navigate(
                isBackgroundImage = false
            )
        }
    }

    private fun pickImage() {
        imagePicker(context = this, select = { selectedType ->
            if (selectedType == 0) {
                checkCameraPermissionForCaptureImage(context = this, permissionGranted = {
                    captureCameraImage()
                }, permissionNotGranted = { permissionList ->
                    cameraPermission.launch(permissionList)
                })
                return@imagePicker
            }

            checkingGalleryPermissionForPickImage(context = this, permissionGranted = {
                captureGalleryImage()
            }, permissionNotGranted = { permissionList ->
                galleryPermission.launch(permissionList)
            })
        })
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
                    setData(
                        imageUri
                    )
                }
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

    private val galleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.READ_EXTERNAL_STORAGE] == true) captureGalleryImage()
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
        registerForActivityResult(ActivityResultContracts.GetContent()) { selectedImage ->
            setData(
                uri = selectedImage
            )
        }

    private fun setData(
        uri: Uri?
    ) {
        uri?.let {
            viewModel.imageUri = it

            getFileFromUri(
                context = this@ChooseBackgroundActivity,
                uri = it,
                file = { file ->

                    viewModel.isGalleryImageSelected = true

                    viewModel.imageFile = file
                    binding.sivUploadImage.also { pic ->
                        pic.visible()
                        pic.glide(viewModel.requestManager, file)

                    }

                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.imageFile = compressImage(
                            imageFile = file,
                            activity = this@ChooseBackgroundActivity
                        )
                    }
                }
            )
        }
    }
}