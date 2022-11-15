package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.data.modals.responses.GetRoleData
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class AdapterCategories(val context: Context,val adapterCategoriesNavigator:AdapterCategoriesNavigator) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var listCategories=ArrayList<GetRoleData>()
    fun setData(listCategories: ArrayList<GetRoleData>?){
        this.listCategories.clear()
        this.listCategories.addAll(listCategories!!)
        notifyDataSetChanged()
    }
    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName = itemView.findViewById<TextView>(R.id.tvCategoryName)
        val nmcCardCategory = itemView.findViewById<NeumorphCardView>(R.id.nmcCardCategory)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myholder = holder as MyHolder
        if(listCategories[position].IsSelected){
            myholder.nmcCardCategory.setShapeType(ShapeType.BASIN)
        }else{
            myholder.nmcCardCategory.setShapeType(ShapeType.FLAT)
        }
        myholder.tvCategoryName.text = listCategories[position].Role_Name

        myholder.nmcCardCategory.setOnClickListener {
            adapterCategoriesNavigator.onClickCategory(position)
        }
    }

    interface AdapterCategoriesNavigator{
        fun onClickCategory(position: Int)
    }

}