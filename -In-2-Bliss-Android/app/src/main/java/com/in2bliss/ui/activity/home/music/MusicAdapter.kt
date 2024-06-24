package com.in2bliss.ui.activity.home.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.musicCateogries.MusicList
import com.in2bliss.databinding.ItemMusicListBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.secondsToMinute

class MusicAdapter(val requestManager: RequestManager) :
    ListAdapter<MusicList.Data, MusicAdapter.ViewHolder>(

        object : DiffUtil.ItemCallback<MusicList.Data>() {
            override fun areItemsTheSame(
                oldItem: MusicList.Data,
                newItem: MusicList.Data
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MusicList.Data,
                newItem: MusicList.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var onClick: ((data: MusicList.Data) -> Unit)? = null
    var favourite: ((position: Int, id: Int, isFav: Boolean) -> Unit)? = null

    class ViewHolder(val view: ItemMusicListBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMusicListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model?.audioName
            secondsToMinute(
                seconds = model.duration?.toInt(),
                converted = { minute, second ->
                    tvTime.text = if (minute == 0) {
                        "$second sec"
                    } else "$minute mins"
                }
            )
            model?.thumbnail?.let { image ->
                musicImage.glide(
                    requestManager = requestManager,
                    image = BuildConfig.MUSIC_BASE_URL.plus(image),
                    error = R.drawable.ic_error_place_holder    ,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }
            root.setOnClickListener {
                onClick?.invoke(model)
            }

            ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (model.favouriteStatus == 0) R.drawable.ic_red_unlike else R.drawable.ic_red_liked
                )
            )

            ivFavourite.setOnClickListener {
                favourite?.invoke(
                    holder.absoluteAdapterPosition,
                    model.id ?: 0,
                    model.favouriteStatus == 1
                )
            }
        }
    }
}