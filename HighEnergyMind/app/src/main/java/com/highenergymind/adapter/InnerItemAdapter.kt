package com.highenergymind.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.SubCategoryData
import com.highenergymind.databinding.ItemInnerSubcatgeoryBinding

class InnerItemAdapter(
    var subCategoryList: MutableList<SubCategoryData>

) : RecyclerView.Adapter<InnerItemAdapter.ViewHolder>() {
    var deleteCallBack: ((item:SubCategoryData) -> Unit)? = null

    inner class ViewHolder(val binding: ItemInnerSubcatgeoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                ivClose.setOnClickListener {
                    val item = subCategoryList[absoluteAdapterPosition]
                    subCategoryList.removeAt(absoluteAdapterPosition)
                    notifyItemRemoved(absoluteAdapterPosition)
                    deleteCallBack?.invoke(item)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemInnerSubcatgeoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.itemNameTV.text = subCategoryList[position].subCategoryName
        }

    }

}