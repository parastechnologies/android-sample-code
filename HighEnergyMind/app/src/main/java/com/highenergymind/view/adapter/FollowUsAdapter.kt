package com.highenergymind.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.FollowUsData
import com.highenergymind.databinding.ItemFollowUsBinding
import com.highenergymind.utils.glideImage

class FollowUsAdapter :
    ListAdapter<FollowUsData, FollowUsAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<FollowUsData>() {
        override fun areItemsTheSame(oldItem: FollowUsData, newItem: FollowUsData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FollowUsData, newItem: FollowUsData): Boolean {
            return oldItem == newItem
        }
    }) {
    inner class ViewHolder(val binding: ItemFollowUsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    val link =
                        if (item.linkName.contains("http") || item.linkName.contains("https")) item.linkName else "https://${item.linkName}"

                    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                    it.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFollowUsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            ivImage.glideImage(item.linkImg)
            tvTitle.text = item.socialLinkTitle
            tvDesc.text = item.linkName

        }
    }
}