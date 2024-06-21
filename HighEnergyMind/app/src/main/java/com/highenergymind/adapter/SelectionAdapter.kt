package com.highenergymind.adapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.databinding.ItemsSelectAgeBinding

class SelectionAdapter(
    val dataList: List<String>,
    private val context: Context,

    ) : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    var isSelected = -1

    inner class ViewHolder(val binding: ItemsSelectAgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsSelectAgeBinding.inflate(
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

            binding.ageTV.text = dataList[position]

            binding.contaibnerCL.setOnClickListener {
                isSelected = position
                notifyDataSetChanged()
            }
            if (isSelected == position) {
                val selectedColor = ContextCompat.getColorStateList(context, R.color.bg_color_1)
                binding.ageTV.setTextColor(selectedColor)
                binding.contaibnerCL.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
            } else {
                val unSelectedColor = ContextCompat.getColorStateList(context, R.color.content_color)
                binding.ageTV.setTextColor(unSelectedColor)
                binding.contaibnerCL.setBackgroundResource(R.drawable.drawable_un_checked_category_affirmation)
            }

        }

    }

}