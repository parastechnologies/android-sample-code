package com.app.muselink.ui.adapter.chatscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.ui.fragments.chatscreen.modals.RecentChatDetails
import com.app.muselink.util.loadImageUser
import soup.neumorphism.NeumorphCardView

class ChatUnReadMessageAdapter(
    private val context: Context,
    val chatUnReadMessageAdapterNavigator: ChatUnReadMessageAdapterNavigator,
    var listRecentChatDetails: ArrayList<RecentChatDetails>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image2: ImageView = itemView.findViewById(R.id.image2)
        val tvOnlineStatus: TextView = itemView.findViewById(R.id.tvOnlineStatus)
        val rlParent: RelativeLayout = itemView.findViewById(R.id.rlParent)
        val nmpRecentChat: NeumorphCardView = itemView.findViewById(R.id.nmpRecentChat)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val imgUser: ImageView = itemView.findViewById(R.id.imgUser)
    }
    fun updateList(listRecentChatDetails: ArrayList<RecentChatDetails>) {
        this.listRecentChatDetails = listRecentChatDetails
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_chat_unreadmessage, parent, false)
        return MyHolder(view)
    }
    override fun getItemCount(): Int {
        return listRecentChatDetails.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyHolder
        holder.tvUserName.text = listRecentChatDetails[position].senderName.toString()
        holder.imgUser.loadImageUser(listRecentChatDetails[position].sender_profile_picture.toString())
        if(listRecentChatDetails[position].onlineStatus=="1"){
            holder.image2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_circle_green))
            holder.tvOnlineStatus.text = context.getString(R.string.online)
        }else{
            holder.image2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_circle_grey))
            holder.tvOnlineStatus.text = context.getString(R.string.offline)
        }/*else if(position == 2){
            holder.image2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_circle_yellow))
            holder.tvOnlineStatus.text = context.getString(R.string.recent_active)
        }else{
            holder.image2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_circle_green))
            holder.tvOnlineStatus.text = context.getString(R.string.online)
        }*/
        holder.nmpRecentChat.setOnClickListener {
            chatUnReadMessageAdapterNavigator.onClickItem(position)
        }
    }
    interface ChatUnReadMessageAdapterNavigator {
        fun onClickItem(position: Int);
    }
}