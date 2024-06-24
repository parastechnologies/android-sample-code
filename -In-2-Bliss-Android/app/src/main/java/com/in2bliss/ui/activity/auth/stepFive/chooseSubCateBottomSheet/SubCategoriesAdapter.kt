package com.in2bliss.ui.activity.auth.stepFive.chooseSubCateBottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.databinding.ItemSubCategoriesBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.visibility

class SubCategoriesAdapter(
    private val requestManager: RequestManager
) :
    androidx.recyclerview.widget.ListAdapter<CategoryResponse.Data.SubCategory, SubCategoriesAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<CategoryResponse.Data.SubCategory>() {

            override fun areItemsTheSame(
                oldItem: CategoryResponse.Data.SubCategory,
                newItem: CategoryResponse.Data.SubCategory
            ): Boolean {
                return oldItem.cID == newItem.cID
            }

            override fun areContentsTheSame(
                oldItem: CategoryResponse.Data.SubCategory,
                newItem: CategoryResponse.Data.SubCategory
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    class ViewHolder(val view: ItemSubCategoriesBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubCategoriesBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(position)

            model?.icon?.let { image ->
                ivSubCategoryImage.glide(
                    requestManager = requestManager,
                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }
            //ivSubCategoryImage.setImageResource(R.drawable.ic_brain)

            tvSubCategoryName.text = model.name

            clContainer.setOnClickListener {
                model.isSelected = model.isSelected.not()
                notifyItemChanged(holder.absoluteAdapterPosition)
            }

            ivSelected.visibility(model.isSelected)

            /** Changing the color of selected and unSelected items */
            var strokeColor = R.color.white
            var textColor = R.color.purple_413688

            if (model.isSelected) {
                strokeColor = R.color.prime_purple_5F46F4
                textColor = R.color.prime_purple_5F46F4
            }

            cvContainer.strokeColor = ContextCompat.getColor(
                root.context,
                strokeColor
            )

            tvSubCategoryName.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    textColor
                )
            )
        }
    }
}