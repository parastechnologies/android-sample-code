package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.NotData
import com.highenergymind.databinding.ItemNotificationBinding
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.timeInAgo

class NotificationAdapter :
    ListAdapter<NotData, NotificationAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<NotData>() {
        override fun areItemsTheSame(oldItem: NotData, newItem: NotData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotData, newItem: NotData): Boolean {
            return oldItem == newItem
        }
    }) {
    class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            ivIcon.glideImage(item.categoryImgPath)
            tvTitle.text = item.notificationTitle
            tvDesc.text = item.notificationMsg
            tvAgo.text=item.createdAt.timeInAgo("yyyy-MM-dd HH:mm:ss")
        }
    }
}