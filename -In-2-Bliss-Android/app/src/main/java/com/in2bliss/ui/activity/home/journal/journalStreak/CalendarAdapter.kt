package com.in2bliss.ui.activity.home.journal.journalStreak

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.CalendarDate
import com.in2bliss.databinding.ItemCalendarBinding
import com.in2bliss.utils.extension.visibility

class CalendarAdapter : ListAdapter<CalendarDate, CalendarAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<CalendarDate>() {
        override fun areItemsTheSame(oldItem: CalendarDate, newItem: CalendarDate): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDate, newItem: CalendarDate): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((position: Int, selectedDate: String, data: String?) -> Unit)? = null
    var selectedPosition = -1
    private var previousPosition = selectedPosition

    class ViewHolder(val view: ItemCalendarBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCalendarBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            firstRow.visibility(holder.absoluteAdapterPosition < 7)

            tvDate.text = model.date.toString()
            tvDayOfWeek.text = model.weekOfDay
            cvDate.visibility(model.isEmpty.not())
            cvEmpty.visibility(model.isEmpty)

            cvDate.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousPosition)
                previousPosition = selectedPosition
                listener?.invoke(
                    holder.absoluteAdapterPosition,
                    "${model.year}-${
                        String.format(
                            "%02d",
                            model.month
                        )
                    }-${String.format("%02d", model.date)}",
                    model.data
                )
            }

            var backgroundColor = R.color.white
            var isEvent = R.color.grey_E0E0E0
            var isEdit = R.color.grey_E0E0E0
            var textColor = R.color.inactive_purple_7168A6

            if (selectedPosition == holder.absoluteAdapterPosition) {
                backgroundColor = R.color.inactive_purple_277168A6
                isEvent = R.color.prime_purple_5F46F4
                isEdit = R.color.black_333333
                textColor = R.color.prime_purple_5F46F4
            }

            isEvent = if (model.isEvent) R.color.prime_purple_5F46F4 else R.color.grey_E0E0E0

            ivEvent.setColorFilter(
                ContextCompat.getColor(
                    root.context,
                    isEvent
                )
            )
            clContainer.setBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    backgroundColor
                )
            )
            ivEdit.setColorFilter(
                ContextCompat.getColor(
                    root.context,
                    isEdit
                )
            )
            tvDate.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )
        }
    }
}