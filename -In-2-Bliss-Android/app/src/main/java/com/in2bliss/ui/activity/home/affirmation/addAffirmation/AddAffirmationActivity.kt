package com.in2bliss.ui.activity.home.affirmation.addAffirmation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Observable
import android.net.Uri
import android.os.Build
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.in2bliss.BuildConfig
import com.in2bliss.BuildConfig.AFFIRMATION_BASE_URL
import com.in2bliss.R
import com.in2bliss.base.BaseActivity
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.data.model.createAffirmation.CreateAffirmation
import com.in2bliss.data.model.journalStreak.JournalDetail
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.ActivityAddAffirmationBinding
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesActivity
import com.in2bliss.ui.activity.home.affirmation.affirmationCategories.AffirmationCategoriesViewModel
import com.in2bliss.ui.activity.home.affirmation.chooseBackground.ChooseBackgroundActivity
import com.in2bliss.ui.activity.home.affirmation.createAffirmationBottomSheet.CreateAffirmationBottomSheet
import com.in2bliss.ui.activity.home.fragment.journal.JournalActivity
import com.in2bliss.ui.activity.imagePicker
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.checkCameraPermissionForCaptureImage
import com.in2bliss.utils.extension.checkingGalleryPermissionForPickImage
import com.in2bliss.utils.extension.compressImage
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.getCurrentTime
import com.in2bliss.utils.extension.getFileFromUri
import com.in2bliss.utils.extension.getUriForCamera
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.hideKeyboard
import com.in2bliss.utils.extension.intent
import com.in2bliss.utils.extension.showToast
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AddAffirmationActivity : BaseActivity<ActivityAddAffirmationBinding>(
    layout = R.layout.activity_add_affirmation
) {
    private val viewModel: AddAffirmationViewModel by viewModels()
    private val affirmationUpdateViewModel: AffirmationCategoriesViewModel by viewModels()

    @Inject
    lateinit var requestManager: RequestManager

    override fun init() {
        binding.toolBar.tvTitle.setText(R.string.my_affirmations)
        binding.data = viewModel
        gettingBundle()
        onClick()
        observer()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.uploadData.collectLatest {
                handleResponse(response = it,
                    context = this@AddAffirmationActivity,
                    success = { response ->
                        viewModel.backgroundImage = response.fileName
                        when (viewModel.categoryType) {
                            AppConstant.HomeCategory.JOURNAL -> {
                                if (viewModel.journalData != null) {
                                    viewModel.retryApiRequest(
                                        ApiConstant.EDIT_JOURNAL
                                    )
                                    return@handleResponse
                                }
                                viewModel.retryApiRequest(
                                    ApiConstant.JOURNAL_ADD
                                )
                            }

                            else -> {
                                if (viewModel.isEdit) {
                                    updateTextAffirmation()
                                } else navigateToCategoryScreen(
                                    image = response.fileName.orEmpty()
                                )
                            }
                        }
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }

        lifecycleScope.launch {
            viewModel.guidedJournalData.collectLatest {
                handleResponse(response = it,
                    context = this@AddAffirmationActivity,
                    success = { _ ->

                        val bundle = bundleOf(
                            AppConstant.CATEGORY_NAME to viewModel.categoryType?.name

                        )
                        AppConstant.STREAK_COUNT.let { key ->
                            /** if intent has streakCount then pushing it to next screen**/
                            if (intent.hasExtra(key)) {
                                bundle.putString(key, intent.getStringExtra(key))
                            }
                        }
                        activityResult.launch(Intent(
                            this@AddAffirmationActivity, JournalActivity::class.java
                        ).apply {
                            putExtras(bundle)
                        })
                        /** added by pk **/
                        finish()
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }

        lifecycleScope.launch {
            viewModel.editJournal.collectLatest {
                handleResponse(response = it,
                    context = this@AddAffirmationActivity,
                    success = { _ ->
                        val intent = Intent()
                        val data = JournalDetail(
                            description = viewModel.journalDescription.get().orEmpty(),
                            backgroundImage = viewModel.backgroundImage.orEmpty(),
                            id = viewModel.journalData?.id.orEmpty(),
                            date = viewModel.journalData?.date.orEmpty()
                        )
                        intent.putExtra(
                            AppConstant.JOURNAL_DATA, Gson().toJson(data)
                        )
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    },
                    error = { message, apiName ->
                        alertDialogBox(
                            message = message
                        ) {
                            viewModel.retryApiRequest(
                                apiName = apiName
                            )
                        }
                    })
            }
        }

        lifecycleScope.launch {
            affirmationUpdateViewModel.addUpdateAffirmationResponse.collectLatest {
                handleResponse(response = it, context = this@AddAffirmationActivity, success = {
                    setResult(Activity.RESULT_OK)
                    finish()
                }, error = { message, apiName ->
                    alertDialogBox(message = message, retry = {
                        viewModel.retryApiRequest(
                            apiName = apiName
                        )
                    })
                })
            }
        }
    }

    private var imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if ((result.data?.getStringExtra(AppConstant.IMAGE_STRING) ?: "").isNotBlank()) {
                    viewModel.backgroundImage =
                        result.data?.getStringExtra(AppConstant.IMAGE_STRING) ?: ""
                    binding.view.visible()
                    binding.sivUploadImage.glide(
                        requestManager = requestManager,
                        image = AFFIRMATION_BASE_URL.plus(viewModel.backgroundImage),
                        error = R.drawable.ic_error_place_holder,
                        placeholder = R.drawable.ic_error_place_holder
                    )
//                    navigateToCategoryScreen(
//                        image = result.data?.getStringExtra(AppConstant.IMAGE_STRING)?:""
//                    )
                }
            }
        }


    private fun navigateToCategoryScreen(
        image: String, isEdit: Boolean = false
    ) {
        val bundle = bundleOf(
            AppConstant.IMAGE_STRING to image,
            AppConstant.AFFIRMATION to viewModel.affirmation.get().orEmpty(),
            AppConstant.EDIT to isEdit,
            AppConstant.TYPE to "0",
            AppConstant.ID to viewModel.textAffirmationData?.id.toString(),
            AppConstant.CATEGORY_NAME to AppConstant.HomeCategory.CREATE_AFFIRMATION,
        )

        activityResult.launch(Intent(
            this@AddAffirmationActivity, AffirmationCategoriesActivity::class.java
        ).apply {
            putExtras(bundle)
        })
    }

    private fun gettingBundle() {

        /** Getting the category */
        intent.getStringExtra(AppConstant.CATEGORY_NAME)?.let { categoryName ->
            viewModel.categoryType = when (categoryName) {
                AppConstant.HomeCategory.CREATE_AFFIRMATION.name -> AppConstant.HomeCategory.CREATE_AFFIRMATION
                AppConstant.HomeCategory.JOURNAL.name -> AppConstant.HomeCategory.JOURNAL
                else -> null
            }
        }

        intent.getStringExtra(AppConstant.DATE)?.let { date ->
            viewModel.date = date
        }


        intent.getBooleanExtra(AppConstant.EDIT, false).let { isEdit ->
            viewModel.isEdit = isEdit
        }

        intent.getStringExtra(AppConstant.CREATE_AFFIRMATION)?.let { createAffirmation ->
            viewModel.createAffirmationDetails =
                Gson().fromJson(createAffirmation, CreateAffirmation::class.java)
        }

        /** Edit journal data */
        intent.getStringExtra(AppConstant.JOURNAL_DATA)?.let { data ->
            viewModel.journalData = Gson().fromJson(data, JournalDetail::class.java)
            setJournalData()
        }

        /** Text affirmation data*/
        intent.getStringExtra(AppConstant.TEXT_AFFIRMATION_DATA)?.let { data ->
            viewModel.textAffirmationData =
                Gson().fromJson(data, AffirmationListResponse.Data::class.java)
            setTextAffirmationData()
        }

        /** If journal screen */
        if (viewModel.categoryType == AppConstant.HomeCategory.JOURNAL) {
            binding.toolBar.tvTitle.text = formatDate(
                date = "${viewModel.date ?: getCurrentDate()} ${getCurrentTime()}",
                inputFormat = "yyyy-MM-dd HH:mm",
                outPutFormat = "MMMM dd, hh:mm aa"
            )
            binding.btnContinue.setText(R.string.save)
        }

        /** Come for transcript */
        intent.getBooleanExtra(AppConstant.TRANSCRIPT, false).let { isTranscript ->
            viewModel.isTranscript = isTranscript
        }

        /** Add journal */
        binding.journal.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.JOURNAL
        )
        binding.viewJournal.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.JOURNAL && viewModel.journalData?.backgroundImage != null
        )

        /** Add text affirmation */
        binding.addAffirmation.visibility(
            isVisible = (viewModel.categoryType != AppConstant.HomeCategory.CREATE_AFFIRMATION) && (viewModel.categoryType != (AppConstant.HomeCategory.JOURNAL) && viewModel.isTranscript.not())
        )
        binding.view.visibility(
            isVisible = (viewModel.categoryType != AppConstant.HomeCategory.CREATE_AFFIRMATION) && (viewModel.categoryType != (AppConstant.HomeCategory.JOURNAL) && viewModel.isTranscript.not() && viewModel.textAffirmationData != null)
        )

        /** Create affirmation*/
        binding.createAffirmation.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION && viewModel.isTranscript.not()
        )
        binding.transcript.visibility(
            isVisible = viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION && viewModel.isTranscript
        )
        binding.toolBar.tvSkip.visibility(
            isVisible = viewModel.isTranscript
        )

        if (viewModel.categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION) {
            binding.toolBar.tvTitle.setText(if (viewModel.createAffirmationDetails?.isEdit == true) R.string.edit_your_affirmation else R.string.create_affirmation)
        }

        if (viewModel.isTranscript) {
            binding.toolBar.tvTitle.setText(if (viewModel.createAffirmationDetails?.isEdit == true) R.string.edit_your_transcript else R.string.add_your_transcript)
        }

        /** Setting create affirmation details */
        viewModel.setCreateAffirmationDetails()
    }

    private fun setTextAffirmationData() {
        binding.cvGallery.gone()
        viewModel.backgroundImage = viewModel.textAffirmationData?.thumbnail
        binding.sivUploadImage.glide(
            requestManager = requestManager,
            image = BuildConfig.AFFIRMATION_BASE_URL.plus(viewModel.textAffirmationData?.thumbnail),
            error = R.drawable.ic_error_place_holder,
            placeholder = R.drawable.ic_error_place_holder
        )
        viewModel.affirmation.set(viewModel.textAffirmationData?.description)
        viewModel.textCount.set("${viewModel.textAffirmationData?.description?.length}/50")
    }

    private fun setJournalData() {
        binding.cvGalleryJournal.gone()
        journalBackground(
            image = viewModel.journalData?.backgroundImage ?: ""
        )
        viewModel.journalDescription.set(viewModel.journalData?.description)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onClick() {
        binding.toolBar.ivBack.setOnClickListener {
            finish()
        }

        binding.edtAffirmation.doAfterTextChanged { text ->
            viewModel.textCount.set("${text?.length}/100")
        }

        binding.cvGalleryJournal.setOnClickListener {
            val intent = Intent(this, ChooseBackgroundActivity::class.java)
            intent.putExtra(AppConstant.CATEGORY_NAME, AppConstant.HomeCategory.JOURNAL.name)
            resultLauncher.launch(intent)
        }

        binding.edtCreateAffirmation.doAfterTextChanged { text ->
            viewModel.createAffirmationCount.set("${text?.length}/1000")
        }


        binding.edtTranscript.addTextChangedListener { text ->
            text.let {
                val count = text?.trim()?.toString()?.ifEmpty { null }?.split(" ")?.size?:0
                viewModel.transcriptCount.set("${count}/1000")
//                text.toString().split(" ").size
            }
        }

        binding.cvGallery.setOnClickListener {
            pickImage()
        }

        binding.cvAttachImage.setOnClickListener {
            pickImage()
        }

        binding.chooseBackGround.setOnClickListener {
            val intent =
                Intent(this@AddAffirmationActivity, ChooseTextAffirmationActivity::class.java)
            imageResultLauncher.launch(intent)
        }

        binding.btnContinue.setOnClickListener {
            when (viewModel.categoryType) {
                (AppConstant.HomeCategory.CREATE_AFFIRMATION) -> {
                    if (viewModel.isTranscript) {
                        if (transcriptValidation()) {
                            dialogBox()
                        }
                        return@setOnClickListener
                    }

                    if (createAffirmationValidation()) {
                        if (viewModel.createAffirmationDetails == null) {
                            viewModel.createAffirmationDetails = CreateAffirmation()
                        }

                        viewModel.createAffirmationDetails?.affirmationTitle = viewModel.title.get()
                        viewModel.createAffirmationDetails?.affirmationDetail =
                            viewModel.createAffirmation.get()?:""

                        val bundle = bundleOf(
                            AppConstant.TRANSCRIPT to true,
                            AppConstant.CATEGORY_NAME to viewModel.categoryType?.name,
                            AppConstant.CREATE_AFFIRMATION to Gson().toJson(viewModel.createAffirmationDetails)
                        )
                        intent(
                            destination = AddAffirmationActivity::class.java, bundle = bundle
                        )
                    }
                }

                AppConstant.HomeCategory.JOURNAL -> {
                    if (isValidateAddJournal()) {
                        if (viewModel.journalData != null) {
                            if (viewModel.imageUri != null) {
                                viewModel.retryApiRequest(
                                    apiName = ApiConstant.UPLOAD
                                )
                                return@setOnClickListener
                            }

                            viewModel.retryApiRequest(
                                ApiConstant.EDIT_JOURNAL
                            )
                            return@setOnClickListener
                        }

                        if (viewModel.isAdminImageSelected) {
                            viewModel.retryApiRequest(
                                ApiConstant.JOURNAL_ADD
                            )
                            return@setOnClickListener
                        }

                        viewModel.retryApiRequest(
                            apiName = ApiConstant.UPLOAD
                        )
                    }
                }

                else -> {
                    if (validateForTextAffirmation()) {
                        if (viewModel.isEdit) {
//                            navigateToCategoryScreen(
//                                image = viewModel.textAffirmationData?.background.orEmpty(),
//                                isEdit = true
//                            )
                            if (viewModel.imageUri != null) {
                                viewModel.retryApiRequest(
                                    apiName = ApiConstant.UPLOAD
                                )
                            } else updateTextAffirmation()
                            return@setOnClickListener
                        }
                        if (viewModel.backgroundImage != null) {
                            navigateToCategoryScreen(
                                image = viewModel.backgroundImage ?: ""
                            )
                        } else {
                            viewModel.retryApiRequest(
                                apiName = ApiConstant.UPLOAD
                            )
                        }

                    }
                }
            }
        }

        binding.etTranscriptTitle.doAfterTextChanged { text ->
            binding.ivTitleCancel.visibility((text?.length ?: "".length) > 0)
        }

        binding.ivTitleCancel.setOnClickListener {
            viewModel.title.set("")
        }

        binding.edtTranscript.setOnTouchListener { view, event ->
            view?.parent?.requestDisallowInterceptTouchEvent(true)
            when (event?.action?.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_UP -> view?.parent?.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        binding.edtCreateAffirmation.setOnTouchListener { view, event ->
            view?.parent?.requestDisallowInterceptTouchEvent(true)
            when (event?.action?.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_UP -> view?.parent?.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        binding.edtDescriptionJournal.setOnTouchListener { view, event ->
            view?.parent?.requestDisallowInterceptTouchEvent(true)
            when (event?.action?.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_UP -> view?.parent?.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        binding.edtDescriptionJournal.doAfterTextChanged { text ->
            if (text?.contains(viewModel.defaultTextForJournal) == false) {
                binding.edtDescriptionJournal.setText(viewModel.defaultTextForJournal)
                binding.edtDescriptionJournal.setSelection(viewModel.defaultTextForJournal.length)
            }
        }

        binding.toolBar.tvSkip.setOnClickListener {
            dialogBox(
                isTranscript = false
            )
        }

        binding.edtTranscript.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_DONE
        }

        binding.edtAffirmation.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_DONE
        }

        binding.edtDescriptionJournal.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_DONE
        }

        binding.edtCreateAffirmation.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_DONE
        }

        binding.edtTitleJournal.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            p1 == EditorInfo.IME_ACTION_DONE
        }
    }


    private fun dialogBox(
        isTranscript: Boolean = true
    ) {
        viewModel.createAffirmationDetails?.transcript = if (isTranscript) {
            viewModel.transcript.get()
        } else null

        CreateAffirmationBottomSheet().apply {
            arguments = bundleOf(
                AppConstant.CREATE_AFFIRMATION to Gson().toJson(viewModel.createAffirmationDetails)
            )
        }.also {
            it.show(
                supportFragmentManager, null
            )
        }
    }

    private fun updateTextAffirmation() {
        affirmationUpdateViewModel.affirmationId = viewModel.textAffirmationData?.id.toString()
        affirmationUpdateViewModel.affirmation = viewModel.affirmation.get().orEmpty()
        affirmationUpdateViewModel.image = viewModel.backgroundImage
        affirmationUpdateViewModel.category = viewModel.getCategoryAndSubCategory()
        affirmationUpdateViewModel.retryApiRequest(
            apiName = ApiConstant.AFFIRMATION_UPDATE
        )
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(AppConstant.IMAGE_STRING)?.let { image ->
                    viewModel.isAdminImageSelected = true
                    journalBackground(
                        image = image
                    )
                }
            }
        }

    private fun journalBackground(image: String) {
        viewModel.backgroundImage = image

        CoroutineScope(Dispatchers.Main).launch {
            binding.viewJournal.visible()

            binding.sivUploadImageJournal.glide(
                requestManager = requestManager,
                image = BuildConfig.JOURNAL_BASE_URL.plus(image),
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
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

    private fun captureGalleryImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pickImageRequest.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            return
        }
        pickGalleryImage.launch("image/*")
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

    private val pickImageRequest =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.imageUri = uri
            uri?.let { imageUri ->
                setImageAndApiRequest(
                    imageUri = imageUri
                )
            }
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

    private fun setImageAndApiRequest(imageUri: Uri) {
        binding.view.visible()
        getFileFromUri(context = this, uri = imageUri, file = { file ->
            viewModel.imageFile = file
            lifecycleScope.launch {
                viewModel.imageFile = compressImage(
                    imageFile = viewModel.imageFile, activity = this@AddAffirmationActivity
                )

                withContext(Dispatchers.Main) {
                    binding.sivUploadImage.glide(
                        requestManager = requestManager,
                        image = imageUri,
                        error = R.drawable.ic_error_place_holder,
                        placeholder = R.drawable.ic_error_place_holder
                    )

                    if (viewModel.categoryType == AppConstant.HomeCategory.JOURNAL) {
                        binding.viewJournal.visible()
                        viewModel.isAdminImageSelected = false
                        binding.sivUploadImageJournal.glide(
                            requestManager = requestManager,
                            image = imageUri,
                            error = R.drawable.ic_error_place_holder,
                            placeholder = R.drawable.ic_error_place_holder
                        )
                    }
                }
            }
        })
    }

    private val galleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.READ_EXTERNAL_STORAGE] == true) captureGalleryImage()
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

    private fun validateForTextAffirmation(): Boolean {
        return when {
            (viewModel.affirmation.get()?.trim().isNullOrEmpty()) -> {
                showToast(
                    message = getString(R.string.please_enter_your_affirmation)
                )
                false
            }

            (viewModel.imageUri == null && viewModel.backgroundImage == null) -> {
                showToast(
                    message = getString(R.string.please_select_your_affirmation_background)
                )
                false
            }

            else -> true
        }
    }

    private fun isValidateAddJournal(): Boolean {
        return when {
            ((viewModel.journalDescription.get()?.length
                ?: 0) <= viewModel.defaultTextForJournal.length) -> {
                showToast(getString(R.string.please_enter_description))
                false
            }

            else -> true
        }
    }

    private fun createAffirmationValidation(): Boolean {
        return when {
            (viewModel.title.get()?.trim().isNullOrEmpty()) -> {
                showToast(
                    message = getString(R.string.please_enter_your_affirmation_title)
                )
                false
            }

//            (viewModel.createAffirmation.get()?.trim().isNullOrEmpty()) -> {
//                showToast(
//                    message = getString(R.string.please_enter_your_affirmation_details)
//                )
//                false
//            }

            else -> true
        }
    }

    private fun transcriptValidation(): Boolean {
        return when {
            (viewModel.transcript.get()?.trim().isNullOrEmpty()) -> {
                showToast(
                    message = getString(R.string.please_enter_your_transcript)
                )
                false
            }
            ((viewModel.transcriptCount.get()?.split("/")?.get(0)?.toInt() ?:0) >1000) -> {
                showToast(
                    message = getString(R.string.max_limit_exceeded)
                )
                false
            }

            else -> true
        }
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
}

