package com.app.muselink.ui.activities.profile.camera

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jackbcustomer.sync.viewmodel.ImageSelectionHelper
import com.app.muselink.R
import com.app.muselink.commonutils.imageselection.UploaderImageDetail
import com.app.muselink.constants.AppConstants
import com.app.muselink.databinding.ActivityCameraBinding
import com.app.muselink.helpers.PermissionHelper
import com.app.muselink.ui.activities.profile.CameraAdapter
import com.app.muselink.ui.base.activity.BaseViewModelActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_camera.*
import soup.neumorphism.ShapeType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class CameraActivity : BaseViewModelActivity<ActivityCameraBinding,CamerActivityViewModel>() {

    private lateinit var recyclerviewImages: RecyclerView
    private lateinit var cameraAdapter: CameraAdapter
    private lateinit var userImage: ImageView
    private var selectedImagePath = ""
    var permissionHelper :PermissionHelper? =null
    var imageSelectionHelper: ImageSelectionHelper? = null


    override fun getLayout(): Int {
        return R.layout.activity_camera
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PermissionHelper(this)
        binding?.vm = viewModel
        viewModel?.lifeCycle = this
        viewModel?.biniding = binding
        viewModel?.setupObserversUploadImage()

        if(permissionHelper!!.checkPermissionCameraStorage()){
            initView()
            setAdapter()
        }else{
            permissionHelper?.checkAndRequestCameraPermission(requestPermissionsResult)
        }


    }

    val requestPermissionsResult = object : PermissionHelper.OnRequestPermissionsResult{

        override fun onPermissionsGranted() {
            initView()
            setAdapter()
        }

        override fun onPermissionsDenied() {
            permissionHelper?.checkAndRequestCameraPermission(this)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper?.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }


    private fun returnFile() : File?{
        var destinationRoot : File? =null
        var file : File? =null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            destinationRoot = File(filesDir, "Muselink/Images")
        } else {
            destinationRoot = File(
                Environment.getExternalStorageDirectory().path,
                "Muselink/Images"
            )
        }
        if (destinationRoot.exists() == false) {
            destinationRoot.mkdirs()
            val currentTime = System.currentTimeMillis().toString()
            file = File((destinationRoot.toString() + "/" + currentTime + ".jpg").toString())
        } else {
            val currentTime = System.currentTimeMillis().toString()
            file =
                File((destinationRoot.toString() + "/" + currentTime + ".jpg").toString())
        }
        return file
    }

    private fun initView() {

        val onImageSelected = object : ImageSelectionHelper.OnImageSelected {
            override fun imageSelected(uploaderImageDetail: UploaderImageDetail?) {
                selectedImagePath = uploaderImageDetail?.file!!.path.toString()
                crop_view.extensions()
                    .load(Uri.fromFile(File(selectedImagePath)))
            }
        }
        imageSelectionHelper = ImageSelectionHelper(onImageSelected)

        cameraAdapter = CameraAdapter()
        recyclerviewImages = findViewById(R.id.recyclerviewImages)
        userImage = findViewById(R.id.userImage)

        tvSave.setOnClickListener {
            try {
                val file = returnFile()
                val bitmap = crop_view.crop()
                try {
                    FileOutputStream(file).use({ out ->
                        bitmap?.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            out
                        ) // bmp is your Bitmap instance
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                viewModel?.onclickSave(file!!.path.toString())
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        tvCancel.setOnClickListener {
            onBackPressed()
        }

        camera.setOnClickListener {
            viewGallery.visibility = View.GONE
            viewCamera.visibility = View.VISIBLE
            camera.setShapeType(ShapeType.BASIN)
            gallery.setShapeType(ShapeType.FLAT)
            imageSelectionHelper?.chooseFromCamera(this, false, null)
        }

        gallery.setOnClickListener {
            viewGallery.visibility = View.VISIBLE
            viewCamera.visibility = View.GONE
            camera.setShapeType(ShapeType.FLAT)
            gallery.setShapeType(ShapeType.BASIN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  AppConstants.PROFILE_IMAGE_REQUEST || requestCode == AppConstants.CAMERA_REQUEST || requestCode == AppConstants.SELECTED_IMG_CROP){
            imageSelectionHelper?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun setAdapter() {
        recyclerviewImages.apply {
            layoutManager = GridLayoutManager(this@CameraActivity, 4)
            adapter = cameraAdapter
        }
        val imagesList = getAllShownImagesPath(this)
        if (imagesList.size > 0) {
            cameraAdapter.setData(imagesList)
//            userImage.setImageURI(Uri.fromFile(File(imagesList[0])))
            selectedImagePath = imagesList[0]
            crop_view.extensions()
                .load(Uri.fromFile(File(imagesList[0])))

        }
        cameraAdapter.onItemSelected(object :
            CameraAdapter.OnItemSelected {
            override fun onSelect(path: String) {
                selectedImagePath = path
                crop_view.extensions()
                    .load(File(path))
//                userImage.setImageURI(Uri.fromFile(File(path)))
            }
        })
    }

    private fun getAllShownImagesPath(activity: Activity): ArrayList<String> {
        val uri: Uri
        var cursor: Cursor? = null
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        cursor = activity.getContentResolver().query(
            uri, projection, null,
            null, null
        )
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }

    override fun getViewModelClass(): Class<CamerActivityViewModel> {
        return CamerActivityViewModel::class.java
    }


}