package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.CheckModel
import com.highenergymind.databinding.ItemWeekDayBinding

class WeekDayAdapter :
    ListAdapter<CheckModel, WeekDayAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<CheckModel>() {
        override fun areItemsTheSame(oldItem: CheckModel, newItem: CheckModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CheckModel, newItem: CheckModel): Boolean {
            return oldItem == newItem
        }
    }) {
    inner class ViewHolder(val binding: ItemWeekDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    item.isChecked = !item.isChecked
                    notifyItemChanged(absoluteAdapterPosition)

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemWeekDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            val contentColor = ContextCompat.getColorStateList(root.context, R.color.bg_color_1)
            val whiteColor = ContextCompat.getColorStateList(root.context, R.color.white)
            tvText.text = item.name[0].uppercase()
            if (item.isChecked) {
                cvContainer.strokeWidth = 0
                cvContainer.setCardBackgroundColor(contentColor)
                tvText.setTextColor(whiteColor)

            } else {
                cvContainer.strokeWidth =
                    root.context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
                cvContainer.setCardBackgroundColor(whiteColor)
                tvText.setTextColor(contentColor)
            }
        }
    }
}