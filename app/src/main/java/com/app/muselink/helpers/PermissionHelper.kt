package com.app.muselink.helpers

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import com.app.muselink.R

class PermissionHelper(private var context: Activity) {
    private val REQUEST_CODE_STORAGE_PERMISSION = 1001
    private val REQUEST_CODE_CAMERA_PERMISSION = 1002
    private val REQUEST_CODE_LOCATION_PERMISSION = 1003
    private val REQUEST_CODE_PHONE_CALL = 1004
    private val REQUEST_CODE_RECORD_AUDIO = 1005
    private var onRequestPermissionsResult: OnRequestPermissionsResult? = null
    fun checkAndRequestPermissionAudio(onRequestPermissionsResult: OnRequestPermissionsResult): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissionRecordAudio()) {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                onRequestPermissionsResult.onPermissionsGranted()
                false
            } else {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                requestPermissionRecordAudio()
                true
            }
        } else {
            this.onRequestPermissionsResult = onRequestPermissionsResult
            onRequestPermissionsResult.onPermissionsGranted()
            true
        }
    }

    fun checkAndRequestCameraPermission(onRequestPermissionsResult: OnRequestPermissionsResult): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissionCameraStorage()) {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                onRequestPermissionsResult.onPermissionsGranted()
                false
            } else {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                requestPermissionCameraStorgae()
                true
            }
        } else {
            this.onRequestPermissionsResult = onRequestPermissionsResult
            onRequestPermissionsResult.onPermissionsGranted()
            true
        }
    }

    fun checkAndRequestPhoneCallPermission(onRequestPermissionsResult: OnRequestPermissionsResult): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissionPhoneCall()) {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                onRequestPermissionsResult.onPermissionsGranted()
                false
            } else {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                requestPermissionPhoneCall()
                true
            }
        } else {
            this.onRequestPermissionsResult = onRequestPermissionsResult
            onRequestPermissionsResult.onPermissionsGranted()
            true
        }
    }

    fun checkAndRequestLocationPermission(onRequestPermissionsResult: OnRequestPermissionsResult): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissionForLocation()) {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                onRequestPermissionsResult.onPermissionsGranted()
                false
            } else {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                requestPermissionLocation()
                true
            }
        } else {
            this.onRequestPermissionsResult = onRequestPermissionsResult
            onRequestPermissionsResult.onPermissionsGranted()
            true
        }
    }

    fun checkAndRequestStoragePermission(onRequestPermissionsResult: OnRequestPermissionsResult): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissionStorage()) {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                onRequestPermissionsResult.onPermissionsGranted()
                false
            } else {
                this.onRequestPermissionsResult = onRequestPermissionsResult
                requestPermissionStorage()
                true
            }
        } else {
            this.onRequestPermissionsResult = onRequestPermissionsResult
            onRequestPermissionsResult.onPermissionsGranted()
            true
        }
    }

    fun checkPermissionForLocation(): Boolean {
        var firstPermissionResult: Int? = null
        var secondPermissionResult: Int? = null
        if (context is Activity) {
            firstPermissionResult = checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            secondPermissionResult = checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        } else if (context is Service) {
            firstPermissionResult = checkSelfPermission(context as Service, Manifest.permission.ACCESS_COARSE_LOCATION)
            secondPermissionResult = checkSelfPermission(context as Service, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                secondPermissionResult == PackageManager.PERMISSION_GRANTED
    }
    private fun checkPermissionPhoneCall(): Boolean {
        val firstPermissionResult = checkSelfPermission(context, Manifest.permission.CALL_PHONE)
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED
    }
    fun checkPermissionRecordAudio(): Boolean {
        val firstPermissionResult =
            checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED
    }
    fun checkPermissionCameraStorage(): Boolean {
        val firstPermissionResult =
            checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val secondPermissionResult =
            checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val thirdPermissionResult =
            checkSelfPermission(context, Manifest.permission.CAMERA)
        return firstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                secondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                thirdPermissionResult == PackageManager.PERMISSION_GRANTED
    }
    fun checkPermissionStorage(): Boolean {
        val firstPermissionResult =
            checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val secondPermissionResult =
            checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val thirdPermissionResult =
            checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)

        return firstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                secondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                thirdPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionPhoneCall() {
        requestPermissions(
            context ,
            arrayOf(
                Manifest.permission.CALL_PHONE
            ),
            REQUEST_CODE_PHONE_CALL
        )
    }

    private fun requestPermissionRecordAudio() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            ),
            REQUEST_CODE_RECORD_AUDIO
        )
    }

    private fun requestPermissionCameraStorgae() {
        requestPermissions(
            context,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_CAMERA_PERMISSION
        )
    }

    private fun requestPermissionLocation() {

        if (context is Activity) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        } else {
//            ActivityCompat.requestPermissions(
//                context,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                REQUEST_CODE_LOCATION_PERMISSION
//            )
        }
    }

    private fun requestPermissionStorage() {
        requestPermissions(
            context,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_STORAGE_PERMISSION)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (checkPermissionCameraStorage()) {
                onRequestPermissionsResult?.onPermissionsGranted()
            } else {
                if (shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.CAMERA
                    ) || shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) || shouldShowRequestPermissionRationale(
                        context ,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    onRequestPermissionsResult?.onPermissionsDenied()
                } else {
                    displayNeverAskAgainDialog(
                        context,
                        "1We need to enable Storage & Camera permissions for performing necessary task. Please permit the permission through Settings screen. Select PermissionHelper -> Enable permission"
                    )
                }
            }
        } else if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (checkPermissionStorage()) {
                onRequestPermissionsResult?.onPermissionsGranted()
            } else {
                if (shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) || shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    || shouldShowRequestPermissionRationale(
                        context,Manifest.permission.RECORD_AUDIO
                    )

                ) {
                    onRequestPermissionsResult?.onPermissionsDenied()
                } else {
                    displayNeverAskAgainDialog(
                        context,
                        "We need to enable Storage permission for performing necessary task. Please permit the permission through Settings screen. Select PermissionHelper -> Enable permission"
                    )
                }
            }
        } else if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (checkPermissionForLocation()) {
                onRequestPermissionsResult?.onPermissionsGranted()
            } else {
                if (shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) || shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    onRequestPermissionsResult?.onPermissionsDenied()
                } else {
                    displayNeverAskAgainDialog(
                        context,
                        "We need to enable Location permission for performing necessary task. Please permit the permission through Settings screen. Select PermissionHelper -> Enable permission"
                    )
                }
            }
        } else if (requestCode == REQUEST_CODE_PHONE_CALL) {
            if (checkPermissionPhoneCall()) {
                onRequestPermissionsResult?.onPermissionsGranted()
            } else {
                if (shouldShowRequestPermissionRationale(context, Manifest.permission.CALL_PHONE)) {
                    onRequestPermissionsResult?.onPermissionsDenied()
                } else {
                    displayNeverAskAgainDialog(context, "We need to enable call phone permission for performing necessary task. Please permit the permission through Settings screen. Select PermissionHelper -> Enable permission")
                }
            }
        } else if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            if (checkPermissionRecordAudio()) {
                onRequestPermissionsResult?.onPermissionsGranted()
            } else {
                if (shouldShowRequestPermissionRationale(context,Manifest.permission.RECORD_AUDIO)) {
                    onRequestPermissionsResult?.onPermissionsDenied()
                } else {
                    displayNeverAskAgainDialog(
                        context,
                        "We need to enable Audio permission for performing necessary task. Please permit the permission through Settings screen. Select PermissionHelper -> Enable permission"
                    )
                }
            }
        }
    }

    private fun displayNeverAskAgainDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton("Permit Manually") { dialog, which ->
            dialog.dismiss()
            val intent = Intent()
            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.getPackageName(), null)
            intent.data = uri
            context.startActivity(intent)
        }
        builder.setNegativeButton("Cancel", null)
        val alertDialog = builder.create()
        alertDialog.show()
        // Get the alert dialog buttons reference
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#ec2028"))
        negativeButton.setTextColor(Color.parseColor("#ec2028"))
    }

    interface OnRequestPermissionsResult {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
    }

}