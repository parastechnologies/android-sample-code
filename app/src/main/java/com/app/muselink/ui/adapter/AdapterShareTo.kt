package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.ModalShareTo

class AdapterShareTo (val context: Context, val listShareTo: ArrayList<ModalShareTo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_search_history, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myholder = holder as MyHolder

    }

    override fun getItemCount(): Int {
        return listShareTo.size
    }


}