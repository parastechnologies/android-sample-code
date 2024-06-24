package com.in2bliss.ui.activity.home.affirmationDetails.musciCategoryBottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.musicCateogries.MusicCategories
import com.in2bliss.databinding.ItemNestedMusicCategoryBinding
import com.in2bliss.utils.extension.glide

class MusicCategoryAdapter(
    private val requestManager: RequestManager
) :
    ListAdapter<MusicCategories.Data, MusicCategoryAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<MusicCategories.Data>() {
            override fun areItemsTheSame(
                oldItem: MusicCategories.Data,
                newItem: MusicCategories.Data
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MusicCategories.Data,
                newItem: MusicCategories.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var listener: ((id: Int) -> Unit)? = null

    class ViewHolder(val view: ItemNestedMusicCategoryBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNestedMusicCategoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.name

            ivMusicCategory.glide(
                requestManager = requestManager,
                image = BuildConfig.MUSIC_BASE_URL.plus(model.icon.orEmpty()),
                placeholder = R.drawable.ic_error_place_holder,
                error = R.drawable.ic_error_place_holder
            )

            clContainer.setOnClickListener {
                listener?.invoke(model.id ?: 0)
            }
        }
    }
}