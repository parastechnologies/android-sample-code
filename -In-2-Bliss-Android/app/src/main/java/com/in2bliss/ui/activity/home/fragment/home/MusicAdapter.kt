package com.in2bliss.ui.activity.home.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.data.model.HomeResponse
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.databinding.ItemHomeMusicListBinding
import com.in2bliss.ui.activity.home.commonAdapter.MusicDescriptionAdapter
import com.in2bliss.utils.extension.visibility

class MusicAdapter(
    private val isSeeAll: Boolean = false,
    private val requestManager: RequestManager,
) :
    ListAdapter<HomeResponse.Data, MusicAdapter.ViewHolder>(

        object : DiffUtil.ItemCallback<HomeResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: HomeResponse.Data,
                newItem: HomeResponse.Data
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HomeResponse.Data,
                newItem: HomeResponse.Data
            ): Boolean {
                return oldItem == newItem
            }

        }
    ) {

    private var isTitleImage = false
    var listener: ((type: String, dataType: String, data: MusicList.Data.Data) -> Unit)? = null
    var favourite: ((id: Int, isFav: String, type:String,dataType:String) -> Unit)? =
        null

    class ViewHolder(val view: ItemHomeMusicListBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHomeMusicListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            tvTitle.text = getItem(holder.absoluteAdapterPosition).title

            val adapter = MusicDescriptionAdapter(requestManager)
            adapter.submitList(getItem(holder.absoluteAdapterPosition).data)
            rvMusicDescription.adapter = adapter

            adapter.listener = { type, dataType, data ->
                listener?.invoke(type, dataType, data)
            }

            adapter.favourite = { _, id, isFav, type, dataType->
                favourite?.invoke(
                    id,
                    isFav,
                    type.toString(),
                    dataType.toString()
                )
            }

            clSeeAll.visibility(isSeeAll)
            ivTitleBg.visibility((isSeeAll && position == 0) && isTitleImage)
        }
    }
}
