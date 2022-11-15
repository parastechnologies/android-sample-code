package com.jackbcustomer.sync.viewmodel

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.app.muselink.commonutils.imageselection.ChoosePhotoHelper
import com.app.muselink.commonutils.imageselection.FileHelper
import com.app.muselink.commonutils.imageselection.FileUtils
import com.app.muselink.commonutils.imageselection.UploaderImageDetail
import com.app.muselink.constants.AppConstants
import com.app.muselink.helpers.PermissionHelper
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File

class ImageSelectionHelper(val onImageSelected: OnImageSelected?) {

    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var IsCrop: Boolean? = null

    private var cropPictureUrl: Uri? = null
    internal var cropImageUrl: Uri? = null


    var choosePhotoHelper: ChoosePhotoHelper? = null
    var storagePermission: PermissionHelper? = null

    interface OnImageSelected {
        fun imageSelected(uploaderImageDetail: UploaderImageDetail?)
    }

    fun chooseFromCamera(activity: Activity, IsCrop: Boolean, fragment: Fragment?) {
        this.activity = activity
        this.fragment = fragment
        this.IsCrop = IsCrop
        storagePermission = PermissionHelper(activity)
        if (storagePermission?.checkPermissionCameraStorage()!!) {
            choosePhotoHelper = ChoosePhotoHelper(activity)
            choosePhotoHelper?.showAlertDialogCamera(fragment)
        } else {
            storagePermission?.checkAndRequestCameraPermission(onRequestPermissionsResult)
        }
    }
    fun chooseProfilePhoto(activity: Activity, IsCrop: Boolean, fragment: Fragment?) {
        this.activity = activity
        this.fragment = fragment
        this.IsCrop = IsCrop
        storagePermission = PermissionHelper(activity)
        if (storagePermission?.checkPermissionCameraStorage()!!) {
            choosePhotoHelper = ChoosePhotoHelper(activity)
            choosePhotoHelper?.showAlertDialogImage(fragment)
        } else {
            storagePermission?.checkAndRequestCameraPermission(onRequestPermissionsResult)
        }
    }
    fun choosePhotoVideo(activity: Activity, fragment: Fragment?) {
        this.activity = activity
        IsCrop = false
        storagePermission = PermissionHelper(activity)
        if (storagePermission?.checkPermissionCameraStorage()!!) {
            choosePhotoHelper = ChoosePhotoHelper(activity)
            choosePhotoHelper?.showAlertDialogImageVideo(fragment)
        } else {
            storagePermission?.checkAndRequestCameraPermission(onRequestPermissionsResult)
        }
    }
    val onRequestPermissionsResult = object : PermissionHelper.OnRequestPermissionsResult {
        override fun onPermissionsGranted() {
            choosePhotoHelper = ChoosePhotoHelper(activity!!)
            choosePhotoHelper?.showAlertDialogImageVideo(fragment)
        }
        override fun onPermissionsDenied() {
            storagePermission?.checkAndRequestCameraPermission(this)
        }

    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppConstants.CAMERA_REQUEST) {
            val selectedImageURI = if (data?.data != null) {
                data.data
            } else {
                choosePhotoHelper?.cameraUri
            }

            cropPictureUrl = Uri.fromFile(
                FileHelper.getInstance(activity!!).createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
            )
            val file = getPath(selectedImageURI)
            if (IsCrop == true) {
                CropImage.activity(selectedImageURI).start(activity!!);

//                if (file.exists()) {
//                    if (Build.VERSION.SDK_INT > 23) {
//                        cropImage(
//                            FileProvider.getUriForFile(
//                                activity!!,
//                                "com.omda.driverapp" + ".provider",
//                                file
//                            ),
//                            cropPictureUrl
//                        )
//                    } else {
//                        cropImage(Uri.fromFile(file), cropPictureUrl)
//                    }
//
//                } else {
//                    cropImage(data?.data, cropPictureUrl)
//                }
            } else {
                val uploaderImageDetail = UploaderImageDetail(file, file.name, file.length(), "")
                onImageSelected?.imageSelected(uploaderImageDetail)
            }
        } else if (requestCode == AppConstants.PERMISSION_SETTING_REQUEST_CODE) {
            choosePhotoVideo(activity!!, null)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                val uri = result.getUri()
                val file = File(uri.path)
                val uploaderImageDetail = UploaderImageDetail(File(uri.path), file.name, file.length(), "")
                onImageSelected?.imageSelected(uploaderImageDetail)
            }
        } else if (requestCode == AppConstants.SELECTED_IMG_CROP) {
            val uri = FileHelper.getRealPathFromURIDB(activity!!, cropImageUrl!!)
            val file = File(uri)
            val uploaderImageDetail = UploaderImageDetail(File(uri), file.name, file.length(), "")
            onImageSelected?.imageSelected(uploaderImageDetail)
        } else if (requestCode == AppConstants.PROFILE_IMAGE_REQUEST) {
            val selectedImageURI = if (data?.data != null) {
                data.data
            } else {
                choosePhotoHelper?.cameraUri
            }
            //val file = getPath(selectedImageURI)
            val file = getPath(selectedImageURI)

            if (IsCrop == true) {

                CropImage.activity(selectedImageURI)
                    .start(activity!!);

//                cropPictureUrl = Uri.fromFile(
//                    FileHelper.getInstance(activity!!)
//                        .createImageTempFile(
//                            Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_PICTURES
//                            )
//                        )
//                )
//
//                if (file.exists()) {
//                    if (Build.VERSION.SDK_INT > 23) {
//                        cropImage(
//                            FileProvider.getUriForFile(
//                                activity!!,
//                                "com.omda.driverapp" + ".provider",
//                                file
//                            ),
//                            cropPictureUrl
//                        )
//                    } else {
//                        cropImage(Uri.fromFile(file), cropPictureUrl)
//                    }
//
//                } else {
//                    cropImage(data?.data, cropPictureUrl)
//                }
            } else {
                val uploaderImageDetail = UploaderImageDetail(file, file.name, file.length(), "")
                onImageSelected?.imageSelected(uploaderImageDetail)
            }


        }
    }

    // todo to crop image
    private fun cropImage(sourceImage: Uri?, destinationImage: Uri?) {

        val intent = Intent("com.android.camera.action.CROP")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        intent.type = "image/*"

        val list = activity?.packageManager?.queryIntentActivities(intent, 0)
        val size = list?.size
        if (size == 0) {
            //Utils.showToast(mContext, mContext.getString(R.string.error_cant_select_cropping_app));
            cropImageUrl = sourceImage
            intent.putExtra(MediaStore.EXTRA_OUTPUT, sourceImage)
            if (fragment != null)
                fragment?.startActivityForResult(intent, AppConstants.SELECTED_IMG_CROP)
            else
                activity?.startActivityForResult(intent, AppConstants.SELECTED_IMG_CROP)
        } else {
            intent.setDataAndType(sourceImage, "image/*")
            val ASPECT_X = 1
            intent.putExtra("aspectX", ASPECT_X)
            val ASPECT_Y = 1
            intent.putExtra("aspectY", ASPECT_Y)
            val OUT_PUT_Y = 300
            intent.putExtra("outputY", OUT_PUT_Y)
            val OUT_PUT_X = 300
            intent.putExtra("outputX", OUT_PUT_X)
            val SCALE = true
            intent.putExtra("scale", SCALE)
            val circleCrop = false
            intent.putExtra("circleCrop", circleCrop);


            //intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, destinationImage)
            cropImageUrl = destinationImage
            if (size == 1) {
                val i = Intent(intent)
                val res = list[0]
                i.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                if (fragment != null)
                    fragment?.startActivityForResult(intent, AppConstants.SELECTED_IMG_CROP)
                else
                    activity?.startActivityForResult(intent, AppConstants.SELECTED_IMG_CROP)
            } else {
                val i = Intent(intent)
                i.putExtra(Intent.EXTRA_INITIAL_INTENTS, list?.toTypedArray<Parcelable>())
                if (fragment != null)
                    fragment?.startActivityForResult(intent, AppConstants.SELECTED_IMG_CROP)
                else
                    activity?.startActivityForResult(intent, AppConstants.SELECTED_IMG_CROP)
            }
        }
    }

    private fun getPath(uri: Uri?): File {
        val path = FileUtils.getPath(activity!!, uri!!)
        val uriString = uri.toString()
        if (uriString.startsWith("content://")) {
            var cursor: Cursor? = null
            try {
                cursor = activity?.contentResolver?.query(
                    uri!!,
                    null,
                    null,
                    null,
                    null
                )
            } finally {
                cursor!!.close()
            }
        }
        return File(path)
    }
}