package com.app.muselink.commonutils.imageselection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.app.muselink.R
import com.app.muselink.constants.AppConstants
import java.text.SimpleDateFormat
import java.util.*


class ChoosePhotoHelper(private val mContext: Activity) {
    internal var cameraUri: Uri? = null
    fun showAlertDialogCamera(fragment: Fragment?) {
        // todo Gallery Picker
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        // todo Camera Picker
        cameraUri = createImageUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        if (fragment != null) {
            fragment.startActivityForResult(cameraIntent, AppConstants.PROFILE_IMAGE_REQUEST, null)
        } else {
            mContext.startActivityForResult(cameraIntent, AppConstants.PROFILE_IMAGE_REQUEST, null)
        }
    }
    fun showAlertDialogImage(fragment: Fragment?) {
        // todo Gallery Picker
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        // todo Camera Picker
        cameraUri = createImageUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        // todo Add them to an intent array
        val intents = arrayOf(cameraIntent)
        // todo Create a choose from your first intent and pass the intent array
        val chooserIntent = Intent.createChooser(galleryIntent, mContext.getString(R.string.choose_photo))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)
        if (fragment != null) {
            fragment.startActivityForResult(chooserIntent, AppConstants.PROFILE_IMAGE_REQUEST, null)
        } else {
            mContext.startActivityForResult(chooserIntent, AppConstants.PROFILE_IMAGE_REQUEST, null)
        }
    }
    fun showAlertDialogImage(activityResultLauncher: ActivityResultLauncher<Intent>) {
        // todo Gallery Picker
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        // todo Camera Picker
        cameraUri = createImageUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        // todo Add them to an intent array
        val intents = arrayOf(cameraIntent)
        // todo Create a choose from your first intent and pass the intent array
        val chooserIntent =
            Intent.createChooser(galleryIntent, mContext.getString(R.string.choose_photo))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)
         activityResultLauncher.launch(chooserIntent)

    }
    @SuppressLint("IntentReset")
    fun showAlertDialogImageVideo(fragment: Fragment?) {
        // todo Gallery Picker
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "video/*,image/*"
        // todo Camera Picker
        cameraUri = createImageUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        // todo Add them to an intent array
        val intents = arrayOf(cameraIntent)
        // todo Create a choose from your first intent and pass the intent array
        val chooserIntent =
            Intent.createChooser(galleryIntent, mContext.getString(R.string.choose_photo))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)

        if (fragment != null) {
            fragment.startActivityForResult(chooserIntent, AppConstants.CAMERA_REQUEST, null)
        } else {
            mContext.startActivityForResult(chooserIntent, AppConstants.CAMERA_REQUEST,null)
        }
    }
    private fun createImageUri(): Uri? {
        val contentResolver = mContext.contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }
}
