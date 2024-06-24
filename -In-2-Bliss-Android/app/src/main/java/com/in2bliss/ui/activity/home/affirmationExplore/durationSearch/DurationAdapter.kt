package com.in2bliss.ui.activity.home.affirmationExplore.durationSearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.musicList.duration.Duration
import com.in2bliss.databinding.ItemSelectDurationBinding

class DurationAdapter :
    ListAdapter<Duration, DurationAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<Duration>() {

        override fun areItemsTheSame(
            oldItem: Duration,
            newItem: Duration
        ): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(
            oldItem: Duration,
            newItem: Duration
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    private var selectedPosition = -1
    private var previousSelectedPosition = -1
    var selectedDuration: ((start: String, end: String) -> Unit)? = null

    class ViewHolder(val view: ItemSelectDurationBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSelectDurationBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {

            val model = getItem(holder.absoluteAdapterPosition)

            time.text = model?.time

            clContainer.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousSelectedPosition)
                previousSelectedPosition = selectedPosition
                selectedDuration?.invoke(
                    model.startTimeInSeconds, model.endTimeInSeconds
                )
            }

            var strokeColor = R.color.grey_FAFAFF
            var iconAndTextColor = R.color.prime_purple_5F46F4
            var backgroundColor = R.color.grey_FAFAFF

            if (selectedPosition == holder.absoluteAdapterPosition) {
                strokeColor = R.color.blue_d9e9fd
                iconAndTextColor = R.color.white
                backgroundColor = R.color.prime_purple_5F46F4
            }

            cvContainer.strokeColor = ContextCompat.getColor(root.context, strokeColor)
            clContainer.setBackgroundColor(ContextCompat.getColor(root.context, backgroundColor))
            time.setTextColor(ContextCompat.getColor(root.context, iconAndTextColor))
        }
    }
}