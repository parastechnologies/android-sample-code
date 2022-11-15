package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.responses.GetGoalsData
import java.util.*

class AdapterCommonPolygon(
    val context: Context,
    val adapterCommonPolygonNavigator: AdapterCommonPolygonNavigator
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listCategories = ArrayList<GetGoalsData>()

    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryname = itemView.findViewById<TextView>(R.id.tvCategoryname)
        val llCategory = itemView.findViewById<LinearLayout>(R.id.llCategory)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_polygon_shape_category, parent, false)
        return MyHolder(view)
    }
    override fun getItemCount(): Int {
        return listCategories.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myholder = holder as MyHolder
        myholder.llCategory.isSelected = listCategories[position].IsSelected == true
        myholder.llCategory.setOnClickListener {
            adapterCommonPolygonNavigator.onClickCategory(position)
        }
        myholder.tvCategoryname.text = listCategories[position].Goal_Name
    }
    fun setData(listCategories: ArrayList<GetGoalsData>) {
        this.listCategories.clear()
        this.listCategories.addAll(listCategories)
        notifyDataSetChanged()
    }
    interface AdapterCommonPolygonNavigator {
        fun onClickCategory(position: Int)
    }
}