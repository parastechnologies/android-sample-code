package com.in2bliss.ui.activity.home.fragment.meditationTracker.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.CalendarDate
import com.in2bliss.databinding.ItemHorizontalCalendarBinding
import com.in2bliss.utils.extension.getCurrentDate

class   HorizontalCalendarAdapter : ListAdapter<CalendarDate, HorizontalCalendarAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<CalendarDate>() {
        override fun areItemsTheSame(oldItem: CalendarDate, newItem: CalendarDate): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDate, newItem: CalendarDate): Boolean {
            return oldItem == newItem
        }
    }
) {

    class ViewHolder(val view: ItemHorizontalCalendarBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemHorizontalCalendarBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvDayOfWeek.text = model.weekOfDay
            tvDate.text = model.date.toString()

            var backgroundColor = R.color.white
            var dateColor = R.color.dark_purple_12046A
            var dayOfWeekColor = R.color.inactive_purple_7168A6
            var eventColor = R.color.inactive_purple_277168A6

            val isCurrentDate = getCurrentDate() == "${String.format("%02d", model.year)}-${
                String.format("%02d", model.month)
            }-${String.format("%02d", model.date)}"

            if (isCurrentDate) {
                dateColor = R.color.white
                dayOfWeekColor = R.color.white
                eventColor = R.color.white
                backgroundColor = R.color.prime_purple_5F46F4
            }

            tvDate.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    dateColor
                )
            )

            tvDayOfWeek.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    dayOfWeekColor
                )
            )

            ivEvent.setColorFilter(
                ContextCompat.getColor(
                    root.context,
                    eventColor
                )
            )

            cvDate.setCardBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    backgroundColor
                )
            )
        }
    }
}