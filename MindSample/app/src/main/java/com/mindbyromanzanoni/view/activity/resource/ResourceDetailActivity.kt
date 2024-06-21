package com.mindbyromanzanoni.view.activity.resource

import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.resource.ResourceTypeList
import com.mindbyromanzanoni.databinding.ActivityResourceDetailBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.PdfViewAndDownload
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.utils.writeExternalStoragePermission
import com.mindbyromanzanoni.view.activity.dashboard.DashboardActivity
import com.mindbyromanzanoni.view.activity.openPdfViewer.OpenPdfActivity
import com.mindbyromanzanoni.view.activity.verificationCode.VerificationCodeActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResourceDetailActivity : BaseActivity<ActivityResourceDetailBinding>() {
    var activity = this@ResourceDetailActivity
    private var data: ResourceTypeList? = null
    private var pdfViewOrDownload: PdfViewAndDownload? = PdfViewAndDownload()
    private val viewModal: HomeViewModel by viewModels()
    override fun getLayoutRes(): Int {
        return R.layout.activity_resource_detail
    }

    override fun initView() {
        setToolbar()
        getIntentData()
        clickListeners()
    }

    override fun viewModel() {}
    private fun callBackPdfViewDownload() {
        writeExternalStoragePermission(applicationContext, {
            pdfViewOrDownload?.startDownload(
                applicationContext,
                data?.pdfFileName.toString(),
                data?.title.toString()
            )
        }, {})
    }

    private fun clickListeners() {
        binding.apply {
            icDownloadPdf.setOnClickListener {
                try {
                    callBackPdfViewDownload()
                }catch (_:Exception){}
            }
            relativeLayout.setOnClickListener {
                val bundle = bundleOf(AppConstants.PDF_URL to data?.pdfFileName)
                launchActivity<OpenPdfActivity>(0, bundle) { }
            }
        }
    }

    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            val screenType = intent.getString(AppConstants.SCREEN_TYPE).toString()
            if (screenType == AppConstants.HOME_SCREEN) {
                binding.relativeLayout.gone()
                observeDataFromViewModal()
                val eventId = intent.getInt(AppConstants.EVENT_ID).toString()
                RunInScope.ioThread {
                    viewModal.responseOnTheBasesOfCategory(eventId = eventId, "3")
                }
                return
            }
            val getResourceData = intent.getString(AppConstants.RESOURCE_DETAILS).toString()
            data = Gson().fromJson(getResourceData, ResourceTypeList::class.java)
            setDataApi()
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = "Resources Detail"
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    private fun setDataApi() {
        binding.apply {
            tvUsername.text = data?.title ?: ""
            tvPdfName.text = resources.getString(R.string.pdf_name)
            tvDesc.text = data?.content?.trimStart()
            ivHome.setImageFromUrl(R.drawable.no_image_placeholder, data?.imageName)
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.allTypeResponse.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val responseData = isResponse.data
                        if (responseData?.isSuccess() == true) {
                            binding.relativeLayout.gone()
                            data = ResourceTypeList(
                                pdfFileName = responseData.data?.fileName ?: "",
                                title = responseData.data?.title ?: "",
                                content = responseData.data?.content ?: "",
                                imageName = responseData.data?.videoThumbImage)
                            binding.relativeLayout.visible()
                            setDataApi()
                        } else {
                            finishActivity()
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
                MyProgressBar.showProgress(activity)
            } else {
                MyProgressBar.hideProgress()
            }
        }
    }
}