package com.app.muselink.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.responses.getInterest.InterestsDatum
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class PostsAdapter internal constructor(
    var context: Context,
    var postItems: ArrayList<InterestsDatum>?,
    var position: Int,
    var param: OnItemSelect):RecyclerView.Adapter<PostsAdapter.ListViewHolder>() {

    var color = R.color.white

    init {
        val pos = position + 1
        color = when {
            pos % 3 == 2 -> {
                R.color.color_purple_100
            }
            pos % 3 == 1 -> {
                R.color.white
            }
            pos % 3 == 0 -> {
                R.color.colorGreen
            }
            else -> {
                R.color.colorGreen
            }
        }
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = inflater.inflate(R.layout.list_item_personal_interest, parent, false)
        return ListViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.setPostImage(postItems!![position])
    }
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryname)
        private val icon: TextView = itemView.findViewById(R.id.icon)
        private val card: NeumorphCardView = itemView.findViewById(R.id.card)
        init {
            val csl = ColorStateList.valueOf(ContextCompat.getColor(context, color))
            card.setStrokeColor(csl)
            card.setOnClickListener {
                param.onSelect(adapterPosition, postItems!![adapterPosition].interestsCategoryId!!)
            }
        }
        fun setPostImage(postItem: InterestsDatum?) {
            tvCategoryName.text = postItem?.interestName
            icon.text = postItem?.interestsIcon
            if (postItem?.isSelected!!) {
                card.setShapeType(ShapeType.BASIN)
            } else {
                card.setShapeType(ShapeType.FLAT)
            }
        }
    }
    override fun getItemCount(): Int {
        return postItems!!.size
    }
    interface OnItemSelect {
        fun onSelect(position: Int, id: String)
    }
}