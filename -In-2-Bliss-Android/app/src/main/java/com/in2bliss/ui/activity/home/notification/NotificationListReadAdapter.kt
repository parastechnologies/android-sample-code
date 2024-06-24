package com.in2bliss.ui.activity.home.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.NotificationListResponse
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.databinding.NotificationCardBinding
import com.in2bliss.utils.extension.setTimeFormat

class NotificationListReadAdapter : PagingDataAdapter<NotificationListResponse.Read, NotificationListReadAdapter.ViewHolder>(
    object :
        DiffUtil.ItemCallback<NotificationListResponse.Read>() {

        override fun areItemsTheSame(
            oldItem: NotificationListResponse.Read,
            newItem: NotificationListResponse.Read
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NotificationListResponse.Read,
            newItem: NotificationListResponse.Read
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    var favourite: ((position: Int, id: Int, isFav: Boolean) -> Unit)? = null
    var listener: ((data: SeeAllResponse.Data, position: Int) -> Unit)? = null
    var type: Int? = null
    var editListener: ((data: SeeAllResponse.Data) -> Unit)? = null

    inner class ViewHolder(val view: NotificationCardBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = NotificationCardBinding.inflate(
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

        }
    }
}