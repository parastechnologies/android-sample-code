package com.in2bliss.ui.activity.home.affirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.databinding.ItemTextAffirmationListBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.loadSvg

class TextAffirmationListAdapter(
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder
) :
    PagingDataAdapter<AffirmationListResponse.Data, TextAffirmationListAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<AffirmationListResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class ViewHolder(val binding: ItemTextAffirmationListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemTextAffirmationListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    var listener: (() -> Unit)? = null
    var onClickMiniMise: ((AffirmationListResponse.Data) -> Unit)? = null
    var onClickTextAffirmationList: ((AffirmationListResponse.Data) -> Unit)? = null
    var onClickShareAffirmation: ((AffirmationListResponse.Data) -> Unit)? = null
    var onClickFavAffirmation: ((AffirmationListResponse.Data) -> Unit)? = null


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvAffirmation.text = model?.description
            tvCategory.text = model?.category?.name
            model?.thumbnail?.let { image ->
                ivAffirmationBg.glide(
                    requestManager = requestManager,
                    image = BuildConfig.AFFIRMATION_BASE_URL.plus(image),
                    error = R.color.black,
                    placeholder = R.color.black
                )
            }
            val extension = model?.category?.icon?.let {
                model.category.icon.substring(
                    it.lastIndexOf(".")
                )
            }

            model?.category?.icon?.let { image ->
                if (extension == ".svg") {
                    ivCategory.loadSvg(
                        imageLoader = imageLoader,
                        imageRequest = imageRequest,
                        url = BuildConfig.IMAGE_BASE_URL.plus(model.category.icon)
                    )
                } else
                    ivCategory.glide(
                        requestManager = requestManager,
                        image = BuildConfig.IMAGE_BASE_URL.plus(image),
                        error = R.color.prime_purple_5F46F4,
                        placeholder = R.color.prime_purple_5F46F4
                    )
            }
            ivBack.setOnClickListener {
                listener?.invoke()
            }
            ivAffirmationShrink.setOnClickListener {
                model?.let { it1 -> onClickMiniMise?.invoke(it1) }
            }
            ivAffirmationShare.setOnClickListener {
                model?.let { it1 -> onClickShareAffirmation?.invoke(it1) }
            }
        }
    }
}