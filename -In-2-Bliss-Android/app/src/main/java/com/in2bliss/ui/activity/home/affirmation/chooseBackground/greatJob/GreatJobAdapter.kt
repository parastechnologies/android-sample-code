package com.in2bliss.ui.activity.home.affirmation.chooseBackground.greatJob

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.databinding.ItemGreatJobBinding

class GreatJobAdapter : ListAdapter<Int, GreatJobAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
) {
    class ViewHolder(val view: ItemGreatJobBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGreatJobBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            tvDescription.setText(getItem(holder.absoluteAdapterPosition))
            tvCount.text = holder.absoluteAdapterPosition.plus(1).toString()
        }
    }
}