package com.in2bliss.ui.activity.home.searchTODOREMOVE

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.HomeData
import com.in2bliss.databinding.ItemSearchFilterResultBinding

class SearchFilterResultAdapter :
    ListAdapter<HomeData.MusicDescription, SearchFilterResultAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<HomeData.MusicDescription>() {
            override fun areItemsTheSame(
                oldItem: HomeData.MusicDescription,
                newItem: HomeData.MusicDescription
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: HomeData.MusicDescription,
                newItem: HomeData.MusicDescription
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    class ViewHolder(val view: ItemSearchFilterResultBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchFilterResultBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            tvDescription.text = model.title

            affirmationImage.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    model.image ?: 1
                )
            )
            val imageFav = if (model.isFav) R.drawable.ic_blue_like else R.drawable.ic_blue_unlike

            ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    imageFav
                )
            )
        }
    }
}