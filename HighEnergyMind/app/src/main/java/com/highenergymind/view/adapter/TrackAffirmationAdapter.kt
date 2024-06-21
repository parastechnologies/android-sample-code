package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.AffirmationData
import com.highenergymind.databinding.ItemTrackAffirmationBinding
import com.highenergymind.di.ApplicationClass

class TrackAffirmationAdapter :
    ListAdapter<AffirmationData, TrackAffirmationAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<AffirmationData>() {
        override fun areItemsTheSame(oldItem: AffirmationData, newItem: AffirmationData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AffirmationData, newItem: AffirmationData): Boolean {
            return oldItem == newItem
        }
    }) {
    class ViewHolder(val binding: ItemTrackAffirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTrackAffirmationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvTitle.text =if (ApplicationClass.isEnglishSelected) item.affirmationTextEnglish else item.affirmationTextGerman
        }
    }
}