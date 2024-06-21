package com.highenergymind.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.SubCategoryData
import com.highenergymind.databinding.ItemsSubcategeroiesLayoutBinding
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible

class SubCategoriesAdapter(
    val subCategoryList: List<SubCategoryData>,
) : RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemsSubcategeroiesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.rlContainer.setOnClickListener {
                val item = subCategoryList[absoluteAdapterPosition]
                item.isChecked = !item.isChecked
                binding.apply {
                    if (item.isChecked) {
                        addIV.visible()
                        val selectedColor = ContextCompat.getColorStateList(
                            root.context,
                            R.color.content_color
                        )
                        categoryNameTV.setTextColor(
                            selectedColor
                        )
                        rlContainer.setBackgroundResource(R.drawable.drawable_checked_gradient_content)

                    } else {
                        addIV.gone()
                        val unSelectedColor = ContextCompat.getColorStateList(
                            root.context,
                            R.color.unselected_text_color
                        )
                        categoryNameTV.setTextColor(
                            unSelectedColor
                        )
                        rlContainer.setBackgroundResource(R.drawable.drawable_un_checked_category_affirmation)
                    }
                }
//                notifyItemChanged(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsSubcategeroiesLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = subCategoryList[position]

        with(holder.binding) {
            categoryNameTV.text = subCategoryList[position].subCategoryName
            categoryIC.glideImage(
                subCategoryList[position].subCategoryImg,
                R.drawable.hem_picture_logo
            )

            if (item.isChecked) {
                addIV.visible()
                val selectedColor = ContextCompat.getColorStateList(
                    root.context,
                    R.color.content_color
                )
                categoryNameTV.setTextColor(
                    selectedColor
                )
                rlContainer.setBackgroundResource(R.drawable.drawable_checked_gradient_content)

            } else {
                addIV.gone()
                val unSelectedColor = ContextCompat.getColorStateList(
                    root.context,
                    R.color.unselected_text_color
                )
                categoryNameTV.setTextColor(
                    unSelectedColor
                )
                rlContainer.setBackgroundResource(R.drawable.drawable_un_checked_category_affirmation)
            }

        }

    }
}
