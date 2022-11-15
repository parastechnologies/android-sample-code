package com.app.muselink.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.responses.GetGoalsData
import soup.neumorphism.ShapeType

class AdapterGoalSelection(
    val adapterGoalPressedNavigator: AdapterGoalPressedNavigator
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listCategories = ArrayList<GetGoalsData>()

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGoalName: TextView = itemView.findViewById(R.id.tvGoalName)
        val card: soup.neumorphism.NeumorphCardView = itemView.findViewById(R.id.card)
        val image: TextView = itemView.findViewById(R.id.image)

    }

    fun updateGoalsList(listCategories: ArrayList<GetGoalsData>) {
        this.listCategories.clear()
        this.listCategories.addAll(listCategories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_goal, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyHolder
        holder.tvGoalName.text = listCategories[position].Goal_Name
        holder.image.text = listCategories[position].Goal_Icon
        if (listCategories[position].IsSelected) {
            holder.card.setShapeType(ShapeType.PRESSED)
        } else {
            holder.card.setShapeType(ShapeType.FLAT)
        }
        holder.card.setOnClickListener {
            adapterGoalPressedNavigator.onClickCategory(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    interface AdapterGoalPressedNavigator {
        fun onClickCategory(position: Int)
    }
}