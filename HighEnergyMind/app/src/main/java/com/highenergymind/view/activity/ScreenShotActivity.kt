package com.highenergymind.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiConstant
import com.highenergymind.base.BaseActivity
import com.highenergymind.data.Affirmation
import com.highenergymind.databinding.LayoutAffirmationScreenShotBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.AppConstant
import com.highenergymind.utils.fullScreenStatusBar
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


/**
 * Created by Puneet on 03/06/24
 */
@AndroidEntryPoint
class ScreenShotActivity : BaseActivity<LayoutAffirmationScreenShotBinding>() {
    var affirm: Affirmation? = null
    override fun getLayoutRes(): Int {
        return R.layout.layout_affirmation_screen_shot
    }

    override fun initView() {
        fullScreenStatusBar()
        getBundleData()
    }

    private fun getBundleData() {
        binding.apply {

            if (intent.hasExtra(AppConstant.TRACK_DATA)) {
                affirm = Gson().fromJson(
                    intent.getStringExtra(AppConstant.TRACK_DATA),
                    Affirmation::class.java
                )
                setData()
            }
        }
    }

    private fun setData() {
        binding.apply {
            ivBackground.glideImage(affirm?.createdAt)

            tvAffirmationText.text=if (ApplicationClass.isEnglishSelected) affirm?.affirmationTextEnglish else affirm?.affirmationTextGerman
            lifecycleScope.launch {
                showToast(getString(R.string.generating_screen_shot))
                progressDialog(true)
                delay(1000)

                saveBitmapToGallery(createBitmapFromView(binding.rlView))
            }
        }
    }
    private fun createBitmapFromView(view: View): Bitmap {

            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap

    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        // Save the bitmap to a file
        val filename = "${System.currentTimeMillis()}.png"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        // Add the image to the gallery
        MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, filename, null)
        val intent=Intent()
        intent.putExtras(Bundle().also {
            it.putString(ApiConstant.IMG,file.path)
        })
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

}