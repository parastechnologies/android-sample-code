package com.in2bliss.ui.activity.home.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.GettingStarted
import com.in2bliss.databinding.ItemGettingStartedBinding

class HomeGettingStartedAdapter : ListAdapter<GettingStarted, HomeGettingStartedAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GettingStarted>() {
        override fun areItemsTheSame(
            oldItem: GettingStarted,
            newItem: GettingStarted
        ): Boolean {
            return oldItem.image == newItem.image
        }

        override fun areContentsTheSame(
            oldItem: GettingStarted,
            newItem: GettingStarted
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((position: Int) -> Unit)? = null

    class ViewHolder(val view: ItemGettingStartedBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ItemGettingStartedBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            ivIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    getItem(holder.absoluteAdapterPosition).image
                )
            )
            tvName.text = getItem(holder.absoluteAdapterPosition).name

            cvContainer.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition)
            }
        }
    }
}