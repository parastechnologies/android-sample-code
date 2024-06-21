package com.highenergymind.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.highenergymind.R
import com.highenergymind.data.MusicModel
import com.highenergymind.databinding.ItemsSpeakersLayoutBinding

class SheetSettingsAdapter(
    val subCategoryList: ArrayList<MusicModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<SheetSettingsAdapter.ViewHolder>() {

    var isSelected = -1

    inner class ViewHolder(val binding: ItemsSpeakersLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsSpeakersLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            binding.titleIV.text = subCategoryList[position].musicName

            Glide.with(context)
                .load(subCategoryList[position].musicImage)
                .into(binding.speakrImage)

            binding.contaibnerCL.setOnClickListener {
                isSelected = position
                notifyDataSetChanged()

            }

            if (isSelected == position) {

                val selectedColor = ContextCompat.getColorStateList(context, R.color.bg_color_1)
                binding.titleIV.setTextColor(selectedColor)
                binding.titleIV.setTextColor(Color.parseColor("#1A6785"))
                binding.titleIV.setTypeface(binding.titleIV.typeface, Typeface.BOLD)
                binding.speakrImage.borderColor = ContextCompat.getColor(context, R.color.content_color)
                binding.speakrImage.borderWidth = 5

            } else {

                val selectedColor = ContextCompat.getColorStateList(context, R.color.content_color)
                binding.titleIV.setTextColor(selectedColor)
                binding.speakrImage.borderColor = ContextCompat.getColor(context, R.color.white)
                binding.speakrImage.borderWidth = 0

            }

        }


    }
}

