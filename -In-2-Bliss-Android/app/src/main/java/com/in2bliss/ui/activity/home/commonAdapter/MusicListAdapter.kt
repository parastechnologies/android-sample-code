package com.in2bliss.ui.activity.home.commonAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.databinding.ItemHomeMusicListBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.visibility


class MusicListAdapter(
    private val isSeeAll: Boolean = false,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder,
    private val requestManager: RequestManager,
    private var categoryType: AppConstant.HomeCategory? = null
) : ListAdapter<MusicList.Data, MusicListAdapter.ViewHolder>(

    object : DiffUtil.ItemCallback<MusicList.Data>() {
        override fun areItemsTheSame(
            oldItem: MusicList.Data,
            newItem: MusicList.Data
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: MusicList.Data,
            newItem: MusicList.Data
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var isGuidedSleep:String?=null

    var exploreCategoryType: AppConstant.HomeCategory? = null

    var isTitleImage = false
    var listener: ((data: MusicList.Data.Data, adapter: MusicDetailAdapter, row: Int, column: Int, type: Int?) -> Unit)? =
        null
    var editListener: ((row: Int, column: Int, data: MusicList.Data.Data, type: Int) -> Unit)? =
        null
    var favourite: ((adapter: MusicDetailAdapter, isFav: Boolean, id: Int, row: Int, column: Int, type: Int?) -> Unit)? =
        null
    var seeAll: ((title: String, id: Int) -> Unit)? = null

    class ViewHolder(val view: ItemHomeMusicListBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHomeMusicListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.title
            clSeeAll.visibility(isSeeAll)
            ivTitleBg.visibility((isSeeAll && position == 0) && isTitleImage)
            val type = model.type ?: model.dataType
            val adapter = MusicDetailAdapter(
                isEdit = categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION,
                imageLoader = imageLoader,
                imageRequest = imageRequest,
                requestManager = requestManager,
                categoryType = categoryType ?: exploreCategoryType,
                types = type,
                title = model.title
            )

            adapter.submitList(model.data)
            rvMusicDescription.adapter = adapter
            (rvMusicDescription.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter.listener = { data, position1 ->
                listener?.invoke(
                    data,
                    adapter,
                    holder.absoluteAdapterPosition,
                    position1,
                    model.type
                )
            }
            adapter.editListener = { position, data ->
                editListener?.invoke(holder.absoluteAdapterPosition, position, data, type ?: 0)
            }
            adapter.favourite = { position, id, isFav ->
                favourite?.invoke(
                    adapter,
                    isFav,
                    id,
                    holder.absoluteAdapterPosition,
                    position,
                    model.type
                )
            }

            clSeeAll.setOnClickListener {
                seeAll?.invoke(
                    model.title ?: "",
                    type ?: 0
                )
            }
        }
    }
}

