package com.app.muselink.ui.fragments.profile.soundfileprofile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.responses.getInterest.InterestsDatum

class AdapterInterests(
    private val context: Context, var listInterestsDetails: ArrayList<InterestsDatum>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInterestName: TextView = itemView.findViewById(R.id.tvInterestName)
        val image: TextView = itemView.findViewById(R.id.image)

    }

    fun updateInterestList(listInterestsDetails: ArrayList<InterestsDatum>) {
        this.listInterestsDetails = listInterestsDetails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_interest, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return listInterestsDetails!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyHolder).tvInterestName.text = listInterestsDetails!![position].interestName
        holder.image.text = listInterestsDetails!![position].interestsIcon

    }

}