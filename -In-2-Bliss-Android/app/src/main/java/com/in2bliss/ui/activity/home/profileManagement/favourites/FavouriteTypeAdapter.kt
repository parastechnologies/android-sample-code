package com.in2bliss.ui.activity.home.profileManagement.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.FavoritesType
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.ItemHomeMusicBinding
import com.in2bliss.utils.extension.popMenu

class FavouriteTypeAdapter :
    ListAdapter<FavoritesType, FavouriteTypeAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<FavoritesType>() {

        override fun areItemsTheSame(
            oldItem: FavoritesType,
            newItem: FavoritesType
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FavoritesType,
            newItem: FavoritesType
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    var onClick: ((ApiConstant.ExploreType, String) -> Unit)? = null

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
            tvText.text = model.title
            root.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousSelectedPosition)
                previousSelectedPosition = selectedPosition
                when (position) {
                    3 -> {
                        onClick?.invoke(ApiConstant.ExploreType.WISDOM, "0")
                    }
                    2 -> {
                        onClick?.invoke(ApiConstant.ExploreType.MUSIC, "0")
                    }
                    else -> {
                        root.context.popMenu(holder.binding.mcvCard, model.array) {
                            onClick?.invoke(model.type, it.toString())
                        }
                    }
                }
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
