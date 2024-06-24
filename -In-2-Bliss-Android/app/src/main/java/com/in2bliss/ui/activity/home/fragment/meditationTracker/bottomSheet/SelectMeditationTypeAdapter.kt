package com.in2bliss.ui.activity.home.fragment.meditationTracker.bottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.databinding.ItemSelectTypeBinding

class SelectMeditationTypeAdapter : ListAdapter<String, SelectMeditationTypeAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
) {
    var callBack: ((Int) -> Unit)? = null
    private var selectedPosition = -1
    private var previousSelectedPosition = -1

    class ViewHolder(val view: ItemSelectTypeBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSelectTypeBinding.inflate(LayoutInflater.from(parent.context)))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.cbCheckBox.text = getItem(position)
        holder.view.cbCheckBox.setOnClickListener {
            selectedPosition = holder.absoluteAdapterPosition
            notifyItemChanged(selectedPosition)
            notifyItemChanged(previousSelectedPosition)
            previousSelectedPosition = selectedPosition
            callBack?.invoke(position)
        }
        holder.view.cbCheckBox.isChecked = selectedPosition == position
    }
}