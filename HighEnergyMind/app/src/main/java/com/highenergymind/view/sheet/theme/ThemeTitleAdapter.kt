package com.highenergymind.view.sheet.theme

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.BackgroundImage
import com.highenergymind.data.ThemeData
import com.highenergymind.databinding.ItemsThemeBackgroundLayoutBinding

class ThemeTitleAdapter(
    val dataList: List<ThemeData>,
) : RecyclerView.Adapter<ThemeTitleAdapter.ViewHolder>() {

    var currentSelected = 0
    var preSelected = 0
    var callBack: ((List<BackgroundImage>) -> Unit)? = null

    inner class ViewHolder(val binding: ItemsThemeBackgroundLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsThemeBackgroundLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            binding.titleIV.text = dataList[position].backgroundThemeCategoryName


            binding.contaibnerCL.setOnClickListener {
                callBack?.invoke(dataList[holder.absoluteAdapterPosition].backgroundImages)
                currentSelected = holder.absoluteAdapterPosition
                notifyItemChanged(currentSelected)
                notifyItemChanged(preSelected)
                preSelected = currentSelected
            }

            if (currentSelected == position) {
                binding.titleIV.setTextColor(
                    ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color.white
                    )
                )
                binding.contaibnerCL.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.content_color)
                binding.titleIV.setTypeface(binding.root.context.resources.getFont(R.font.brandontext_bold))
            } else {
                binding.contaibnerCL.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.theme_textcolor)
                binding.titleIV.setTextColor(
                    ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color.bg_color_1
                    )
                )
                binding.titleIV.setTypeface(binding.root.context.resources.getFont(R.font.brandontext_regular))
            }

        }

    }

}