package com.in2bliss.ui.activity.home.seeAll

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.SeeAllResponse
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.ItemSeeAllBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.loadSvg
import com.in2bliss.utils.extension.secondsToMinute
import com.in2bliss.utils.extension.visibility

class SeeAllAdapter(
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder,
    private val categoryType: AppConstant.HomeCategory?
) : PagingDataAdapter<SeeAllResponse.Data, SeeAllAdapter.ViewHolder>(
    object :
        DiffUtil.ItemCallback<SeeAllResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: SeeAllResponse.Data,
            newItem: SeeAllResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SeeAllResponse.Data,
            newItem: SeeAllResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    var favourite: ((position: Int, id: Int, isFav: Boolean) -> Unit)? = null
    var listener: ((data: SeeAllResponse.Data, position: Int) -> Unit)? = null
    var type: Int? = null
    var editListener: ((data: SeeAllResponse.Data) -> Unit)? = null

    inner class ViewHolder(val view: ItemSeeAllBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSeeAllBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            tvCategory.text = model?.categoryName

            edit.visibility(
                isVisible = categoryType == AppConstant.HomeCategory.CREATE_AFFIRMATION
            )

            val thumbNailImage = getImageUrl(
                category = categoryType,
                type = type,
                image = model?.thumbnail
            )

            if (model?.image?.takeLast(3) == "svg") {
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

            ivCategory.isInvisible = model?.categoryIcon.isNullOrEmpty()

            val icon = if (type == ApiConstant.ExploreType.MUSIC.value.toInt()) {
                BuildConfig.MUSIC_BASE_URL.plus(model?.categoryIcon)
            } else BuildConfig.IMAGE_BASE_URL.plus(model?.categoryIcon)

            if (model?.categoryIcon?.takeLast(3) == "svg") {
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
                seconds = model?.duration?.toInt(),
                converted = { minute, second ->
                    tvTime.text = if (minute == 0) {
                        "$second sec"
                    } else "$minute mins"
                }
            )


            if (model?.title!=null){
                tvTitle.text = model.title
            }else{
                tvTitle.text = model?.audioName
            }
            tvCategory.text = model?.categoryName
            tvDescription.text = model?.description
            ivEdit.gone()

            cvContainer.setOnClickListener {
                model?.let { data -> listener?.invoke(data, holder.absoluteAdapterPosition) }
            }

            ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (model?.favouriteStatus == 0) R.drawable.ic_red_unlike else R.drawable.ic_red_liked
                )
            )

            ivFavourite.setOnClickListener {
                favourite?.invoke(
                    holder.absoluteAdapterPosition,
                    model?.id ?: 0,
                    model?.favouriteStatus == 1
                )
            }

            ivEditBg.setOnClickListener {
                model?.let { it1 ->
                    editListener?.invoke(
                        it1
                    )
                }
            }
        }
    }
}