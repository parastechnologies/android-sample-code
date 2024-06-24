package com.in2bliss.ui.activity.home.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.databinding.ItemHomeMusicBinding

class MusicCategoryAdapter :
    ListAdapter<MusicCategories.Data, MusicCategoryAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<MusicCategories.Data>() {

        override fun areItemsTheSame(
            oldItem: MusicCategories.Data,
            newItem: MusicCategories.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MusicCategories.Data,
            newItem: MusicCategories.Data
        ): Boolean {
            return oldItem == newItem
        }
    }) {


    var onClick: ((Int) -> Unit)? = null

    inner class ViewHolder(val binding: ItemHomeMusicBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemHomeMusicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    private var selectedPosition = 0
    private var previousSelectedPosition = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvText.text = model.name


            root.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousSelectedPosition)
                previousSelectedPosition = selectedPosition
                model.id?.let { it1 -> onClick?.invoke(it1) }
            }

            val backgroundColor =
                if (selectedPosition == position) R.color.color_E0EDFD else R.color.color_E0EDFD
            val textColor =
                if (selectedPosition == position) R.color.blue_1c92f1 else R.color.dark_purple_12046A
            val strokeColor =
                if (selectedPosition == position) R.color.blue_1c92f1 else R.color.color_E0EDFD

            holder.binding.mcvCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    backgroundColor
                )
            )
            holder.binding.tvText.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    textColor
                )
            )
            holder.binding.mcvCard.strokeColor =
                ContextCompat.getColor(holder.binding.root.context, strokeColor)
        }
    }
}
