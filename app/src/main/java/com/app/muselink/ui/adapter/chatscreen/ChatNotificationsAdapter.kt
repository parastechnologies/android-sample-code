package com.app.muselink.ui.adapter.chatscreen

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.responses.notification.NotificationListData
import com.app.muselink.util.SyncConstants.BASE_URL_IMAGE
import com.app.muselink.util.loadImageUser
import de.hdodenhof.circleimageview.CircleImageView

class ChatNotificationsAdapter(
    private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var list = ArrayList<NotificationListData>()
    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: CircleImageView = itemView.findViewById(R.id.image)
        var message: TextView = itemView.findViewById(R.id.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_chat_notification, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyHolder)
        if (list[position].message == "like") {
            holder.message.text = "You have a new admirer, keep swiping to match"
        } else {
            holder.message.text = "Your have matched with " + list[position].userName.toString()
        }
        holder.image.loadImageUser(BASE_URL_IMAGE + list[position].profileImage.toString())
    }

    fun setData(list: ArrayList<NotificationListData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}