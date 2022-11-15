package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R

class SmileyAdapter(private val context: Context, private var smiley_Arraylist: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_smiley, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return smiley_Arraylist.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val list = smiley_Arraylist[position]
        (holder as MyHolder).tvSmiley.text = list
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSmiley: TextView = itemView.findViewById(R.id.tvSmiley)
        init {
           itemView.setOnClickListener {
               listener?.onItemClick(adapterPosition)
           }
        }
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}