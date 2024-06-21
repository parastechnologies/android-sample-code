package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.CheckModel
import com.highenergymind.databinding.ItemRadioButtonsBinding

class RadioButtonAdapter :
    ListAdapter<CheckModel, RadioButtonAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<CheckModel>() {
        override fun areItemsTheSame(oldItem: CheckModel, newItem: CheckModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CheckModel, newItem: CheckModel): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class ViewHolder(val binding: ItemRadioButtonsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    item.isChecked = !item.isChecked
                    notifyItemChanged(absoluteAdapterPosition)
                }
                rbButton.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    item.isChecked = !item.isChecked
                    notifyItemChanged(absoluteAdapterPosition)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRadioButtonsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvName.text = item.name
            rbButton.isChecked = item.isChecked
        }
    }
}