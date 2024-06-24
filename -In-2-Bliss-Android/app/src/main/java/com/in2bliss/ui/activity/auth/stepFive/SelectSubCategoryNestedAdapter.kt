package com.in2bliss.ui.activity.auth.stepFive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.SelectedCategory
import com.in2bliss.databinding.ItemSelectedSubCategoryBinding

class SelectSubCategoryNestedAdapter :
    ListAdapter<SelectedCategory, SelectSubCategoryNestedAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<SelectedCategory>() {

            override fun areItemsTheSame(
                oldItem: SelectedCategory,
                newItem: SelectedCategory
            ): Boolean {
                return oldItem.categoryId == newItem.categoryId
            }

            override fun areContentsTheSame(
                oldItem: SelectedCategory,
                newItem: SelectedCategory
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var deleteSubCategoriesListener: ((position: Int) -> Unit)? = null


    class ViewHolder(val view: ItemSelectedSubCategoryBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectedSubCategoryBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvCateName.text = model.subCategoryName
            ivDelete.setOnClickListener {
                deleteSubCategoriesListener?.invoke(holder.absoluteAdapterPosition)
            }
        }
    }
}