package com.in2bliss.ui.activity.home.commonAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.ItemMusicDescriptionBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.loadSvg
import com.in2bliss.utils.extension.secondsToMinute
import com.in2bliss.utils.extension.visibility

class MusicDetailAdapter(
    private val isEdit: Boolean = false,
    private val imageLoader: ImageLoader,
    private val types: Int? = null,
    private val imageRequest: ImageRequest.Builder,
    private val requestManager: RequestManager,
    private val categoryType: AppConstant.HomeCategory?,
    private val title: String? = null
) :
    ListAdapter<MusicList.Data.Data, MusicDetailAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<MusicList.Data.Data>() {
            override fun areItemsTheSame(
                oldItem: MusicList.Data.Data,
                newItem: MusicList.Data.Data
            ): Boolean {
                return (oldItem.id == newItem.id) && (oldItem.favouriteStatus == newItem.favouriteStatus)
            }

            override fun areContentsTheSame(
                oldItem: MusicList.Data.Data,
                newItem: MusicList.Data.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var listener: ((data: MusicList.Data.Data, position: Int) -> Unit)? = null
    var editListener: ((position: Int, data: MusicList.Data.Data) -> Unit)? = null
    var favourite: ((position: Int, id: Int, isFav: Boolean) -> Unit)? = null

    class ViewHolder(val view: ItemMusicDescriptionBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMusicDescriptionBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {

            val model = getItem(holder.absoluteAdapterPosition)
            val categoryTypes: AppConstant.HomeCategory?
            var type: Int? = null
            when (categoryType) {
                AppConstant.HomeCategory.SLEEP_AFFIRMATION -> {
                    categoryTypes = AppConstant.HomeCategory.GUIDED_SLEEP
                    type = 0
                }

                AppConstant.HomeCategory.SLEEP_MEDIATION -> {
                    categoryTypes = AppConstant.HomeCategory.GUIDED_SLEEP
                    type = null
                }

                else -> {
                    categoryTypes = categoryType
                }
            }

            val thumbNailImage = getImageUrl(
                category = categoryTypes,
                type = types,
                image = model.thumbnail
            )

            if (model.thumbnail?.takeLast(3) == "svg") {
                musicImage.loadSvg(
                    imageLoader = imageLoader,
                    imageRequest = imageRequest,
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder,
                    url = thumbNailImage
                )
            } else {
                musicImage.glide(
                    requestManager = requestManager,
                    image = thumbNailImage,
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }

            ivCategory.isInvisible = model.categoryIcon.isNullOrEmpty()

            /** Changing the base url if in guided sleep and type is music */
            val icon =
                if (type == ApiConstant.ExploreType.MUSIC.value.toInt() && categoryType == AppConstant.HomeCategory.GUIDED_SLEEP
                ) {
                    BuildConfig.MUSIC_BASE_URL.plus(model.categoryIcon)
                } else BuildConfig.IMAGE_BASE_URL.plus(model.categoryIcon)

            if (model.categoryIcon?.takeLast(3) == "svg") {
                ivCategory.loadSvg(
                    imageLoader = imageLoader,
                    imageRequest = imageRequest,
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder,
                    url = icon
                )
            } else {
                ivCategory.glide(
                    requestManager = requestManager,
                    image = icon,
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }

            secondsToMinute(
                seconds = model.duration?.toInt(),
                converted = { minute, second ->
                    tvTime.text = if (minute == 0) {
                        "$second sec"
                    } else "$minute mins"
                }
            )

            if (title == "Music for sleep" && types == 2) {
                tvTitle.text = model.audioName
            } else if (title == "Recently added" && types == 1) {
                tvTitle.text = model.title
                tvDescription.text = model.description
            } else if (title == "Sleep meditations" && types == 1) {
                tvTitle.text = model.title
                tvDescription.text = model.description
            } else if (types == 1) {
                tvTitle.text = model.audioName
                tvDescription.text = model.description
            } else {
                tvTitle.text = model.title
                tvDescription.text = model.description
            }

            tvCategory.text = model?.categoryName
            edit.visibility(isVisible = isEdit)

            cvContainer.setOnClickListener {

                listener?.invoke(model, holder.absoluteAdapterPosition)
            }

            ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (model.favouriteStatus == 0) R.drawable.ic_red_unlike else R.drawable.ic_red_liked
                )
            )

            ivFavourite.setOnClickListener {
                favourite?.invoke(
                    holder.absoluteAdapterPosition,
                    model.id ?: 0,
                    model.favouriteStatus == 1
                )
            }
            ivEditBg.setOnClickListener {
                editListener?.invoke(
                    holder.absoluteAdapterPosition,
                    model
                )
            }
        }
    }

}