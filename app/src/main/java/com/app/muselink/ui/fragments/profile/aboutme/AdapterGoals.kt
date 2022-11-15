package com.app.muselink.ui.fragments.profile.aboutme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.responses.GetGoalsData

class AdapterGoals(
    private val context: Context, var listInterestsDetails: ArrayList<GetGoalsData>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGoalName:TextView = itemView.findViewById(R.id.tvInterestName)
        val image :TextView= itemView.findViewById(R.id.image)
    }
    fun updateGoalsList(listInterestsDetails: ArrayList<GetGoalsData>) {
        this.listInterestsDetails = listInterestsDetails
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_interest_, parent, false)
        return MyHolder(view)
    }
    override fun getItemCount(): Int {
        return listInterestsDetails.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyHolder).tvGoalName.text = listInterestsDetails[position].Goal_Name
        holder.image.text = listInterestsDetails[position].Goal_Icon
    }
}