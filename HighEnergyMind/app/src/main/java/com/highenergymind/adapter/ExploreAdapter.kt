package com.highenergymind.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.ChoiceData
import com.highenergymind.databinding.ItemsExploreBinding
import com.highenergymind.utils.glideImage


class ExploreAdapter(
    val categoryList: List<ChoiceData>,
) : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemsExploreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                frame.setOnClickListener {
                    val item = categoryList[absoluteAdapterPosition]
                    item.isChecked = !(item.isChecked ?: false)
//                    notifyItemChanged(absoluteAdapterPosition)
                    val selectedColor =
                        ContextCompat.getColorStateList(root.context, R.color.bg_color_1)
                    val unSelectedColor =
                        ContextCompat.getColorStateList(root.context, R.color.content_color)
                    if (categoryList[absoluteAdapterPosition].isChecked == true) {
                        categoryImageIV.glideImage(categoryList[absoluteAdapterPosition].choiceImgPath)
                        categoryNameTV.alpha = 1f
                        categoryNameTV.alpha = 1f
                        categoryNameTV.setTextColor(selectedColor)
                        frame.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
                        categoryImageIV.alpha = 1f
                    } else {
                        categoryImageIV.glideImage(categoryList[absoluteAdapterPosition].choiceImgPath)
                        categoryNameTV.alpha = 0.7f
                        categoryNameTV.setTextColor(unSelectedColor)
                        frame.setBackgroundResource(R.drawable.drawable_un_checked_category)
                        categoryImageIV.alpha = 0.4f
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemsExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding) {
            categoryNameTV.text = categoryList[position].choiceName
            val selectedColor =
                ContextCompat.getColorStateList(root.context, R.color.bg_color_1)
            val unSelectedColor =
                ContextCompat.getColorStateList(root.context, R.color.content_color)
            if (categoryList[position].isChecked == true) {
                categoryImageIV.glideImage(categoryList[position].choiceImgPath, R.drawable.hem_picture_logo)
                categoryNameTV.alpha = 1f
                categoryNameTV.alpha = 1f
                categoryNameTV.setTextColor(selectedColor)
                frame.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
                categoryImageIV.alpha = 1f
            } else {
                categoryImageIV.glideImage(categoryList[position].choiceImgPath, R.drawable.hem_picture_logo)
                categoryNameTV.alpha = 0.7f
                categoryNameTV.setTextColor(unSelectedColor)
                frame.setBackgroundResource(R.drawable.drawable_un_checked_category)
                categoryImageIV.alpha = 0.4f
            }
        }
    }


    override fun getItemCount(): Int {
        return categoryList.size
    }
}
