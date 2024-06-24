package com.in2bliss.ui.activity.home.notification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.NotificationList
import com.in2bliss.databinding.NotificationCardBinding

class NotificationAdapter : ListAdapter<NotificationList, NotificationAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<NotificationList>() {
        override fun areItemsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return oldItem.title == newItem.title
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return oldItem == newItem
        }
    }
) {
    class ViewHolder(val view: NotificationCardBinding) : RecyclerView.ViewHolder(view.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NotificationCardBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.title
            ivNotification.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    model.icon
                )
            )
        }
    }
}