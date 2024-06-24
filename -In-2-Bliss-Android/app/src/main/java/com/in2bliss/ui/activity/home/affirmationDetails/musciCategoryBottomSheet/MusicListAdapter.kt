package com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet

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
import com.in2bliss.databinding.ItemMusicSubCategoryBinding
import com.in2bliss.utils.extension.glide

class MusicListAdapter(
    private val requestManager: RequestManager
) :
    ListAdapter<MusicList.Data, MusicListAdapter.ViewHolder>(
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

    var selectedMusic : ((data : MusicList.Data) -> Unit)? = null

    private var selectedPosition = -1
    private var previousPosition = selectedPosition

    class ViewHolder(val view: ItemMusicSubCategoryBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMusicSubCategoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.audioName

            ivMusicSubCategory.glide(
                requestManager = requestManager,
                image = BuildConfig.MUSIC_BASE_URL.plus(model.thumbnail.orEmpty()),
                placeholder = R.drawable.ic_error_place_holder,
                error = R.drawable.ic_error_place_holder
            )

            ivSelected.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (selectedPosition == holder.absoluteAdapterPosition) {
                        R.drawable.ic_checked
                    } else R.drawable.ic_not_checked_
                )
            )

            clContainer.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousPosition)
                previousPosition = selectedPosition

                selectedMusic?.invoke(
                    model
                )
            }
        }
    }
}