package com.in2bliss.ui.activity.home.searchTODOREMOVE.searchFilter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.R
import com.in2bliss.data.model.CategoryModel
import com.in2bliss.databinding.ItemCategoryBinding
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visible

class SearchFilterSubCategoryAdapter :
    ListAdapter<CategoryModel, SearchFilterSubCategoryAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<CategoryModel>() {

        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.categoryImage == newItem.categoryImage
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }

    }) {

    var subCategorySelected  :((position : Int,selected : Boolean) -> Unit)? = null

    class ViewHolder(val view: ItemCategoryBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            model.categoryName?.let { tvCateName.text = it }

            clContainer.setOnClickListener {
                model.isSelected = model.isSelected.not()
                notifyItemChanged(position)
                subCategorySelected?.invoke(holder.absoluteAdapterPosition,model.isSelected)
            }

            var strokeColor = R.color.grey_FAFAFF
            var iconAndTextColor = R.color.prime_purple_5F46F4
            var backgroundColor = R.color.grey_FAFAFF

            if (model.isSelected) {
                strokeColor = R.color.blue_d9e9fd
                iconAndTextColor = R.color.white
                backgroundColor = R.color.prime_purple_5F46F4
            }

            cvContainer.strokeColor = ContextCompat.getColor(root.context, strokeColor)
            clContainer.setBackgroundColor(ContextCompat.getColor(root.context, backgroundColor))
            tvCateName.setTextColor(ContextCompat.getColor(root.context, iconAndTextColor))
            ivIcon.gone()
            view.visible()
        }
    }
}