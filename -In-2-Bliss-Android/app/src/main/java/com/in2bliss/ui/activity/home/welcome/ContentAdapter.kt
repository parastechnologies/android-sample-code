package com.in2bliss.ui.activity.home.welcome

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.content.Content
import com.in2bliss.databinding.ItemContentBinding
import com.in2bliss.utils.extension.visibility

class ContentAdapter : ListAdapter<Content, ContentAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Content>() {
        override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem == newItem
        }

    }
) {
    class ViewHolder(val view: ItemContentBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            if (model.screenType == 1 || model.screenType==2) {
                tvDescription.gravity = Gravity.CENTER
            }

            tvTitle.visibility(isVisible = model.title != "")
            tvTitle.text = model.title
            tvDescription.text = model.description
            model.image?.let { ivImage.setImageResource(it) }
        }
    }
}