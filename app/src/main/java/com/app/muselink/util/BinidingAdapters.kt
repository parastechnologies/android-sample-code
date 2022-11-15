package com.app.muselink.util

import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.app.muselink.R

@BindingAdapter("android:loadImage")
fun loadImage(imageView: ImageView, resource: String?) {
    if (resource.isNullOrEmpty().not()) {
        imageView.loadProfileImage(resource)
    }else{
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context,R.drawable.boyset))
    }
}

fun ImageView.loadImageUser(url: String?) {
    if (url.isNullOrEmpty().not() && (url!!.contains("http") || url.contains("https"))) {
        Glide.with(this)
            .load(url)
//            .override(1600, 1600)
            .placeholder(R.drawable.boyset)
            .dontAnimate()
            .into(this)
    } else if(url.isNullOrEmpty().not()){
        Glide.with(this)
            .load(SyncConstants.BASE_URL_IMAGE + url)
//            .override(1600, 1600)
            .placeholder(R.drawable.boyset)
            .dontAnimate()
            .into(this)
    }else{
        this.setImageDrawable(ContextCompat.getDrawable(this.context,R.drawable.boyset))
    }

}

fun ImageView.loadProfileImage(url: String?) {
    if (url!!.contains("http") || url.contains("https")) {
        Glide.with(this)
            .load(url)
//            .override(1600, 1600)
            .placeholder(R.drawable.boyset)
            .dontAnimate()
            .into(this)
    } else if(url.isNullOrEmpty()){
        this.setImageDrawable(ContextCompat.getDrawable(this.context,R.drawable.boyset))
    }else{
        setImageViewResource(this,url)
    }

}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: String?) {
    if (resource.isNullOrEmpty().not()) {
        imageView.setImageURI(Uri.parse(resource))
    }
}