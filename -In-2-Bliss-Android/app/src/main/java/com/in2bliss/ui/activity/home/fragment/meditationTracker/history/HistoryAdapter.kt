package com.in2bliss.ui.activity.home.fragment.meditationTracker.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.MeditationTrackerDateHistoryResponse
import com.in2bliss.databinding.MyHistoryCardBinding
import com.in2bliss.utils.extension.convertMilliseconds
import com.in2bliss.utils.extension.glide

class HistoryAdapter(


    private val requestManager: RequestManager
) :
    ListAdapter<MeditationTrackerDateHistoryResponse.Data, HistoryAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<MeditationTrackerDateHistoryResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: MeditationTrackerDateHistoryResponse.Data,
                newItem: MeditationTrackerDateHistoryResponse.Data
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: MeditationTrackerDateHistoryResponse.Data,
                newItem: MeditationTrackerDateHistoryResponse.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var deleteListener:((id:Int)->Unit)?=null

    class ViewHolder(val view: MyHistoryCardBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MyHistoryCardBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)


            convertMilliseconds(
                timeInMilli = (model.endTime?.times(1000))?.toLong() ?: 0,
                convertedTime = { _, minute, seconds ->
                    val time = if (minute <= 0) "$seconds sec" else "$minute min"
                    tvTimeHistory.text = time
                }
            )
            tvHistoryName.text = model.date.toString()
            tvTitleHistory.text = model.title

            val imageUrl = if (model.createdBy == 1) {
                BuildConfig.MUSIC_BASE_URL.plus(model.image)
            } else {
                BuildConfig.MEDITATION_BASE_URL.plus(model.image)
            }

            sivThumbnail.glide(
                requestManager = requestManager,
                image = imageUrl,
                placeholder = R.drawable.ic_error_place_holder,
                error = R.drawable.ic_error_place_holder
            )
            ivMore.setOnClickListener {
                currentList[position].id?.let { it1 -> deleteListener?.invoke(it1) }
            }
        }
    }
}

