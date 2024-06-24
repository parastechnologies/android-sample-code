package com.in2bliss.ui.activity.home.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.NotificationListResponse
import com.in2bliss.databinding.ItemUnreadBinding
import com.in2bliss.utils.extension.setTimeFormat

class NotificationListAdapter : PagingDataAdapter<NotificationListResponse.Unread, NotificationListAdapter.ViewHolder>(
    object :
        DiffUtil.ItemCallback<NotificationListResponse.Unread>() {

        override fun areItemsTheSame(
            oldItem: NotificationListResponse.Unread,
            newItem: NotificationListResponse.Unread
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NotificationListResponse.Unread,
            newItem: NotificationListResponse.Unread
        ): Boolean {
            return oldItem == newItem
        }
    }) {


    var listener: ((Int, position: Int) -> Unit)? = null

    inner class ViewHolder(val view: ItemUnreadBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemUnreadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text=model?.title
            tvDescription.text=model?.message
            tvTime.text= model?.created_at?.let { setTimeFormat(it,"yyyy-MM-dd HH:mm:ss") }

            holder.view.root.setOnClickListener {
                model?.id?.let { it1 -> listener?.invoke(it1,position) }
            }
        }
    }
}