package com.in2bliss.ui.activity.home.profileManagement.download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.R
import com.in2bliss.data.roomDataBase.OfflineAudioEntity
import com.in2bliss.databinding.DownloadCartBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.visibility

class DownloadAdapter(
    private val requestManager: RequestManager
) : ListAdapter<OfflineAudioEntity, DownloadAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<OfflineAudioEntity>() {
        override fun areItemsTheSame(
            oldItem: OfflineAudioEntity,
            newItem: OfflineAudioEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OfflineAudioEntity,
            newItem: OfflineAudioEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((audioFile: OfflineAudioEntity) -> Unit)? = null
    var deleteListener: ((audioFile: OfflineAudioEntity, position: Int) -> Unit)? = null

    class ViewHolder(val view: DownloadCartBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DownloadCartBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.title
            tvTitleDelete.text = model.title

            tvDescription.text = model.description
            tvDescriptionDelete.text = model.description

            sivThumbnail.glide(
                requestManager = requestManager,
                image = (model.musicImageFilePath),
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )
            ivThumbnailDelete.glide(
                requestManager = requestManager,
                image = (model.musicImageFilePath),
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )

            clContainer.visibility(
                isVisible = model.isDelete.not()
            )

            delete.visibility(
                isVisible = model.isDelete
            )

            clContainer.setOnClickListener {
                listener?.invoke(model)
            }

            clDelete.setOnClickListener {
                deleteListener?.invoke(model, holder.absoluteAdapterPosition)
            }
        }
    }
}