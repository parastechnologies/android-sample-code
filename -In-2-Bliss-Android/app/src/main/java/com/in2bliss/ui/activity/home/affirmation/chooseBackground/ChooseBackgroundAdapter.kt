package com.in2bliss.ui.activity.home.affirmation.chooseBackground

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.ChooseBackgroundResponse
import com.in2bliss.databinding.ItemMusicSubCategoryBinding
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.glide

class ChooseBackgroundAdapter(
    private val requestManager: RequestManager,
    private val categoryType: AppConstant.HomeCategory?
) :
    PagingDataAdapter<ChooseBackgroundResponse.Data, ChooseBackgroundAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<ChooseBackgroundResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: ChooseBackgroundResponse.Data,
                newItem: ChooseBackgroundResponse.Data
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ChooseBackgroundResponse.Data,
                newItem: ChooseBackgroundResponse.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    private var selectedPosition = -1
    private var previousSelected = -1
    var callBack: ((String) -> Unit)? = null

    class ViewHolder(val view: ItemMusicSubCategoryBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMusicSubCategoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

//            tvTitle.text = model.type.toString()

            ivSelected.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (selectedPosition == holder.absoluteAdapterPosition) {
                        R.drawable.ic_checked
                    } else R.drawable.ic_not_checked_
                )
            )

            val image = when (categoryType) {
                AppConstant.HomeCategory.JOURNAL -> {
                    BuildConfig.JOURNAL_BASE_URL.plus(model?.background ?: "")
                }

                else -> {
                    BuildConfig.AFFIRMATION_BASE_URL.plus(model?.background ?: "")
                }
            }

            ivMusicSubCategory.glide(
                requestManager = requestManager,
                image = image,
                error = R.color.black,
                placeholder = R.color.black
            )

            clContainer.setOnClickListener {
                selectedPosition = holder.absoluteAdapterPosition
                notifyItemChanged(selectedPosition)
                notifyItemChanged(previousSelected)
                previousSelected = selectedPosition

                callBack?.invoke(model?.background ?: "")
            }
        }
    }
}