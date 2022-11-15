package com.app.muselink.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.ui.ModalAudioFile
import soup.neumorphism.NeumorphCardView

class Soundfile_Adapter(private val context: Context,val itemClickListener: ItemClickListener,var listSoundFile: ArrayList<ModalAudioFile>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.soundfile_adapter, parent, false)
        return MyHolder(view)
    }

    fun updateList(listSoundFile: ArrayList<ModalAudioFile>){
        this.listSoundFile = listSoundFile
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listSoundFile.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as MyHolder

        myHolder.cardPlayAudio.setOnClickListener {
            itemClickListener.onItemClick(position)
        }

        myHolder.rlOptionMenu.setOnClickListener {
            itemClickListener.onClickMoreInfo(position)
        }

        holder.tvSongName.setText(listSoundFile[position].fullAudio)

//        myHolder.iv_smileyimage?.text = list.level.toString()

    }

    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardPlayAudio = itemView.findViewById<NeumorphCardView>(R.id.cardPlayAudio)
        val rlOptionMenu = itemView.findViewById<RelativeLayout>(R.id.rlOptionMenu)
        val tvSongName = itemView.findViewById<TextView>(R.id.tvSongName)
        val llParentView = itemView.findViewById<LinearLayout>(R.id.llParentView)
        val tvSongSinger = itemView.findViewById<TextView>(R.id.tvSongSinger)
    }


    fun setItemClickListener(itemClickListener: ItemClickListener) {
//        mItemClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onClickMoreInfo(position: Int)
    }
}