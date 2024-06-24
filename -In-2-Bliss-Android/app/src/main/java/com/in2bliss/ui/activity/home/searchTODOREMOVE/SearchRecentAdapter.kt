package com.in2bliss.ui.activity.home.searchTODOREMOVE

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.databinding.ItemRecentSearchBinding

class SearchRecentAdapter : ListAdapter<String, SearchRecentAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
) {

    var deleteListener: ((position: Int) -> Unit)? = null

    class ViewHolder(val view: ItemRecentSearchBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            tvSearch.text = getItem(holder.absoluteAdapterPosition)
            delete.setOnClickListener {
                deleteListener?.invoke(holder.absoluteAdapterPosition)
            }
        }
    }

}