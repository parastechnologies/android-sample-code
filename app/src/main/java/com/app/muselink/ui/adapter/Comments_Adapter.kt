package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.data.modals.responses.comment.CommentResponseData
import de.hdodenhof.circleimageview.CircleImageView

class Comments_Adapter(
    private val context: Context,var messageArrayList: ArrayList<CommentResponseData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateListComments(messageArrayList: ArrayList<CommentResponseData>){
        this.messageArrayList = messageArrayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comments_adapter, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return messageArrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as MyHolder
        myHolder.tvComment.setText(messageArrayList[position].comment)
        myHolder.tvUsername.setText(messageArrayList[position].User_Name)
//        myHolder.civ_userimage.loadImageUser(messageArrayList[position].Profile_Image)

        myHolder.rlOptionMenu.setOnClickListener {

        }
    }

    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvComment = itemView.findViewById<TextView>(R.id.tvComment)
        val civ_userimage = itemView.findViewById<CircleImageView>(R.id.civ_userimage)
        val rlOptionMenu = itemView.findViewById<RelativeLayout>(R.id.rlOptionMenu)

    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
//        mItemClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}