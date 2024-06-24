package com.in2bliss.ui.activity.home.profileManagement.manageSubscription

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.subscription.SubscriptionModel
import com.in2bliss.databinding.ItemSubscriptionBinding

class SubscriptionAdapter :
    ListAdapter<SubscriptionModel, SubscriptionAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<SubscriptionModel>() {

        override fun areItemsTheSame(
            oldItem: SubscriptionModel,
            newItem: SubscriptionModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SubscriptionModel,
            newItem: SubscriptionModel
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    var listener: ((productId: String, position: Int) -> Unit)? = null
    var countryRupees:String?=null
    inner class ViewHolder(val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSubscriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    var isSelected = -1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvMonth.text = model?.name
            tvTitle.text = model?.title
            tvAmount.text = model?.price.toString()
            tvDollar.text=model?.countryDollar

            root.setOnClickListener {
                listener?.invoke(
                    model.productId ?: "", holder.absoluteAdapterPosition
                )
            }
            val backgroundColor = if (model.selected) R.color.prime_purple_5F46F4 else R.color.white
            val textColor = if (model.selected) R.color.white else R.color.prime_purple_5F46F4

            cvContainer.setCardBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    backgroundColor
                )
            )
            tvAmount.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )
            tvDollar.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )
            tvTitle.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )

            tvMonth.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )
        }
    }
}