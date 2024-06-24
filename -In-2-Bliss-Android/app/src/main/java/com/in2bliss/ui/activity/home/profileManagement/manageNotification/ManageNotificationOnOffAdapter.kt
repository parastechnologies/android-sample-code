package com.in2bliss.ui.activity.home.profileManagement.manageNotification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.NotificationList
import com.in2bliss.databinding.ItemOnOffBinding

class ManageNotificationOnOffAdapter :
    ListAdapter<NotificationList, ManageNotificationOnOffAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<NotificationList>() {
            override fun areItemsTheSame(
                oldItem: NotificationList,
                newItem: NotificationList
            ): Boolean {
                return oldItem.title == newItem.title
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: NotificationList,
                newItem: NotificationList
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    class ViewHolder(val view: ItemOnOffBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemOnOffBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.title
            tvDescription.text = model.description

        }
    }
}