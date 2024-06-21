package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.BackAudios
import com.highenergymind.databinding.ItemMusicAllBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible


/**
 * Created by Puneet on 15/05/24
 */
class SeeAllMusicAdapter(val sharedPrefs: SharedPrefs) :
    PagingDataAdapter<BackAudios, SeeAllMusicAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<BackAudios>() {
        override fun areItemsTheSame(oldItem: BackAudios, newItem: BackAudios): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BackAudios, newItem: BackAudios): Boolean {
            return oldItem == newItem
        }
    }) {
    var callBack: ((BackAudios, Int, isPremium: Boolean) -> Unit)? = null

    inner class ViewHolder(val binding: ItemMusicAllBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.apply {
                root.setOnClickListener {
                    val item = getItem(absoluteAdapterPosition)
                    if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty() && premiuemIV.visibility == View.VISIBLE) {
                        item?.let { it1 -> callBack?.invoke(it1, absoluteAdapterPosition, true) }
                    } else {
                        item?.let { it1 -> callBack?.invoke(it1, absoluteAdapterPosition, false) }

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMusicAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            val item = getItem(position)
            binding.apply {
                titleIV.text = item?.backgroundTitle
                musicIV.glideImage(item?.backTrackImg)
                binding.apply {
                    if (item?.backTrackType == "premium" && sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty()) {
                        premiuemIV.visible()
                    } else {
                        premiuemIV.gone()
                    }
                }
            }

        }
    }
}