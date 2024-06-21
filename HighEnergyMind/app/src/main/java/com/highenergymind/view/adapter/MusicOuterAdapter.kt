package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.api.ApiEndPoint
import com.highenergymind.data.BackAudios
import com.highenergymind.data.MusicData
import com.highenergymind.databinding.ItemOuterMusicBinding
import com.highenergymind.utils.SharedPrefs

class MusicOuterAdapter(val sharedPrefs: SharedPrefs) :
    ListAdapter<MusicData, MusicOuterAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<MusicData>() {
        override fun areItemsTheSame(oldItem: MusicData, newItem: MusicData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MusicData, newItem: MusicData): Boolean {
            return oldItem == newItem
        }
    }) {
    var callBack: ((outerItem: MusicData, outerPos: Int, innerItem: BackAudios?, innerPos: Int?, type: String, isPremium: Boolean) -> Unit)? =
        null

    inner class ViewHolder(val binding: ItemOuterMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {

                tvSeeAll.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    callBack?.invoke(
                        item,
                        absoluteAdapterPosition,
                        null,
                        null,
                        ApiEndPoint.SEE_ALL_MUSIC_LIBRARY, false
                    )

                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOuterMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvName.text = item.backgroundCategoryName
            rvInner.adapter = MusicAdapter(item.backgroundAudios, sharedPrefs).also {
                it.callBack = { innerItem, innerPos, isPremium ->
                    val currentItem = getItem(holder.absoluteAdapterPosition)
                    callBack?.invoke(
                        currentItem,
                        holder.absoluteAdapterPosition,
                        innerItem,
                        innerPos, "go else case", isPremium
                    )
                }
            }
        }
    }
}