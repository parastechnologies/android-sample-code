package com.in2bliss.ui.activity.home.affirmation.textList

import android.util.Log
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
import com.in2bliss.data.model.TextAffirmation
import com.in2bliss.databinding.ItemTextAffirmationListBinding
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.loadSvg

class TextListAdapter(
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder
) :
    PagingDataAdapter<TextAffirmation, TextListAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<TextAffirmation>() {

        override fun areItemsTheSame(
            oldItem: TextAffirmation,
            newItem: TextAffirmation
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: TextAffirmation,
            newItem: TextAffirmation
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

    var listener: ((TextAffirmation) -> Unit)? = null
    var musicId=""
    var onClickMiniMise: ((TextAffirmation) -> Unit)? = null
    var onClickTextAffirmationList: ((TextAffirmation) -> Unit)? = null
    var onClickShareAffirmation: ((TextAffirmation) -> Unit)? = null
    var onClickFavAffirmation: ((Int, Int) -> Unit)? = null
    var onClickAdd: ((TextAffirmation) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            musicId=model?.id.toString()
            tvAffirmation.text = model?.description
            tvCategory.text = model?.categoryName
            model?.background?.let { image ->
                ivAffirmationBg.glide(
                    requestManager = requestManager,
                    image = BuildConfig.AFFIRMATION_BASE_URL.plus(image),
                    error = R.color.black,
                    placeholder = R.color.black
                )
            }
            val extension = model?.categoryIcon?.let {
                it.substring(
                    it.lastIndexOf(".")
                )
            }

            Log.d("sacsacsacsacsa", "onBindViewHolder: ${BuildConfig.IMAGE_BASE_URL.plus(model?.categoryIcon)}")

            model?.categoryIcon.let { image ->
                if (extension == ".svg") {
                    ivCategory.loadSvg(
                        imageLoader = imageLoader,
                        imageRequest = imageRequest,
                        url = BuildConfig.IMAGE_BASE_URL.plus(model.categoryIcon)
                    )
                } else
                    ivCategory.glide(
                        requestManager = requestManager,
                        image = BuildConfig.IMAGE_BASE_URL.plus(image),
                        error = R.drawable.ic_user_heart,
                        placeholder = R.drawable.ic_user_heart
                    )
            }
            tvDate.text = model?.createdAt?.let {
                formatDate(
                    it,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMMM"
                )
            }

            if (model?.favouriteStatus == 1) {
                ivAffirmationFav.setImageResource(R.drawable.ic_red_fav)
            } else {
                ivAffirmationFav.setImageResource(R.drawable.ic_affirmation_fav)
            }

            ivAffirmationFav.setOnClickListener {
                model?.id?.let { it1 ->
                    model.favouriteStatus?.let { it2 ->
                        onClickFavAffirmation?.invoke(
                            it1,
                            it2
                        )
                    }
                }
                getItem(position)?.favouriteStatus =
                    if (getItem(position)?.favouriteStatus == 1) {
                        0
                    } else {
                        1
                    }
                notifyItemChanged(position)
            }

            ivBack.setOnClickListener {
                model?.let { it1 -> listener?.invoke(it1) }
            }
            ivAffirmationShrink.setOnClickListener {
                model?.let { it1 -> onClickMiniMise?.invoke(it1) }
            }
            ivAffirmationShare.setOnClickListener {
                model?.let { it1 -> onClickShareAffirmation?.invoke(it1) }
            }

            ivMenu.setOnClickListener {
                model?.let { it1 -> onClickTextAffirmationList?.invoke(it1) }
            }
            addMoreIV.setOnClickListener {
                if (model != null) {
                    onClickAdd?.invoke(model)
                }
            }
            ivAffirmationGallery.setOnClickListener {
                if (model != null) {
                    onClickAdd?.invoke(model)
                }
            }
        }
    }
}