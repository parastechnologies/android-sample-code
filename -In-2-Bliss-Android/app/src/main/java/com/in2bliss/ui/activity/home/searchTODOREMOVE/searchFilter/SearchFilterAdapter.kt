package com.in2bliss.ui.activity.home.searchTODOREMOVE.searchFilter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.in2bliss.data.model.SearchFilter
import com.in2bliss.databinding.ItemSearchFilterBinding

class SearchFilterAdapter : ListAdapter<SearchFilter, SearchFilterAdapter.ViewHolder>(

    object : DiffUtil.ItemCallback<SearchFilter>() {
        override fun areItemsTheSame(oldItem: SearchFilter, newItem: SearchFilter): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SearchFilter, newItem: SearchFilter): Boolean {
            return oldItem == newItem
        }

    }
) {

    var selectedCategoryPositionListener: ((position: Int) -> Unit)? = null

    private var selectedCategoryPosition = -1
    private var selectedSubCategoryList: ArrayList<Int> = arrayListOf()

    class ViewHolder(val view: ItemSearchFilterBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchFilterBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            tvCategory.text = getItem(holder.absoluteAdapterPosition).name

            /** Setting the adapter */
            val layoutManger = FlexboxLayoutManager(root.context)
            layoutManger.flexDirection = FlexDirection.ROW
            rvCategory.layoutManager = layoutManger

            val adapter = SearchFilterSubCategoryAdapter()
            rvCategory.adapter = adapter
            adapter.submitList(getItem(holder.absoluteAdapterPosition).dataList)

            adapter.subCategorySelected = { position, selected ->

                currentList[holder.absoluteAdapterPosition].dataList[position].isSelected = selected

                if (selectedCategoryPosition != holder.absoluteAdapterPosition && selectedCategoryPosition != -1) {
                    selectedSubCategoryList.forEach { isSelectedPosition ->
                        currentList[selectedCategoryPosition].dataList[isSelectedPosition].isSelected =
                            false
                    }
                    notifyItemChanged(selectedCategoryPosition)
                }

                if (holder.absoluteAdapterPosition != selectedCategoryPosition) {
                    selectedSubCategoryList.clear()
                }
                selectedCategoryPosition = holder.absoluteAdapterPosition
                selectedSubCategoryList.add(position)
                selectedCategoryPositionListener?.invoke(holder.absoluteAdapterPosition)
            }
        }
    }
}