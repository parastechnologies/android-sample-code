package com.in2bliss.ui.activity.home.commonAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.databinding.ItemSearchKeywordsBinding

class RecentSearchAdapter : ListAdapter<MusicList.SearchHistory, RecentSearchAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<MusicList.SearchHistory>() {
        override fun areItemsTheSame(
            oldItem: MusicList.SearchHistory,
            newItem: MusicList.SearchHistory
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MusicList.SearchHistory,
            newItem: MusicList.SearchHistory
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((search: String) -> Unit)? = null

    class ViewHolder(val view: ItemSearchKeywordsBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ItemSearchKeywordsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tvSearch.text = getItem(holder.absoluteAdapterPosition).text
        holder.view.cvContainer.setOnClickListener {
            listener?.invoke(getItem(holder.absoluteAdapterPosition).text ?: "")
        }
    }
}