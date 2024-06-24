package com.in2bliss.ui.activity.home.profileManagement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.MainProfileList
import com.in2bliss.databinding.MainProfileCardBinding
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible

class MainProfileAdapter : ListAdapter<MainProfileList, MainProfileAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<MainProfileList>() {
        override fun areItemsTheSame(oldItem: MainProfileList, newItem: MainProfileList): Boolean {
            return oldItem.title == newItem.title
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: MainProfileList,
            newItem: MainProfileList
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((position: Int) -> Unit)? = null

    class ViewHolder(val view: MainProfileCardBinding) : RecyclerView.ViewHolder(view.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MainProfileCardBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            val isVisible = model.title == 0

            removeView.visibility(isVisible.not())
            viewLine.visibility(isVisible)

            tvTitle.text =
                if (model.title == 0) "" else holder.view.root.context.getString(model.title)
            ivIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    model.icon
                )
            )

            clProfileParent.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition)
            }
            when(position){
                2->{
                    view1.visible()
                }
                6->{
                    view1.visible()
                }
            }
        }
    }
}