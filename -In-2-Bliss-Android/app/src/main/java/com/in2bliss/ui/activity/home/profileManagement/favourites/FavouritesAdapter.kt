package com.in2bliss.ui.activity.home.profileManagement.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.FavouritesResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.databinding.FavouritesCartBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.getImageUrl
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.inVisible
import com.in2bliss.utils.extension.secondsToMinute
import com.in2bliss.utils.extension.visible

class FavouritesAdapter(private val requestManager: RequestManager) :
    PagingDataAdapter<FavouritesResponse.Data, FavouritesAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<FavouritesResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: FavouritesResponse.Data,
            newItem: FavouritesResponse.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FavouritesResponse.Data,
            newItem: FavouritesResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    var categoryType: AppConstant.HomeCategory? = null
    var type: ApiConstant.ExploreType? = null

    var favourite: ((position: Int, id: Int, isFav: Int) -> Unit)? = null
    var listener: ((position: Int, data: FavouritesResponse.Data?) -> Unit)? = null

    inner class ViewHolder(val binding: FavouritesCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = FavouritesCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            /**
             * set audio name on text view if favourites type and datatype =0
             * */

            when {
                model?.favouriteType == 0 && model.type == 0 -> {
                    tvTitle.gone()
                    tvTime.gone()
                    ivTime.gone()

                    if (!model.category.isNullOrEmpty()) {
                        if (model.category[0].cName != null && model.category[0].cName != null) {
                            tvCategory.text = model.category[0].cName
                            setImageBaseUrl(
                                model.favouriteType,
                                model.category[0].cIcon,
                                holder.binding
                            )
                        }
                    }
                }

                model?.favouriteType == 0 && model.type == 1 -> {
                    tvCategory.inVisible()
                    ivCategory.inVisible()
                    tvTitle.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.text = model.title

                }

                model?.favouriteType == 2 && model.type == 0 -> {
                    ivCategory.inVisible()
                    tvCategory.inVisible()
                    tvTitle.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.text = model.audioName
                }

                model?.favouriteType == 2 && model.type == 1 -> {
                    ivCategory.inVisible()
                    tvCategory.inVisible()
                    tvTitle.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.text = model.audioName
                }
                model?.favouriteType == 1 && model.type == 1 -> {
                    ivCategory.visible()
                    tvCategory.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.visible()
                    tvDescription.visible()
                    tvTitle.text = model.title
                    tvDescription.text = model.description
                    if (model.category.isNullOrEmpty().not()) {
                        if (model.category?.get(0)!!.cName != null && model.category[0].cName != null) {
                            tvCategory.text = model.category[0].cName
                            setImageBaseUrl(
                                model.favouriteType,
                                model.category[0].cIcon,
                                holder.binding
                            )
                        }
                    }
                }

                else -> {
                    ivCategory.visible()
                    tvCategory.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.visible()
                    tvTitle.text = model?.title
                    tvDescription.text = model?.description
                    if (model?.category.isNullOrEmpty().not()) {
                        if (model?.category?.get(0)!!.cName != null && model.category[0].cName != null) {
                            tvCategory.text = model.category[0].cName
                            setImageBaseUrl(
                                model.favouriteType,
                                model.category[0].cIcon,
                                holder.binding
                            )
                        }
                    }
                }
            }

            val thumbNailImage = getImageUrl(
                category = categoryType ?: AppConstant.HomeCategory.TEXT_AFFIRMATION,
                type = type?.value?.toInt() ?: 0,
                image = model?.thumbnail,
                createdBy = model?.createdBy ?: 0,
                background = model?.background ?: ""
            )

            secondsToMinute(
                seconds = model?.duration?.toInt(),
                converted = { minute, second ->
                    tvTime.text = if (minute == 0) {
                        "$second sec"
                    } else "$minute mins"
                }
            )

            sivThumbnail.glide(
                requestManager = requestManager, image = thumbNailImage,
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )

            /** Changing favourite states for only favourites screen **/

            model?.favouriteStatus = 1
            ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (model?.favouriteStatus == 0) R.drawable.ic_red_unlike else R.drawable.ic_red_liked
                )
            )
            tvDescription.text = model?.description
            root.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition, model)
            }
            ivFavourite.setOnClickListener {
                favourite?.invoke(
                    holder.absoluteAdapterPosition,
                    model?.id ?: 0,
                    model?.favouriteStatus ?: 0
                )
            }
        }
    }

    private fun setImageBaseUrl(favType: Int?, cIcon: String?, binding: FavouritesCartBinding) {
        val icon = if (favType == 2) {
            BuildConfig.MUSIC_BASE_URL.plus(cIcon)
        } else BuildConfig.IMAGE_BASE_URL.plus(cIcon)

        binding.ivCategory.glide(
            requestManager = requestManager,
            image = icon,
            error = R.drawable.ic_error_place_holder,
            placeholder = R.drawable.ic_error_place_holder
        )
    }
}