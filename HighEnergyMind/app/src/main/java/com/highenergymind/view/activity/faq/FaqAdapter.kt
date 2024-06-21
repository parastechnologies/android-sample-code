package com.highenergymind.view.activity.faq

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.Faq
import com.highenergymind.databinding.ItemsFagsLayoutBinding
import com.highenergymind.`interface`.OnClick
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible

class FaqAdapter(
    var context: Context,
    var dataList: List<Faq>,
    private val onClick: OnClick,
) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    val isCliked = ObservableBoolean(true)

    class ViewHolder(var binding: ItemsFagsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsFagsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    var isSelected = -1

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            binding.apply {

                fagTitleTV.text = dataList[position].question
                fagContentTV.text = dataList[position].answer

                root.setOnClickListener {
                    onClick.showSubcate()
                    isSelected = if (isSelected!=holder.absoluteAdapterPosition) {
                        holder.absoluteAdapterPosition
                    }else{
                        -1
                    }
                    notifyDataSetChanged()
                }
            }

            if (isSelected == position) {
                binding.dropDowenIV.rotation = 180f
                binding.fagContentTV.visible()
                isCliked.set(false)
            } else {
                binding.dropDowenIV.rotation = 0f
                binding.fagContentTV.gone()
                isCliked.set(true)
            }

        }

    }

}