package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.muselink.R
import com.app.muselink.data.modals.responses.getInterest.GetInterestResponseData
import com.app.muselink.model.responses.getInterest.InterestsDatum

class AdapterInterestSelection(
    val context: Context,
    val listCategories: ArrayList<GetInterestResponseData>,
    val adapterPolygonPressedNavigator: AdapterPolygonPressedNavigator
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val rvInterestList: RecyclerView = itemView.findViewById(R.id.rvInterestList)

        init {
            val mLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
            rvInterestList.layoutManager = mLayoutManager
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_polygon_shape_pressed, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyHolder
        holder.tvName.text = listCategories[position].interestsCategoryName
        holder.rvInterestList.adapter = listCategories[position].interestsData?.let {
            PostsAdapter(context, it, position, object : PostsAdapter.OnItemSelect {
                override fun onSelect(position_: Int, id: String) {
                    if (listCategories[position].interestsData!![position_].isSelected) {
                        listCategories[position].interestsData!![position_].isSelected = false
                        adapterPolygonPressedNavigator.onRemove(listCategories[position].interestsData!![position_].interestId!!)

                    } else {
                        listCategories[position].interestsData!![position_].isSelected = true
                        adapterPolygonPressedNavigator.onAdd(listCategories[position].interestsData!![position_])
                    }
                    holder.rvInterestList.adapter?.notifyDataSetChanged()
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    interface AdapterPolygonPressedNavigator {
        fun onAdd(interestsDatum: InterestsDatum)
        fun onRemove(id: String)
    }
}