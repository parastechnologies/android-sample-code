package com.in2bliss.ui.activity.home.affirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.databinding.ItemAffirmationListBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.visibility

class AffirmationListPagingAdapter(
    private val requestManager: RequestManager
) : ListAdapter<AffirmationListResponse.Data, AffirmationListPagingAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<AffirmationListResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((position: Int) -> Unit)? = null

    class ViewHolder(val view: ItemAffirmationListBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAffirmationListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            model?.category?.let { image ->
                ivCategory.glide(
                    requestManager = requestManager,
                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
                    error = R.drawable.ic_brain,
                    placeholder = R.drawable.ic_brain
                )
            }

            tvCategory.text = model.category?.name
            tvDescription.text = model.description
            view.visibility(holder.absoluteAdapterPosition != currentList.size - 1)

            ivMenu.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition)
            }
        }
    }
}

