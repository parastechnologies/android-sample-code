package com.app.muselink.ui.adapter.chatscreen

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.enum.MessageType
import com.app.muselink.ui.fragments.chatscreen.modals.ModalMessage
import com.app.muselink.util.SyncConstants.BASE_URL_IMAGE
import com.app.muselink.util.SyncConstants.CHAT_IMAGE_BASE_URL
import com.app.muselink.util.loadImage
import soup.neumorphism.NeumorphCardView


class UserChatAdapter(private var activity: Activity, var listChatMessages: List<ModalMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var userId: String? = null

    init {
        userId = SharedPrefs.getUser().id
    }

    fun updateList(listChatMessages: List<ModalMessage>) {
        this.listChatMessages = listChatMessages.reversed()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.list_item_right_chat_view, parent, false)
                MessageRight(view)
            }
            else -> {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.list_item_left_chat_view, parent, false)
                MessageLeft(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listChatMessages[position].sender_id) {
            userId -> 0
            else -> 1
        }
    }

    inner class MessageRight(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageRight: TextView = itemView.findViewById(R.id.tvMessageRight)
        val ncvImage: NeumorphCardView = itemView.findViewById(R.id.ncvImage)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val userProfilePic: ImageView = itemView.findViewById(R.id.userProfilePic)
    }

    inner class MessageLeft(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageLeft: TextView = itemView.findViewById(R.id.tvMessageLeft)
        val ncvImage: NeumorphCardView = itemView.findViewById(R.id.ncvImage)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val userProfilePic: ImageView = itemView.findViewById(R.id.userProfilePic)
    }
    override fun getItemCount(): Int {
        return listChatMessages.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageRight) {
            bindRightMessage(holder, listChatMessages[position])

        } else if (holder is MessageLeft) {
            bindLeftMessage(holder, listChatMessages[position])
        }
    }

    /**
     * View binding Right chat 👉
     * */
    private fun bindRightMessage(holder: MessageRight, model: ModalMessage) {
        if (model.MessageType == MessageType.TEXT.type) {
            holder.tvMessageRight.visibility=View.VISIBLE
            holder.ncvImage.visibility=View.GONE
            holder.tvMessageRight.text = model.message
        } else {
            holder.tvMessageRight.visibility=View.GONE
            holder.ncvImage.visibility=View.VISIBLE
            loadImage(holder.ivImage,CHAT_IMAGE_BASE_URL+model.message)
        }
    }

    /**
     * View binding Left chat 👈
     * */
    private fun bindLeftMessage(holder: MessageLeft, model: ModalMessage) {
        //loadImage(holder.userProfilePic,BASE_URL_IMAGE+model.message)
        if (model.MessageType == MessageType.TEXT.type) {
            holder.tvMessageLeft.text = model.message
            holder.tvMessageLeft.visibility=View.VISIBLE
            holder.ncvImage.visibility=View.GONE
        } else {
            holder.tvMessageLeft.visibility=View.GONE
            holder.ncvImage.visibility=View.VISIBLE
            loadImage(holder.ivImage,CHAT_IMAGE_BASE_URL+model.message)
        }
    }
}