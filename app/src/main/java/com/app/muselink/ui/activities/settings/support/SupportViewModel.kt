package com.app.muselink.ui.activities.settings.support

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.RequestCodeConstants
import com.app.muselink.data.modals.responses.SupportResponse
import com.app.muselink.retrofit.RequestBodyRetrofit
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.ui.base.activity.BaseViewModel
import com.app.muselink.util.FileUtils
import com.app.muselink.retrofit.Resource
import com.app.muselink.util.SyncConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream


class SupportViewModel @ViewModelInject constructor(
    val repository: ApiRepository, activity: Activity
) : BaseViewModel(activity) {


    var review = "";
    //    var charCounts  = "0/500";
    var supportType = ""
    var pathFile = ""

    var charCounts = ObservableField<String>()
    var showLoader = ObservableField<Boolean>()
    var enableButton = ObservableField<Boolean>()

    var file: MultipartBody.Part? = null

    val formErrors = ObservableArrayList<AppConstants.FormErrors>()

    init {
        charCounts.set("0/500")
    }

    fun isFormValid(): Boolean {
        formErrors.clear()
        if (supportType.equals(SyncConstants.SupportTypes.REPORT_A_PROBLEM.value)) {
            if (review.isEmpty()) {
                formErrors.add(AppConstants.FormErrors.MISSING_REVIEW)
            }
        } else {
            if (review.isEmpty()) {
                formErrors.add(AppConstants.FormErrors.MISSING_REVIEW)
            }
        }
        return formErrors.isEmpty()
    }

    fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        var charLength = s.toString().length
        if (supportType.equals(SyncConstants.SupportTypes.REPORT_A_PROBLEM.value)) {
            charCounts.set(charLength.toString() + "/500")
        } else {

        }
    }

    private val requestApi = MutableLiveData<HashMap<String, RequestBody>>()

    fun uploadFile() {
        val intentUpload = Intent()
        intentUpload.setType("*/*");
        intentUpload.action = Intent.ACTION_GET_CONTENT
        intentUpload.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivityForResult(intentUpload, RequestCodeConstants.REQUEST_CODE_SELECT_FILE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCodeConstants.REQUEST_CODE_SELECT_FILE) {
                var path: String? = ""
                val uri: Uri? = data?.data
                val uriString = uri?.toString()
                var displayName = ""
                if (uriString!!.contains("msf:") || uriString.replace(
                        "%3A",
                        ":"
                    ).contains("msf:")
                ) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            activity.contentResolver.query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                        val fileByte =
                            (activity.contentResolver?.openInputStream(uri))?.readBytes()
                        val originFileAnd10 = File(
                            activity.getExternalFilesDir(null),
                            displayName
                        )
                        path = originFileAnd10.path
                        try {
                            val fos = FileOutputStream(path)
                            fos.write(fileByte)
                            fos.close()
                            cursor?.close()
                        } catch (e: Exception) {
                        }
                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    path = FileUtils.getPath(activity ,uri)
                }
                pathFile = path!!
            }
        }
    }

    fun callApiSupport() {

        if (isFormValid()) {
            val requests = HashMap<String, RequestBody>()
            if (supportType.equals(SyncConstants.SupportTypes.REPORT_A_PROBLEM.value)) {
                file = RequestBodyRetrofit.toRequestBodySupportFile(pathFile)
                requests.put(SyncConstants.APIParams.SUPPORT_TYPE.value, RequestBodyRetrofit.toRequestBodyString(supportType))
                requests.put(SyncConstants.APIParams.REVIEW.value, RequestBodyRetrofit.toRequestBodyString(review))
                requests.put(
                    SyncConstants.APIParams.USER_ID.value,
                    RequestBodyRetrofit.toRequestBodyString(SharedPrefs.getUser().id.toString())
                )
            } else {
                file = RequestBodyRetrofit.toRequestBodyNullImageSupport(pathFile)
                requests.put(SyncConstants.APIParams.REVIEW.value, RequestBodyRetrofit.toRequestBodyString(review))
                requests.put(SyncConstants.APIParams.SUPPORT_TYPE.value, RequestBodyRetrofit.toRequestBodyString(supportType))
                requests.put(
                    SyncConstants.APIParams.USER_ID.value,
                    RequestBodyRetrofit.toRequestBodyString(SharedPrefs.getUser().id.toString())
                )
            }
            requestApi.value = requests
        }

    }

    private val _support = requestApi.switchMap { requestApi ->
        repository.supportUser(requestApi, file!!)
    }

    val _supportRes: LiveData<Resource<SupportResponse>> = _support


}