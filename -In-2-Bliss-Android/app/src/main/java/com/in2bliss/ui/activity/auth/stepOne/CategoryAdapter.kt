package com.in2bliss.ui.activity.auth.stepOne

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.StepOneResponse
import com.in2bliss.databinding.ItemCategoryBinding
import com.in2bliss.utils.extension.glide

class CategoryAdapter(
    private val requestManager: RequestManager
) :
    ListAdapter<StepOneResponse.Data, CategoryAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<StepOneResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: StepOneResponse.Data,
            newItem: StepOneResponse.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StepOneResponse.Data,
            newItem: StepOneResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }) {

    class ViewHolder(val view: ItemCategoryBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {

            val model = getItem(holder.absoluteAdapterPosition)

            tvCateName.text = model?.reason
            model?.icon?.let { image ->
                ivIcon.glide(
                    requestManager = requestManager,
                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }

            clContainer.setOnClickListener {
                model.isSelected = (model.isSelected ?: false).not()
                notifyItemChanged(holder.absoluteAdapterPosition)
            }

            var strokeColor = R.color.grey_FAFAFF
            var iconAndTextColor = R.color.prime_purple_5F46F4
            var backgroundColor = R.color.grey_FAFAFF

            if (model.isSelected == true) {
                strokeColor = R.color.blue_d9e9fd
                iconAndTextColor = R.color.white
                backgroundColor = R.color.prime_purple_5F46F4
            }

            cvContainer.strokeColor = ContextCompat.getColor(root.context, strokeColor)
            clContainer.setBackgroundColor(ContextCompat.getColor(root.context, backgroundColor))
            //      ivIcon.setColorFilter(ContextCompat.getColor(root.context, iconAndTextColor))
            tvCateName.setTextColor(ContextCompat.getColor(root.context, iconAndTextColor))
        }
    }
}