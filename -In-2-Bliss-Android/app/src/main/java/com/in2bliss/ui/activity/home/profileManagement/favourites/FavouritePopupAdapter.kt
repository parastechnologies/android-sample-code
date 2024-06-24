package com.in2bliss.ui.activity.home.profileManagement.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.databinding.ItemHomeMusicBinding
import com.in2bliss.databinding.ItemPopupMenuBinding
import com.in2bliss.utils.extension.popMenu

class FavouritePopupAdapter :
    ListAdapter<String, FavouritePopupAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }) {


    var onClick: ((Int) -> Unit)? = null

    inner class ViewHolder(val binding: ItemPopupMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPopupMenuBinding.inflate(
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
            tvText.text = model

            root.setOnClickListener {
                onClick?.invoke(position)

            }

        }
    }
}
