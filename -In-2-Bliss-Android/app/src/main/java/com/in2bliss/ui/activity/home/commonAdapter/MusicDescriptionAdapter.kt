package com.in2bliss.ui.activity.home.commonAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.HomeResponse
import com.in2bliss.data.model.musicList.MusicList
import com.in2bliss.databinding.ItemMusicDescriptionBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.inVisible
import com.in2bliss.utils.extension.secondsToMinute
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible

class MusicDescriptionAdapter(
    private val requestManager: RequestManager,
    private val isEdit: Boolean = false
) :
    ListAdapter<HomeResponse.Data.Data, MusicDescriptionAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<HomeResponse.Data.Data>() {
            override fun areItemsTheSame(
                oldItem: HomeResponse.Data.Data,
                newItem: HomeResponse.Data.Data
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: HomeResponse.Data.Data,
                newItem: HomeResponse.Data.Data
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    ) {

    var listener: ((type: String, datatype: String, data: MusicList.Data.Data) -> Unit)? = null
    var editListener: ((position: Int) -> Unit)? = null
    var favourite: ((position: Int, id: Int, isFav: String, type: Int, dataType: Int) -> Unit)? =
        null


    class ViewHolder(val view: ItemMusicDescriptionBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMusicDescriptionBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.title
            tvDescription.text = model.description
            edit.visibility(
                isVisible = isEdit
            )
            when {
                model?.dataType == 0 && model.type == 0 -> {
                    tvTitle.gone()
                    tvTime.gone()
                    ivTime.gone()
                    if (model.category != null) {
                        if (model.category.name != null && model.category.icon != null) {
                            tvCategory.text = model.category.name
                            setImageBaseUrl(
                                model.category.icon,
                                holder.view
                            )
                        }
                    }
                }

                model?.dataType == 0 && model.type == 1 -> {
                    tvCategory.inVisible()
                    ivCategory.inVisible()
                    tvTitle.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTime.text = model.duration.toString()

                }

                model?.dataType == 1 && model.type == 0 -> {
                    ivCategory.visible()
                    tvCategory.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.visible()
                    tvCategory.text = model.categoryName
                    tvCategory.text = model.categoryName
                    setImageBaseUrl(
                        model.categoryIcon,
                        holder.view
                    )
                }

                model?.dataType == 1 && model.type == 1 -> {
                    ivCategory.visible()
                    tvCategory.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.visible()
                    tvCategory.text = model.categoryName
                    setImageBaseUrl(
                        model.categoryIcon,
                        holder.view
                    )
                }
                model?.dataType == 4 && model.type == 0 -> {
                    ivCategory.visible()
                    tvCategory.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.visible()
                    tvCategory.text = model.categoryName
                    setImageBaseUrl(
                        model.categoryIcon,
                        holder.view
                    )
                }

                else -> {
                    ivCategory.visible()
                    tvCategory.visible()
                    tvTime.visible()
                    ivTime.visible()
                    tvTitle.visible()
                    if (model.category != null) {
                        if (model.category.name != null && model.category.icon != null) {
                            tvCategory.text = model.category.name
                            setImageBaseUrl(
                                model.category.icon,
                                holder.view
                            )
                        }
                    }else{
                        tvCategory.text = model.categoryName
                        setImageBaseUrl(
                            model.categoryIcon,
                            holder.view
                        )
                    }
                }
            }

            secondsToMinute(
                seconds = model?.duration?.toInt(),
                converted = { minute, second ->
                    tvTime.text = if (minute == 0) {
                        "$second sec"
                    } else "$minute mins"
                }
            )

            /**
             * check background image check
             * */
            val icon = when (model.dataType) {
                0 -> {
                    BuildConfig.AFFIRMATION_BASE_URL
                }

                1 -> {
                    BuildConfig.MEDITATION_BASE_URL
                }

                else -> {
                    BuildConfig.WISDOM_BASE_URL
                }
            }

            musicImage.glide(
                requestManager = requestManager,
                image = icon.plus(model.thumbnail),
                error = R.drawable.ic_error_place_holder,
                placeholder = R.drawable.ic_error_place_holder
            )



            ivEditBg.setOnClickListener {
                editListener?.invoke(holder.absoluteAdapterPosition)
            }

            ivFavourite.setOnClickListener {
                model?.dataType?.let { it1 ->
                    model.type?.let { it2 ->
                        favourite?.invoke(
                            holder.absoluteAdapterPosition,
                            model.id ?: 0,
                            model.favouriteStatus.toString(), it1, it2
                        )
                    }
                }
                if (model.favouriteStatus == 1) {
                    model.favouriteStatus = 0
                } else {
                    model.favouriteStatus = 1
                }
                notifyItemChanged(position)
            }

            ivFavourite.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (model?.favouriteStatus == 0) R.drawable.ic_red_unlike else R.drawable.ic_red_liked
                )
            )

            cvContainer.setOnClickListener {
                val musicListData = MusicList.Data.Data(
                    audio = model.audio,
                    affirmation = model.affirmation,
                    introAffirmation = model.introAffirmation,
                    customise = model.customise,
                    title = model.title,
                    description = model.description,
                    id = model.id,
                    views = model.views,
                    favouriteStatus = model.favouriteStatus,
                    thumbnail = model.thumbnail,
                    duration = model.duration,
                    audioName = model.audioName,
                    background = model.background
                )
                listener?.invoke(model.dataType.toString(), model.type.toString(), musicListData)
            }
        }

    }

    private fun setImageBaseUrl(cIcon: String?, binding: ItemMusicDescriptionBinding) {

        val icon = BuildConfig.IMAGE_BASE_URL.plus(cIcon)

        binding.ivCategory.glide(
            requestManager = requestManager,
            image = icon,
            error = R.drawable.ic_error_place_holder,
            placeholder = R.drawable.ic_error_place_holder
        )
    }

}