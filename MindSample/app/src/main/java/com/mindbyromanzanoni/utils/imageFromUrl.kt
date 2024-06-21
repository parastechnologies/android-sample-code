package com.mindbyromanzanoni.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mindbyromanzanoni.R

fun ImageView?.setImageFromUrl(imageUrl: String?, progressBar: ProgressBar?) {
     progressBar?.visible()
    Glide
        .with(this?.context!!)
        .load(imageUrl)
        .centerCrop()
        .dontAnimate()
        .placeholder(R.drawable.placeholder_mind)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        }).into(this)
}

fun ImageView?.setImageFromUrlTest(imageUrl: String?, progressBar: ProgressBar?) {
    progressBar?.visible()
    Glide
        .with(this?.context!!)
        .load(imageUrl)
        .override(300,300)
        .centerCrop()
        .dontAnimate()
        .placeholder(R.drawable.placeholder_mind)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        }).into(this)
}


fun ImageView?.setImageFromUrl(placeHolder: Int, imageUrl: String?, progressBar: ProgressBar?) {
    val image = imageUrl?.replace(" ", "%20")
    progressBar?.visible()
    Glide
        .with(this?.context!!)
        .load(imageUrl)
        .centerCrop()
        .dontAnimate()
        .placeholder(placeHolder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        }).into(this)
}
fun ImageView?.setImageFromUrl(placeHolder: Int, imageUrl: String?) {
    Glide
        .with(this?.context!!)
        .load(imageUrl)
        .fitCenter()
         .dontAnimate()
        .placeholder(placeHolder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }).into(this)
}
