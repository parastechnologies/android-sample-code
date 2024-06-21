package com.highenergymind.view.sheet.colortheme

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.ThemeDataModel
import com.highenergymind.databinding.ItemsColorTitlesLayoutBinding

class ColorThemeTitleAdapter  (
    val dataList : ArrayList<ThemeDataModel>,
    private val context: Context,

    ) : RecyclerView.Adapter<ColorThemeTitleAdapter.ViewHolder>() {

    var isSelected = -1

    inner class ViewHolder(val binding: ItemsColorTitlesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsColorTitlesLayoutBinding.inflate(
                LayoutInflater.from(context),
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

            binding.titleIV.text = dataList[position].themeTitle


            binding.contaibnerCL.setOnClickListener {

                isSelected = position
                notifyDataSetChanged()

            }

            if (isSelected == position) {
                val checkedBgPlan = ContextCompat.getDrawable(context, R.drawable.color_title_back)

                binding.titleIV.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.contaibnerCL.background = checkedBgPlan
                binding.titleIV.setTypeface(binding.titleIV.typeface, Typeface.BOLD)

            } else {
                val uncheckBG = ContextCompat.getDrawable(context, R.drawable.theme_text_back)

                binding.titleIV.setTextColor(Color.parseColor("#1A6785"))
                binding.contaibnerCL.background = uncheckBG

            }

        }

    }

}