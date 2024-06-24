package com.in2bliss.ui.activity.home.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.DaysList
import com.in2bliss.databinding.ItemDayBinding


class ReminderAdapter :
    ListAdapter<DaysList, ReminderAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<DaysList>() {

            override fun areItemsTheSame(
                oldItem: DaysList,
                newItem: DaysList
            ): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(
                oldItem: DaysList,
                newItem: DaysList
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    class ViewHolder(val view: ItemDayBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDayBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {

            val model = getItem(position)
            tvDay.text = model.day

            var backGroundColor = R.color.white
            var strokeColor = R.color.blue_d9e9fd
            var textColor = R.color.dark_purple_12046A

            clContainer.setOnClickListener {
                model.isSelected = model.isSelected.not()
                notifyItemChanged(holder.absoluteAdapterPosition)
            }

            if (model.isSelected) {
                backGroundColor = R.color.prime_blue_418FF6
                strokeColor = R.color.prime_blue_418FF6
                textColor = R.color.white
            }

            clContainer.setCardBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    backGroundColor
                )
            )
            clContainer.strokeColor = ContextCompat.getColor(
                root.context,
                strokeColor
            )

            tvDay.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )
        }
    }
}
