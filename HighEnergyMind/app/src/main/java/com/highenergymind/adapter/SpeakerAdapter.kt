package com.highenergymind.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.AudioFile
import com.highenergymind.databinding.ItemsSpeakersLayoutBinding
import com.highenergymind.utils.glideImage

class SpeakerAdapter(
    var isSelected: Int, var isClickAble: Boolean = true
) :
    ListAdapter<AudioFile, SpeakerAdapter.ViewHolder>(object : DiffUtil.ItemCallback<AudioFile>() {
        override fun areItemsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean {
            return oldItem == newItem
        }
    }) {


    var callBack: ((index: Int) -> Unit)? = null

    inner class ViewHolder(val binding: ItemsSpeakersLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsSpeakersLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder) {
            binding.titleIV.text = item.speakerName
            binding.speakrImage.glideImage(item.speakerImg)
            binding.contaibnerCL.setOnClickListener {
                if (isClickAble && holder.absoluteAdapterPosition != isSelected) {
                    isSelected = holder.absoluteAdapterPosition
                    callBack?.invoke(isSelected)
                    notifyDataSetChanged()
                }
            }

            if (isSelected == position) {
                val selectedColor =
                    ContextCompat.getColorStateList(binding.root.context, R.color.bg_color_1)
                binding.titleIV.setTextColor(selectedColor)
                binding.titleIV.typeface =
                    binding.root.context.resources.getFont(R.font.brandontext_bold)
                binding.speakrImage.borderColor =
                    ContextCompat.getColor(binding.root.context, R.color.content_color)
                binding.speakrImage.borderWidth = 5

            } else {
                val selectedColor =
                    ContextCompat.getColorStateList(binding.root.context, R.color.content_color)
                binding.titleIV.setTextColor(selectedColor)
                binding.titleIV.typeface =
                    binding.root.context.resources.getFont(R.font.brandontext_regular)

                binding.speakrImage.borderColor = R.color.white
                binding.speakrImage.borderWidth = 0

            }

        }

    }

}