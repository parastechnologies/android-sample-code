package com.highenergymind.utils

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("upLoadImage")
fun upLoadImage(imageView: ImageView, resource: String) {
    val imageBitmap = resource as Bitmap

    imageView.setImageBitmap(imageBitmap)

}