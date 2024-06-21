package com.highenergymind.view.sheet.colortheme

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highenergymind.data.ThemeDataModel
import com.highenergymind.databinding.ItemsColorthemeLayoutBinding

class ColorThemeAdapter (
    val dataList : ArrayList<ThemeDataModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<ColorThemeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemsColorthemeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsColorthemeLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            if (position==1){
                binding.premiuemIV.visibility = View.VISIBLE
            }
            Glide.with(context)
                .load(dataList[position].themeImage)
                .into(binding.imageIV)

        }

    }

}