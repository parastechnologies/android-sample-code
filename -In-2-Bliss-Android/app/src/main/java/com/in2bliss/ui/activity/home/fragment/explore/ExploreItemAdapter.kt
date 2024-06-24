package com.in2bliss.ui.activity.home.fragment.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.explore.ExploreSelect
import com.in2bliss.databinding.ItemListExploreBinding

class ExploreItemAdapter :
    ListAdapter<ExploreSelect, ExploreItemAdapter.ViewHolder>(

        object : DiffUtil.ItemCallback<ExploreSelect>() {
            override fun areItemsTheSame(oldItem: ExploreSelect, newItem: ExploreSelect): Boolean {
                return oldItem.affirmationType == newItem.affirmationType
            }

            override fun areContentsTheSame(
                oldItem: ExploreSelect,
                newItem: ExploreSelect
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    private var selectedPosition = 0
    private var previousSelectedPosition = 0
    var onClick: ((data: ExploreSelect) -> Unit)? = null

    class ViewHolder(val view: ItemListExploreBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListExploreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {

            val model = getItem(holder.absoluteAdapterPosition)
            tvText.setText(model.title)

            root.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousSelectedPosition)
                previousSelectedPosition = selectedPosition
                onClick?.invoke(model)
            }

            val backgroundColor =
                if (selectedPosition == position) R.color.blue_1c92f1 else R.color.white
            val textColor = if (selectedPosition == position) R.color.white else R.color.blue_1c92f1

            holder.view.mcvCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.view.root.context,
                    backgroundColor
                )
            )
            holder.view.tvText.setTextColor(
                ContextCompat.getColor(
                    holder.view.root.context,
                    textColor
                )
            )
        }
    }
}