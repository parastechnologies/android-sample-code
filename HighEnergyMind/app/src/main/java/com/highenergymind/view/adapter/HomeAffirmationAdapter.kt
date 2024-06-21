package com.highenergymind.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.highenergymind.R
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.data.TrackOb
import com.highenergymind.databinding.ItemHomeAffirmationBinding
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.intentComponent
import com.highenergymind.utils.visible
import com.highenergymind.view.activity.trackDetail.TrackDetailActivity

class HomeAffirmationAdapter(val isDescShow: Boolean = false) :
    ListAdapter<TrackOb, HomeAffirmationAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<TrackOb>() {
        override fun areItemsTheSame(oldItem: TrackOb, newItem: TrackOb): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TrackOb, newItem: TrackOb): Boolean {
            return oldItem == newItem
        }
    }) {
    var callBack: ((item: TrackOb, pos: Int, type: Int) -> Unit)? = null

    inner class ViewHolder(val binding: ItemHomeAffirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                ivFav.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    item.isFav = if (item.isFav == 0) 1 else 0
                    notifyItemChanged(absoluteAdapterPosition)
                    callBack?.invoke(item, absoluteAdapterPosition, R.id.ivFav)
                }
            }
            binding.root.setOnClickListener {
                val item = Gson().toJson(getItem(absoluteAdapterPosition))
                it.context.intentComponent(
                    TrackDetailActivity::class.java,
                    Bundle().also { bnd ->
                        bnd.putString(ApiEndPoint.GET_AFFIRMATION_BY_TRACK_ID, item)
                    }
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHomeAffirmationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.binding.apply {
                tvCategory.text = item.categoryName ?: ""
                tvTitle.text = item.trackTitle ?: ""
                if (isDescShow) {
                    tvTrackDesc.text = item.trackDesc
                    tvTrackDesc.visible()
                } else {
                    tvTrackDesc.gone()
                }
                if (item.isFav == 1) {
                    ivFav.setImageResource(R.drawable.ic_fill_heart_bg)
                } else {
                    ivFav.setImageResource(R.drawable.ic_un_fill_heart_bg)
                }
                ivBackground.glideImage(item.trackThumbnail)
                item.totalTrackDuration?.let {
                    tvDuration.text = String.format("%02d", it / 60)
                        .plus(" ${root.context.getString(R.string.mins)}")
                }
            }
        }
    }


}
